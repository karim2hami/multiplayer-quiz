package com.example.jplquiz.members;

import com.example.jplquiz.ServerClientDashboard;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author karimtouhami
 *     <p>ClientHandler: Handles the connection and communication between Server and all Clients.
 */
@SuppressWarnings("DuplicatedCode")
public class ClientHandler implements Runnable {

  protected static final List<ClientHandler> clientHandlers = new ArrayList<>();
  private Socket socket;
  private BufferedReader bufferedReader;
  private BufferedWriter bufferedWriter;
  private String clientUsername;

  private boolean isStart;
  private ServerClientDashboard serverClientDashboard;

  /**
   * @param socket <Client Handler that is responsible for communication with Client and Server
   *     <OutputStream is wrapped with BufferWriter for sending characters and not bytes same is
   *     <for InputStream
   */
  public ClientHandler(Socket socket) {
    try {
      this.socket = socket;
      this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.clientUsername = bufferedReader.readLine();
      clientHandlers.add(this);
      broadcastMessage("Server: " + clientUsername + " has entered the game!");
    } catch (IOException e) {
      closeEverything(socket, bufferedReader, bufferedWriter);
    }
  }

  /**
   * @author karimtouhami
   *     <p>Listens for messages from the Client.
   */
  @Override
  public void run() {
    String messageFromClient;

    while (socket.isConnected()) {
      try {
        messageFromClient = bufferedReader.readLine();
        if (!isStart) {
          serverClientDashboard.addName(messageFromClient);
        }
        broadcastMessage(messageFromClient);
      } catch (IOException e) {
        closeEverything(socket, bufferedReader, bufferedWriter);
        break;
      }
    }
  }

  /**
   * @author karimtouhami
   *     <p>Sends the game rankings to all clients at the same time.
   * @param messageToSend - message that is sent to the client
   */
  // Send a message to all clients at the same time
  public void broadcastMessage(String messageToSend) {

    System.out.println("message to send" + messageToSend + "\n");
    for (ClientHandler clientHandler : clientHandlers) {
      try {

        if (messageToSend.equals("true")) {
          clientHandler.setStart(true);
        }
        clientHandler.bufferedWriter.write(messageToSend);
        // clients wait for the new line
        clientHandler.bufferedWriter.newLine();
        clientHandler.bufferedWriter.flush();

      } catch (IOException e) {
        closeEverything(socket, bufferedReader, bufferedWriter);
      }
    }
  }

  /**
   * @author karimtouhami
   *     <p>Removes a client from the ClientHandler List.
   */
  public void removeClientHandler() {
    clientHandlers.remove(this);
    broadcastMessage("Server : " + clientUsername + " has left the game!");
  }

  /**
   * @author karimtouhami
   *     <p>Closes all running sockets, connections, readers and writers.
   * @param socket - socket for connection to Server
   * @param bufferedReader - bufferedReader to read messages from stream
   * @param bufferedWriter - bufferedWriter to write messages to stream
   */
  public void closeEverything(
      Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
    removeClientHandler();
    try {
      if (bufferedReader != null) {
        bufferedReader.close();
      }
      if (bufferedWriter != null) {
        bufferedWriter.close();
      }
      if (socket != null) {
        socket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setServerClientDashboard(ServerClientDashboard serverClientDashboard) {
    this.serverClientDashboard = serverClientDashboard;
  }

  public void setStart(boolean start) {
    isStart = start;
  }
}

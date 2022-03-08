package members;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

  private Socket socket;
  private BufferedReader bufferedReader;
  private BufferedWriter bufferedWriter;
  private String userName;

  public Client(Socket socket, String userName) {
    try {
      this.socket = socket;
      this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.userName = userName;
    } catch (IOException e) {
      closeEverything(socket, bufferedReader, bufferedWriter);
    }
  }

  public void sendMessage() {
    try {
      bufferedWriter.write(userName);
      bufferedWriter.newLine();
      bufferedWriter.flush();

      Scanner scan = new Scanner(System.in);
      while (socket.isConnected()) {
        String messageToSend = scan.nextLine();
        bufferedWriter.write(userName + ": " + messageToSend);
        bufferedWriter.newLine();
        bufferedWriter.flush();
      }
    } catch (IOException e) {
      closeEverything(socket, bufferedReader, bufferedWriter);
    }
  }

  /** listenForMessage listens to messages that are broadcasted from the ClientHandler */
  public void listenForMessage() {
    new Thread(
            new Runnable() {
              @Override
              public void run() {
                String msgFromGroupChat;

                while (socket.isConnected()) {
                  try {
                    msgFromGroupChat = bufferedReader.readLine();
                    System.out.println(msgFromGroupChat);
                  } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                  }
                }
              }
            })
        .start();
  }

  public void closeEverything(
      Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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

  public static void main(String[] args) throws IOException {

    Scanner scan = new Scanner(System.in);
    System.out.println("Enter your username for the game: ");
    String username = scan.nextLine();
    Socket socket = new Socket("localhost", 1234);
    Client client = new Client(socket, username);
    client.listenForMessage();
    client.sendMessage();
  }
}
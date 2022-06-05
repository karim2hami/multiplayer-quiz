package members;

import com.example.jplquiz.controller.ClientQuestionView;
import com.example.jplquiz.models.QuestionModel;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

/**
 * @author karimtouhami
 *     <p>Client: Class handling the connection between server and client, and transfers the
 *     received questions from the server to the ClientQuestionView GUI.
 */
@SuppressWarnings("DuplicatedCode")
public class Client {

  private Socket socket;
  private BufferedReader bufferedReader;
  private BufferedWriter bufferedWriter;
  private String userName;
  private List<QuestionModel> questionModelList;
  private ClientQuestionView clientQuestionView;

  /**
   * @author karimtouhami
   *     <p>Constructes a new Client instance with a Socket and userName.
   * @param socket - connection Socket running on port "localhost:1234".
   * @param userName - userName of the client.
   */
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
      if (socket.isConnected()) {
        String messageToSend = "hallo";
        bufferedWriter.write(userName + ": " + messageToSend);
        bufferedWriter.newLine();
        bufferedWriter.flush();
      }
    } catch (IOException e) {
      closeEverything(socket, bufferedReader, bufferedWriter);
    }
  }

  /**
   * @author devinhasler
   *     <p>Listens for messages that are broadcasted from the ClientHandler.
   */
  public void listenForQuestions() {
    new Thread(
            () -> {
              while (questionModelList == null) {
                try {
                  InputStream inputStream = socket.getInputStream();
                  ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                  readObjectForQuestion(objectInputStream);
                } catch (IOException e) {
                  closeEverything(socket, bufferedReader, bufferedWriter);
                }
              }
            })
        .start();
  }

  /**
   * @author karimtouhami
   *     <p>Reads the questions from the objectinputstream and converts them into a List of
   *     Questionmodels.
   * @param objectInputStream - Stream of received question objects.
   */
  public void readObjectForQuestion(ObjectInputStream objectInputStream) {
    try {
      this.questionModelList = (List<QuestionModel>) objectInputStream.readObject();
      System.out.println(questionModelList);
    } catch (ClassNotFoundException | IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * @author devinhasler
   *     <p>Transfers the list of questionmodels to the ClientQuestionView GUI, which are then
   *     stored as a variable.
   */
  public void transferQuestions() {
    System.out.println("ArrayList client " + questionModelList);
    clientQuestionView.setSocket(socket);
    clientQuestionView.setQuestionModels(questionModelList);
    if (questionModelList != null) {
      clientQuestionView.loadQuestionFromList();
    }
  }

  /**
   * Handles the open and running connection ports and closes them all at once, when not needed
   * anymore.
   *
   * @param socket - Socket object to be closed.
   * @param bufferedReader - BufferedReader object to be closed.
   * @param bufferedWriter - BufferedWriter object to be closed.
   */
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

  public void setClientQuestionView(ClientQuestionView clientQuestionView) {
    this.clientQuestionView = clientQuestionView;
  }

  public void setQuestionModelList(List<QuestionModel> questionModelList) {
    this.questionModelList = questionModelList;
  }
}

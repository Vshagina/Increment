package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Приложение ClickerApp, реализующее окна сервера и клиента для отслеживания нажатий кнопки
 */
public class ClickerApp extends Application {

    // элементы интерфейса
    private TextField serverTextField;
    private TextField clientTextField;
    private int counter = 0; // счетчик отслеживания нажатий
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter clientOut;
    private BufferedReader clientIn;

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    /**
     * Инициализация и запуск графического интерфейса.
     *
     * @param primaryStage главное окно приложения.
     */
    public void start(Stage primaryStage) {
        // интерфейс сервера
        VBox serverLayout = new VBox(10);
        serverTextField = new TextField("0");
        serverTextField.setEditable(false);
        serverTextField.setPrefSize(400, 40);
        Button serverButton = new Button("Click");
        serverButton.setOnAction(e -> {
            counter++;
            updateText(serverTextField, counter);
            sendMessageToClient(counter);
        });
        serverButton.setPrefSize(100, 40);
        serverLayout.getChildren().addAll(serverTextField, serverButton);

        // интерфейс клиента
        VBox clientLayout = new VBox(10);
        clientTextField = new TextField("0");
        clientTextField.setEditable(false);
        clientTextField.setPrefSize(400, 40);
        Button clientButton = new Button("Click");
        clientButton.setOnAction(e -> {
            counter++;
            updateText(clientTextField, counter);
            sendMessageToServer(counter);
        });
        clientButton.setPrefSize(100, 40);
        clientLayout.getChildren().addAll(clientTextField, clientButton);

        // размеры окна сервера
        Scene serverScene = new Scene(serverLayout, 200, 120);
        primaryStage.setScene(serverScene);
        primaryStage.setTitle("Server");

        // размеры окна клиента
        Stage clientStage = new Stage();
        Scene clientScene = new Scene(clientLayout, 200, 120);
        clientStage.setScene(clientScene);
        clientStage.setTitle("Client");

        // запускается сервер в отдельном потоке
        new Thread(() -> {
            startServer();
        }).start();

        // запуск клиента
        startClient();

        primaryStage.show();
        clientStage.show();
    }

    /**
     * метод для запуска сервера
     */
    private void startServer() {
        try {
            serverSocket = new ServerSocket(8080);
            clientSocket = serverSocket.accept();
            clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
            clientIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // обрабатываются входящие сообщений от клиента
            new Thread(() -> {
                try {
                    String inputLine;
                    while ((inputLine = clientIn.readLine()) != null) {
                        int receivedCounter = Integer.parseInt(inputLine);
                        Platform.runLater(() -> {
                            updateText(serverTextField, receivedCounter);
                            updateText(clientTextField, receivedCounter);
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * метод для запуска клиента
     */
    private void startClient() {
        try {
            Socket serverSocket = new Socket("localhost", 8080);
            PrintWriter serverOut = new PrintWriter(serverSocket.getOutputStream(), true);
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

            // обрабатываются входящие сообщения от сервера
            new Thread(() -> {
                try {
                    String inputLine;
                    while ((inputLine = serverIn.readLine()) != null) {
                        int receivedCounter = Integer.parseInt(inputLine);
                        Platform.runLater(() -> {
                            updateText(serverTextField, receivedCounter);
                            updateText(clientTextField, receivedCounter);
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * метод для отправки сообщения клиенту
     * @param counter
     */
    private void sendMessageToClient(int counter) {
        if (clientOut != null) {
            clientOut.println(counter);
        }
    }

    /**
     * метод для отправки сообщения серверу
     * @param counter
     */
    private void sendMessageToServer(int counter) {
        if (serverSocket != null && clientSocket != null) {
            try {
                PrintWriter serverOut = new PrintWriter(clientSocket.getOutputStream(), true);
                serverOut.println(counter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * метод для обновления числа в полях
     * @param textField
     * @param counter
     */
    private void updateText(TextField textField, int counter) {
        textField.setText(String.valueOf(counter));
    }
}
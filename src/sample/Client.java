package sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    public void sendToServer(int value) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {

            outputStream.write(value);
            outputStream.flush();
            int response = inputStream.read();
            System.out.println("Получен ответ от сервера: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

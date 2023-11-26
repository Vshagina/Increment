package sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Сервер ждет клиента...");

        while (true) {
            try (Socket clientSocket = serverSocket.accept();
                 InputStream inputStream = clientSocket.getInputStream();
                 OutputStream outputStream = clientSocket.getOutputStream()) {

                System.out.println("Новое соединение: " + clientSocket.getInetAddress().toString());

                int request;
                int sum = 0;

                while ((request = inputStream.read()) != -1) {
                    System.out.println("Прислал клиент: " + request);
                    sum += request;
                    System.out.println("Текущая сумма на сервере: " + sum);
                    outputStream.write(sum);
                    System.out.println("Отправлено клиенту: " + sum);
                    outputStream.flush();
                }
            }
        }
    }
}
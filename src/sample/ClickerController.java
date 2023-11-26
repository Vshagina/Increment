package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ClickerController {

    @FXML
    private Button button;

    @FXML
    private TextField text;

    private int currentValue = 0;

    private final Client client = new Client();

    @FXML
    private void initialize() {
        text.setText("0");

        button.setOnAction(e -> {
            currentValue++;
            text.setText(Integer.toString(currentValue));
            client.sendToServer(currentValue);
        });
    }
}
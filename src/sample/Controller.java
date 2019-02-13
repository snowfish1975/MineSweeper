package sample;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.awt.*;

public class Controller {

    @FXML
    private Button btn;

    @FXML
    private void restartGame(ActionEvent e){
        Main.fillGameField();
        Main.restoreGameFieldView();
    }

    public Controller(){
    }

    @FXML
    private void initialize(){

    }
}

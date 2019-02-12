package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class Main extends Application {

    String[] colors = {"GREY","BROWN", "GREEN", "BLUE", "YELLOW", "PURPLE", "MAGENTA", "CYAN", "BLACK", "BLACK","RED"};

    private static Label minesCleared;
    private static Label minesStill;

    private static final int fieldSize = 10;
    private static int mineCount = 15;
    private static int[][] gameFieldValues = new int[fieldSize][fieldSize];

    @Override
    public void start(Stage primaryStage) throws Exception{

        fillGameField(); // fills game arrays with random "mines" and count numbers

        // generates main game window
        SplitPane root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Mine Sweeper");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        minesStill = (Label) scene.lookup("#minesstill"); // mines left counter label
        minesStill.setText(""+mineCount);

        GridPane gameField = (GridPane) scene.lookup("#GameField");

        Label[][] labels = new Label[fieldSize][fieldSize];     // array of labels-digits on the game field
        Button[][] buttons = new Button[fieldSize][fieldSize];  // array of buttons covering cells and waiting for clicks

        int fieldSize = 10;
        String s;
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {

                labels[i][j] = new Label("");
                switch (gameFieldValues[i][j]){
                    case 0: { s=""; break; }
                    case 10: { s="*"; labels[i][j].setFont(Font.font("System", FontWeight.BOLD, 40)); break; }
                    default: s=""+gameFieldValues[i][j];
                }
                labels[i][j].setText(s);
                labels[i][j].setPrefWidth(40);
                labels[i][j].setPrefHeight(40);
                labels[i][j].setAlignment(Pos.CENTER);
                labels[i][j].setTextFill(Paint.valueOf(colors[gameFieldValues[i][j]]));
                labels[i][j].setFont(Font.font("System", FontWeight.BOLD, 24));
                gameField.add(labels[i][j],i,j);

                buttons[i][j] = new Button("");
                buttons[i][j].setPrefHeight(40);
                buttons[i][j].setPrefWidth(40);
                buttons[i][j].setMinHeight(40);
                buttons[i][j].setMinWidth(40);
                buttons[i][j].setAlignment(Pos.CENTER);
                buttons[i][j].setId("X"+i+"Y"+j+"STYLE"+buttons[i][j].getStyle());

                buttons[i][j].setOnMouseClicked(e -> {
                    Button eventButton = (Button) e.getSource();
                    switch (e.getButton()){
                        case PRIMARY: {
                            if (eventButton.getText().equals("X")) break;
                            eventButton.setVisible(false);
                            eventButton.setText(eventButton.getId());
                            int x = Integer.parseInt(eventButton.getId().substring(eventButton.getId().indexOf("X")+1, eventButton.getId().indexOf("Y")));
                            int y = Integer.parseInt(eventButton.getId().substring(eventButton.getId().indexOf("Y")+1, eventButton.getId().indexOf("STYLE")));
                            break;
                        }
                        case SECONDARY: {
                            if (eventButton.getText().equals("X")){
                                eventButton.setText("");
                                eventButton.setStyle(eventButton.getId().substring(eventButton.getId().indexOf("STYLE")+5));
                                mineCount++;
                                minesStill.setText(""+mineCount);
                            }
                            else {
                                eventButton.setText("X");
                                eventButton.setFont(Font.font("System", FontWeight.BOLD, 24));
                                eventButton.setTextFill(Paint.valueOf("YELLOW"));
                                eventButton.setStyle("-fx-background-color: #222222");
                                mineCount--;
                                minesStill.setText("" + mineCount);
                            }
                            break;
                        }
                    }
                });
                gameField.add(buttons[i][j],i,j);
            }
        }
    }

    private void fillGameField() {
        int mineX, mineY;
        for (int i = 0; i < mineCount; i++) {
            do {
                mineX = (int) (Math.random() * fieldSize);
                mineY = (int) (Math.random() * fieldSize);
            } while (gameFieldValues[mineX][mineY] == 10);
            gameFieldValues[mineX][mineY] = 10;
        }
        for (int i = 0; i < fieldSize; i++)
            for (int j = 0; j < fieldSize; j++)
                if (gameFieldValues[i][j]<10)
                    for (int k = -1; k < 2; k++)
                        for (int l = -1; l < 2; l++)
                            if((i+k)>=0  && (i+k)<fieldSize && (j+l)>=0 && (j+l)<fieldSize && (gameFieldValues[i+k][j+l]==10))
                                gameFieldValues[i][j]++;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

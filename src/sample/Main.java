package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.Arrays;

public class Main extends Application {

    String[] colors = {"GREY","BROWN", "GREEN", "BLUE", "YELLOW", "PURPLE", "MAGENTA", "CYAN", "BLACK", "BLACK","RED"};

    private static final int fieldSize = 10;
    private static final int mineCount = 15;
    private static int[][] gameFieldValues = new int[fieldSize][fieldSize];

    @Override
    public void start(Stage primaryStage) throws Exception{

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

        SplitPane root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Mine Sweeper");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        GridPane gameField = (GridPane) scene.lookup("#GameField");
        Label[][] labels = new Label[10][10];
        int fieldSize = 10;
        String s;
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                switch (gameFieldValues[i][j]){
                    case 0: { s=""; break; }
                    case 10: { s="*"; break; }
                    default: s=""+gameFieldValues[i][j];
                }
                labels[i][j] = new Label(s);
                labels[i][j].setPrefWidth(40);
                labels[i][j].setPrefHeight(40);
                labels[i][j].setAlignment(Pos.CENTER);
                labels[i][j].setTextFill(Paint.valueOf(colors[gameFieldValues[i][j]]));
                labels[i][j].setFont(Font.font("System", FontWeight.BOLD, 24));
                gameField.add(labels[i][j],i,j);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

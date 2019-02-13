package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Main extends Application {

    private static String[] colors = {"GREY","BROWN", "GREEN", "BLUE", "YELLOW", "PURPLE", "MAGENTA", "CYAN", "BLACK", "BLACK","RED"};

    private static String s;

    private static Label minesCleared;
    private static Label minesStill;

    private static final int fieldSize = 10;
    private static final int cellSize = 40;
    private static int mineCount = 15;
    private static int[][] gameFieldValues = new int[fieldSize][fieldSize];

    private static Label[][] labels = new Label[fieldSize][fieldSize];     // array of labels-digits on the game field
    private static Button[][] buttons = new Button[fieldSize][fieldSize];  // array of buttons covering cells and waiting for clicks

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
        Image image = new Image(getClass().getResourceAsStream("flag.png"));

        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {

                labels[i][j] = new Label("");
                defineLabel(i,j);
                gameField.add(labels[i][j],i,j);

                buttons[i][j] = new Button("");
                defineButton(i,j);

//              Добавление обработчика нажатий кнопок мыши для каждой кнопки на игровом поле
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
                                eventButton.setGraphic(new ImageView(image));
                                eventButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
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

    static void fillGameField() {
        int mineX, mineY;

        // Обнуление массива значений игрового поля
        for (int i = 0; i < fieldSize; i++)
            for (int j = 0; j < fieldSize; j++) {
                gameFieldValues[i][j]=0;
            }

        // Расстановка мин по полю
        for (int i = 0; i < mineCount; i++) {
            do {
                mineX = (int) (Math.random() * fieldSize);
                mineY = (int) (Math.random() * fieldSize);
            } while (gameFieldValues[mineX][mineY] == 10);
            gameFieldValues[mineX][mineY] = 10;
        }

        // Подсчет значения каждой пустой клетки = количество мин вокруг нее
        for (int i = 0; i < fieldSize; i++)
            for (int j = 0; j < fieldSize; j++)
                if (gameFieldValues[i][j]<10)
                    for (int k = -1; k < 2; k++)
                        for (int l = -1; l < 2; l++)
                            if ((i + k) >= 0 && (i + k) < fieldSize && (j + l) >= 0 && (j + l) < fieldSize && (gameFieldValues[i + k][j + l] == 10))
                                gameFieldValues[i][j]++;
    }

    static void restoreGameFieldView(){
        for (int i = 0; i < fieldSize; i++)
            for (int j = 0; j < fieldSize; j++) {
                defineLabel(i,j);
                defineButton(i,j);
            }
    }

    private static void defineLabel(int i, int j){
        String s;
        switch (gameFieldValues[i][j]){
            case 0: { s=""; break; }
            case 10: { s="@"; labels[i][j].setStyle("-fx-background-color: #ffbbbb"); break; }
            default: s=""+gameFieldValues[i][j];
        }
        labels[i][j].setText(s);
        labels[i][j].setTextFill(Paint.valueOf(colors[gameFieldValues[i][j]]));
        labels[i][j].setPrefWidth(cellSize);
        labels[i][j].setMinWidth(cellSize);
        labels[i][j].setPrefHeight(cellSize);
        labels[i][j].setMinHeight(cellSize);
        labels[i][j].setAlignment(Pos.CENTER);
        labels[i][j].setFont(Font.font("System", FontWeight.BOLD, 24));
    }

    private static void defineButton(int i, int j){
        buttons[i][j].setText("");
        buttons[i][j].setPrefHeight(cellSize);
        buttons[i][j].setPrefWidth(cellSize);
        buttons[i][j].setMinHeight(cellSize);
        buttons[i][j].setMinWidth(cellSize);
        buttons[i][j].setAlignment(Pos.CENTER);
        buttons[i][j].setStyle("-fx-base: #aaaaaa");
        buttons[i][j].setId("X"+i+"Y"+j+"STYLE"+buttons[i][j].getStyle());
        buttons[i][j].setVisible(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

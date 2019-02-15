package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;

public class Main extends Application {

    private static String[] colors = {"GREY", "BROWN", "GREEN", "BLUE", "YELLOW", "PURPLE", "MAGENTA", "CYAN", "BLACK", "BLACK", "RED"};
    private static Label minesStill;
    private static Label timeElapsed;

    private static final int fieldSize = 10;  // размер поля в ширину и в высоту
    private static final int cellSize = 40;   // размер каждой клетки в пикселях
    public static int mineCount = 15;   // оставшееся количество мин на поле
    public static int spareCount = fieldSize * fieldSize - mineCount; // оставшееся количество свободных клеток на поле
    public static Scene scene;  // основная сцена игры, на которой будут располагаться все компоненты интерфейса
    private static int[][] gameFieldValues = new int[fieldSize][fieldSize]; // массив значений клеток поля. 10 - мина, остальное - количество рядом стоящих мин
    private static Label[][] labels = new Label[fieldSize][fieldSize];     // массив текстовых меток для обозначения количества мин рядом
    private static Button[][] buttons = new Button[fieldSize][fieldSize];  // массив кнопок, закрывающих текстовые метки
    private static int[][] emptySpaceMask = new int[fieldSize][fieldSize]; // временный массив для расчта свободной площади игрового поля
    private static int timeCount = 0; // счетчик времени, прошедшего с начала игры
    private static int bestTimeCount = -1; // переменная для хранения лучшего времени

    private static Timeline timeline;

    private static Image flagImage;
    private static Image noMineImage;
    private static ImageView emptyImage;
    private static Image rightMineImage;

    @Override
    public void start(Stage primaryStage) throws Exception {

        flagImage = new Image(getClass().getResourceAsStream("flag.png"));          // создание объекта "Картинка" и загрузка в него изображения из png-файла
        noMineImage = new Image(getClass().getResourceAsStream("nomine.png"));      // создание объекта "Картинка" и загрузка в него изображения из png-файла
        rightMineImage = new Image(getClass().getResourceAsStream("yesmine.png"));  // создание объекта "Картинка" и загрузка в него изображения из png-файла
        emptyImage = new ImageView();   // создание пустой картинки для кнопки, чтобы убрать установленные пометки

        SplitPane root = FXMLLoader.load(getClass().getResource("sample.fxml"));    // загрузка из FXML-файла созданного окна со статичным интерфейсом игры
        scene = new Scene(root);   // создание основной сцены игры на основе загруженного интерфейса

        fillGameField(); // заполняет массив значений метками "мин" или количеством рядом стоящих

        // создаем основное игровое окно и устанавливаем сцену
        primaryStage.setTitle("Mine Sweeper v.1");
        primaryStage.setScene(scene);
        primaryStage.show();  // если .show сделать в конце, не работает поиск по id (scene.lookup(String #id) )

        minesStill = (Label) scene.lookup("#minesstill"); // текстовая строка, отображающая количество оставшихся мин
        minesStill.setText("" + mineCount);

        GridPane gameField = (GridPane) scene.lookup("#GameField");  // табличный вид, который будем заполнять данными игрового поля

        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {

                labels[i][j] = new Label("");  // в очередной клетке создаем новый текстовый ярлык
                defineLabel(i, j);                   // определяем все свойства этого ярлыка и присваиваем значение (мина или их количество рядом)
                gameField.add(labels[i][j], i, j);    // добавляем ярлык в табличный вид игрового поля

                buttons[i][j] = new Button("");  // поверх ярлыка создаем новую кнопку
                defineButton(i, j);                    // и присваиваем ей стартовые свойства

//              Добавление обработчика нажатий кнопок мыши для каждой кнопки на игровом поле
                buttons[i][j].setOnMouseClicked(e -> {
                    Button eventButton = (Button) e.getSource(); // определяем и запоминаем, какая именно кнопка нажата
                    switch (e.getButton()) {
                        case PRIMARY: {  // если нажата левая кнопка мыши
                            if (eventButton.getText().equals("X"))
                                break;  // если кнопка помечена флажком, кнопка не нажимается
                            eventButton.setVisible(false);                 // убираем кнопку, чтобы стал виден текстовый ярлык
                            eventButton.setText(eventButton.getId());      // позиция кнопки на игровом поле сохранена в ее ID (X...Y...). Читаем позицию
                            int x = Integer.parseInt(eventButton.getId().substring(eventButton.getId().indexOf("X") + 1, eventButton.getId().indexOf("Y")));
                            int y = Integer.parseInt(eventButton.getId().substring(eventButton.getId().indexOf("Y") + 1, eventButton.getId().indexOf("STYLE")));
                            spareCount--; // меньшаем количество безопасных клеток, которые осталось открыть
                            if (spareCount == 0 || gameFieldValues[x][y] == 10) gameEnd(); // если открыта последняя свободная от мин клетка или наступили на мину
                            if (gameFieldValues[x][y] == 0) clearEmptySpace(x, y);  // если наступили на свободную от чисел и мин площадь, очистить ее всю
                            break;
                        }
                        case SECONDARY: {  // если нажата правая кнопка мыши
                            if (eventButton.getText().equals("X")) { // если кнопка уже помечена флажком...
                                eventButton.setText("");             // снять признак помеченности
                                eventButton.setStyle(eventButton.getId().substring(eventButton.getId().indexOf("STYLE") + 5));
                                eventButton.setGraphic(emptyImage);  // убрать картинку флажка
                                mineCount++;                         // вернуть в счётчик 1 мину, которую необходимо открыть
                            } else {  // если кнопка еще не помечена флажком
                                eventButton.setText("X");   // установить признак помеченности
                                eventButton.setGraphic(new ImageView(flagImage)); // добавить картинку флажка
                                mineCount--;  // уменьшить счетчик мин, которые необходимо ещё пометить
                                if (mineCount == 0) gameEnd(); // если мин больше не осталось (помечено количество клеток равное количеству мин)
                            }
                            minesStill.setText("" + mineCount); // сообщаем игроку, сколько мин осталось открыть
                            break;
                        }
                    }
                });
                gameField.add(buttons[i][j], i, j);
            }
        }

//      Создаем и запускаем бесконечный таймер с периодичностью вызова 1 раз в секунду для вывода прошедшего времени на экран
//      Благодаря лямбда-выражению "ae->{}" можно без создания отдельного метода описать что будет происходить при каждой обработке события
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), ae -> {
                    timeCount++; // при каждом вызове таймера на 1 увеличивается количество прошедших секунд в счетчике
                    timeElapsed = (Label) scene.lookup("#timeelapsed");  // находим в сцене ярлык, отображающий прошедшее время по заранее присвоенному id
                    timeElapsed.setText("" + String.format("%02d:%02d", timeCount / 60, timeCount % 60)); // форматируем и выводим время
                })
        );
        timeline.setCycleCount(-1);  // эмпирически установлено, что -1 запускает таймер не на конечное количество запусков, а навечно
        timeline.play(); // запуск таймера
    }

    // При попадании на пустую клетку, нужно по принципу разливающейся воды очистить все пустое пространство,
    // включая прилегающие ячейки с цифрами ("берега" пустого пространства)
    private void clearEmptySpace(int x, int y) {
        for (int i = 0; i < fieldSize; i++)
            for (int j = 0; j < fieldSize; j++)
                emptySpaceMask[i][j] = 0;
        openSquare(x, y);
        for (int i = 0; i < fieldSize; i++)
            for (int j = 0; j < fieldSize; j++)
                if (emptySpaceMask[i][j]>0) buttons[i][j].setVisible(false);
    }

    // метод помечает к открытию квадрат ячеек 3х3 вокруг центральной пустой клетки если одна из боковых клеток пустая,
    // рекурсивно вызывает сам себя уже по ее адресу
    private void openSquare(int x, int y) {
        emptySpaceMask[x][y] = 2;
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++) {
                if ((x + i >= 0) && (x + i < fieldSize) && (y + j >= 0) && (y + j < fieldSize)) { // если не вышли за рамки игрового поля
                    if (emptySpaceMask[x + i][y + j]<2)
                        if (labels[x + i][y + j].getText().equals("")) openSquare(x + i, y + j);
                        else emptySpaceMask[x+i][y+j] = 1;
                }
            }
    }

    // при наступлении события, означающего конец игры, нужно проверить, выиграл ли игрок, все ли мины отмечены правильно
    private void gameEnd() {
        // счетчики показывают, что все мины найдены (и/или все пустые клетки открыты). нужно проверить правильность
        timeline.stop();
        boolean died = false;
        for (int i = 0; i < fieldSize; i++)
            for (int j = 0; j < fieldSize; j++) {
                if (!(buttons[i][j].getText().equals("X")) && gameFieldValues[i][j] == 10)
                    died = true; // не помечена мина
                else if (buttons[i][j].isVisible() && (gameFieldValues[i][j] == 10) && (!(buttons[i][j].getText().equals("X")))) died = true; // "наступили" на мину
                else if (buttons[i][j].getText().equals("X") && gameFieldValues[i][j] != 10)
                    buttons[i][j].setGraphic(new ImageView(noMineImage)); // помечена несуществующая мина
                else if ((gameFieldValues[i][j] == 10) && (buttons[i][j].getText().equals("X"))) buttons[i][j].setGraphic(new ImageView(rightMineImage));
                else buttons[i][j].setVisible(false); // открываем каждую ячейку поля
            }
        if (died) scene.lookup("#died").setVisible(true);
        else {  // если выиграл
            scene.lookup("#won").setVisible(true);
            Label bestTime = (Label) scene.lookup("#besttime");
            if ((timeCount < (bestTimeCount)) || (bestTimeCount < 0)) {
                bestTimeCount = timeCount;
                bestTime.setText(timeElapsed.getText());
                scene.lookup("#newbesttime").setVisible(true);
            }
        }
    }

    // расстановка мин по массиву и заполнение прочих ячеек количеством рядом стоящих мин
    static void fillGameField() {
        int mineX, mineY;

        // Обнуление массива значений игрового поля
        for (int i = 0; i < fieldSize; i++)
            for (int j = 0; j < fieldSize; j++) {
                gameFieldValues[i][j] = 0;
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
                if (gameFieldValues[i][j] < 10)
                    for (int k = -1; k < 2; k++)
                        for (int l = -1; l < 2; l++)
                            if ((i + k) >= 0 && (i + k) < fieldSize && (j + l) >= 0 && (j + l) < fieldSize && (gameFieldValues[i + k][j + l] == 10))
                                gameFieldValues[i][j]++;
    }

    // визуальное приведение поля к начальному состоянию - все кнопки видны, все маркеры сняты, таймер обнулен
    static void restoreGameFieldView() {
        minesStill.setText("" + mineCount);
        spareCount = fieldSize * fieldSize - mineCount;
        timeCount = 0;
        timeElapsed = (Label) scene.lookup("#timeelapsed");
        scene.lookup("#died").setVisible(false);
        scene.lookup("#won").setVisible(false);
        scene.lookup("#newbesttime").setVisible(false);
        timeElapsed.setText("00:00");
        for (int i = 0; i < fieldSize; i++)
            for (int j = 0; j < fieldSize; j++) {
                defineLabel(i, j);
                defineButton(i, j);
            }
        timeline.play();
    }

    // установка начальных свойств для тектового ярлыка в каждой ячейке игрового поля
    private static void defineLabel(int i, int j) {
        labels[i][j].setStyle("-fx-background-color: transparent");
        String s;
        switch (gameFieldValues[i][j]) {
            case 0: {
                s = "";
                break;
            }
            case 10: {
                s = "@";
                labels[i][j].setStyle("-fx-background-color: #ffbbbb");
                break;
            }
            default:
                s = "" + gameFieldValues[i][j];
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

    // установка начальных свойств для кнопки в каждой ячейке игрового поля
    private static void defineButton(int i, int j) {
        buttons[i][j].setText("");
        buttons[i][j].setPrefHeight(cellSize);
        buttons[i][j].setPrefWidth(cellSize);
        buttons[i][j].setMinHeight(cellSize);
        buttons[i][j].setMinWidth(cellSize);
        buttons[i][j].setAlignment(Pos.CENTER);
        buttons[i][j].setStyle("-fx-base: #aaaaaa");
        buttons[i][j].setId("X" + i + "Y" + j + "STYLE" + buttons[i][j].getStyle());
        buttons[i][j].setGraphic(emptyImage);
        buttons[i][j].setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        buttons[i][j].setFocusTraversable(false);
        buttons[i][j].setVisible(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

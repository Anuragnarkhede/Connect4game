package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = fxmlLoader.load();

       MenuBar menuBar = createMenu();
       menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        controller = fxmlLoader.getController();
        controller.createPlayGround();
        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);

        Scene scene = new Scene(rootGridPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect4");
        primaryStage.setResizable(false);
        primaryStage.show();

       }


       public MenuBar createMenu(){
        Menu fileMenu = new Menu("File");

           MenuItem newGame = new MenuItem("New Game");
           newGame.setOnAction(event -> { controller.resetGame(); });

           MenuItem ThreePlayer = new MenuItem("3 Player");
           ThreePlayer.setOnAction(event -> { controller.ThreePlayer();});

           MenuItem resetGame = new MenuItem("Reset Game ");
           resetGame.setOnAction(event -> {
               controller.resetGame();
           });
           MenuItem exitGame = new MenuItem("Exit");

           exitGame.setOnAction(event -> exitgame());

           SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

           fileMenu.getItems().addAll(newGame,resetGame,separatorMenuItem,exitGame);
           Menu helpMenu = new Menu("Help");
           MenuItem aboutGame = new MenuItem("About Connect4");
           aboutGame.setOnAction(event -> aboutgame());
           MenuItem aboutDeveloper = new MenuItem("About Me");
           aboutDeveloper.setOnAction(new EventHandler<ActionEvent>() {
               @Override
               public void handle(ActionEvent event) {
                   aboutMe();
               }
           });
           SeparatorMenuItem separator = new SeparatorMenuItem();

           helpMenu.getItems().addAll(aboutGame,separator,aboutDeveloper);
           MenuBar menuBar = new MenuBar();
           menuBar.getMenus().addAll(fileMenu,helpMenu);

           return menuBar;
 }

    private void aboutMe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Anout The Developer ");
        alert.setHeaderText(" Anurag Narkhede");
        alert.setContentText("I Love to play around with code and create game." +
                " Connect4 is the one of the game. I  love to play in Free Time ");
        alert.show();
    }

    private void aboutgame() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Four");
        alert.setHeaderText("How To Play");
        alert.setContentText("Connect Four is a two-player connection game in which the players first choose a color and " +
                "then take turns dropping colored discs from the top into a seven-column," +
                " six-row vertically suspended grid. The pieces fall straight down, " +
                "occupying the next available space within the column. The objective of " +
                "the game is to be the first to form a horizontal, vertical, or diagonal " +
                "line of four of one's own discs. Connect Four is a solved game. " +
                "The first player can always win by playing the right moves.");
        alert.show();
    }

    private void exitgame() {
        Platform.exit();
        System.exit(0);
    }

    private void resetGame() {

    }

    public static void main(String[] args) {
        launch(args);
    }
}

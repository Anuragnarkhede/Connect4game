package sample;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	public static final int COLUMNS = 7;
	public static final int ROWS = 6;
	public static final int CIRCLE_DIAMETER = 80;
	public static final String disc_color1 = "#24303E";
	public static final String disc_color2 = "#4CAA88";
	public static String Player_One ="Player One" ;
	public static String player_two = "Player Two" ;

	private boolean isPlayerOneTurn = true;

	private Disc[][] insertedDiscArray = new Disc[ROWS][COLUMNS];


	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscsPane;

	@FXML
	public Label playerNameLabel;

	@FXML
	public TextField PlayerOneTextField , PlayerTwoTextField ;

	@FXML
	public Button submitbtn;

	private boolean isAllowedToInsert = true;

	public void createPlayGround(){

		Platform.runLater(() -> submitbtn.requestFocus());

		Shape rectangleWithHoles = gameStructuralGrid();
		rootGridPane.add(rectangleWithHoles,0 ,1 );
		List<Rectangle> rectangleList = createClickableColumn();
		for(Rectangle rectangle: rectangleList){
			rootGridPane.add(rectangle,0,1);
		}


		submitbtn.setOnAction(event -> {
			Player_One = PlayerOneTextField.getText();
			player_two = PlayerTwoTextField.getText();
			playerNameLabel.setText(isPlayerOneTurn? Player_One : player_two);
		});



	}
	private Shape gameStructuralGrid() {

		javafx.scene.shape.Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER , (ROWS + 1) * CIRCLE_DIAMETER);

		for(int row =0; row<ROWS;row++){
			for(int col=0;col<COLUMNS;col++){

				Circle circle = new Circle();
				circle.setRadius(CIRCLE_DIAMETER/2);
				circle.setCenterX(CIRCLE_DIAMETER/2);
				circle.setCenterY(CIRCLE_DIAMETER/2);
				circle.setSmooth(true);
				circle.setTranslateX(col * (CIRCLE_DIAMETER + 5)+ CIRCLE_DIAMETER /4);
				circle.setTranslateY(row * (CIRCLE_DIAMETER + 5)+CIRCLE_DIAMETER /4);
				rectangleWithHoles = Shape.subtract(rectangleWithHoles,circle);
			}
		}
		rectangleWithHoles.setFill(Color.WHITE);
		return rectangleWithHoles;

	}

	private List<Rectangle> createClickableColumn(){

		List<Rectangle> rectangleList = new ArrayList<>();
		for(int col = 0; col<COLUMNS;col++){

			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER,(ROWS + 1) * CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5)+ CIRCLE_DIAMETER /4);
			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			final int column = col;
			rectangle.setOnMouseClicked(event ->{
				if(isAllowedToInsert){
					isAllowedToInsert = false; // when disc is being droped no more disc will be inserted

				insertDisc(new Disc(isPlayerOneTurn),column);
				}
			});
			rectangleList.add(rectangle);
		}


		return rectangleList;
	}

	private void insertDisc(Disc disc,int column){

		int row = ROWS-1;
		while (row >= 0){
			if(getDiscIfPresent(row,column) == null)
				break;
				row--;
		}
		if(row<0)
			return;

		insertedDiscArray[row][column] = disc;
		insertedDiscsPane.getChildren().add(disc);
		disc.setTranslateX(column * (CIRCLE_DIAMETER + 5)+ CIRCLE_DIAMETER /4);

		String submitbtn;


		int currentRow =row;
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),disc);
		translateTransition.setToY(row * (CIRCLE_DIAMETER + 5)+CIRCLE_DIAMETER /4);
		translateTransition.setOnFinished(event ->{
			isAllowedToInsert = true; // finally when disc is droped allow next player to insert the disc
			if(gameEnded(currentRow,column)){
				gameOver();
				return;
			}
			isPlayerOneTurn = !isPlayerOneTurn;
			playerNameLabel.setText(isPlayerOneTurn? Player_One : player_two);
		});
		translateTransition.play();
	}

	private void gameOver() {
		String winner = isPlayerOneTurn ? Player_One : player_two;
		System.out.println("Winner Is :"+winner);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect4");
		alert.setHeaderText("Winner is :"+winner);
		alert.setContentText("Want To Play Again?");
		ButtonType yesBtn = new ButtonType("Yes");
		ButtonType noBtn = new ButtonType("No");
		alert.getButtonTypes().setAll(yesBtn,noBtn);

		Platform.runLater(()-> {
			Optional<ButtonType> btnClicked = alert.showAndWait();
			if(btnClicked.isPresent() && btnClicked.get() == yesBtn){

				resetGame();
			}
			else {

				Platform.exit();
				System.exit(0);
			}
		});

	}

	public void resetGame() {
		insertedDiscsPane.getChildren().clear();
		for(int row=0;row<insertedDiscArray.length;row++){
			for(int column=0;column<insertedDiscArray[row].length;column++){
				insertedDiscArray[row][column] =null;
			}
		}
		isPlayerOneTurn = true;
		playerNameLabel.setText(Player_One);
		createPlayGround();
	}

	private boolean  gameEnded(int row, int column){

		List<Point2D> verticalPoints = IntStream.rangeClosed(row-3,row + 3) // raange of row values = 0,1,2,3,4,5
				.mapToObj(r -> new Point2D(r,column))//0.3,1.3,2.3,3.3,4.3,5.3 ->points2D X,Y
				.collect(Collectors.toList());

		List<Point2D> horizontalPoints = IntStream.rangeClosed(column-3,column + 3) // raange of row values = 0,1,2,3,4,5
				.mapToObj(col -> new Point2D(row,col))//0.3,1.3,2.3,3.3,4.3,5.3 ->points2D X,Y
				.collect(Collectors.toList());

		Point2D startingPoint1 = new Point2D(row -3,column + 3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0,6)
				.mapToObj(i -> startingPoint1.add(i,-i))
				.collect(Collectors.toList());

		Point2D startingPoint2 = new Point2D(row -3,column - 3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6)
				.mapToObj(i -> startingPoint2.add(i,i))
				.collect(Collectors.toList());

		boolean isEnded = checkCombination(verticalPoints) || checkCombination(horizontalPoints)
				|| checkCombination(diagonal1Points)|| checkCombination(diagonal2Points);
		return isEnded;
	}

	private boolean checkCombination(List<Point2D> Points) {

		int chain = 0;
		for(Point2D point : Points){

			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();

			Disc disc = getDiscIfPresent(rowIndexForArray,columnIndexForArray);
			if(disc != null && disc.isPlayerOneMove == isPlayerOneTurn){
					chain++;
					if(chain == 4){
						return true;
				}
			}
			else {
				chain = 0;
			}
		}
		return false;
	}

	private Disc getDiscIfPresent(int row , int column){

		if(row >= ROWS || row < 0 || column >= COLUMNS || column < 0)
			return null;
		return insertedDiscArray[row][column];
	}

	public void ThreePlayer() {
	}

	private static class Disc extends Circle{

		private final boolean isPlayerOneMove;
		public Disc(boolean isPlayerOneMove){

			this.isPlayerOneMove = isPlayerOneMove;
			setRadius(CIRCLE_DIAMETER/2);
			setFill(isPlayerOneMove? Color.valueOf(disc_color1): Color.valueOf(disc_color2));
			setCenterX(CIRCLE_DIAMETER/2);
			setCenterY(CIRCLE_DIAMETER/2);

		}
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}

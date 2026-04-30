package Game.PlantGrowthSimulator;

import javafx.application.Application;
import javafx.stage.Stage;

public class GameMain extends Application {

	@Override
	public void start(Stage primaryStage) {
		// Initialize the model, view, and controller
		GameModel model = new GameModel();
		GameView view = new GameView(primaryStage);
		GameController controller = new GameController(model, view);
		// Connect the view with its controller and display the start screen
		view.setController(controller);
		view.showStartScreen();
	}

	public static void main(String[] args) {
		// Launch the JavaFX application
		launch(args);
	}
}

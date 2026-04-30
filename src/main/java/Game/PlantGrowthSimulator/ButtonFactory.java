package Game.PlantGrowthSimulator;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ButtonFactory {
	public static Button createButton(String type) {
		Button button = new Button();
		Image image = null;
		switch (type) {
		case "Play":
			image = new Image(ButtonFactory.class.getResourceAsStream("play.png"));
			button.setPrefSize(120, 45);
			break;
		case "Start":
			image = new Image(ButtonFactory.class.getResourceAsStream("start.png"));
			button.setPrefSize(50, 29);
			break;
		case "Again":
			image = new Image(ButtonFactory.class.getResourceAsStream("play-again.png"));
			button.setPrefSize(100, 29);
			break;
		case "Sun":
			image = new Image(ButtonFactory.class.getResourceAsStream("sun.png"));
			button.setPrefSize(140, 140);
			break;
		case "Water":
			image = new Image(ButtonFactory.class.getResourceAsStream("water.png"));
			button.setPrefSize(57, 66);
			break;
		case "Fertilizer":
			image = new Image(ButtonFactory.class.getResourceAsStream("fertilizer.png"));
			button.setPrefSize(57, 61);
			break;
		case "Cucumber":
			image = new Image(ButtonFactory.class.getResourceAsStream("cucumber-seed-packet.png"));
			button.setPrefSize(80, 102);
			// Optional: button.setText("Cucumber");
			break;
		case "Tomato":
			image = new Image(ButtonFactory.class.getResourceAsStream("tomato-seed-packet.png"));
			button.setPrefSize(80, 102);
			// Optional: button.setText("Tomato");
			break;
		default:
			button.setText("Button");
			return button;
		}
		// Set image if available
		if (image != null) {
			ImageView imageView = new ImageView(image);
			imageView.setFitWidth(button.getPrefWidth());
			imageView.setFitHeight(button.getPrefHeight());
			button.setGraphic(imageView);
		}
		// Remove default text and background
		button.setText("");
		button.setStyle("-fx-background-color: transparent;");

		// Apply hover scale effect
		Animation.addHoverScaleEffect(button, 1.1, 250);
		// Apply fade-in effect
		Animation.createFadeTransition(button, 500, 0, 1).play();

		return button;
	}
}

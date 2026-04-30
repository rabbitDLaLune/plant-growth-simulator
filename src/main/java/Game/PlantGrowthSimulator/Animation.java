package Game.PlantGrowthSimulator;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animation {

	// Adds a hover scale effect to a node.
	public static void addHoverScaleEffect(Node node, double scaleFactor, double durationMillis) {
		node.setOnMouseEntered(e -> {
			// Change cursor to hand and scale up the node.
			node.setCursor(javafx.scene.Cursor.HAND);
			ScaleTransition scaleUp = new ScaleTransition(Duration.millis(durationMillis), node);
			scaleUp.setToX(scaleFactor);
			scaleUp.setToY(scaleFactor);
			scaleUp.play();
		});

		node.setOnMouseExited(e -> {
			// Reset cursor and scale node back to original size.
			node.setCursor(javafx.scene.Cursor.DEFAULT);
			ScaleTransition scaleDown = new ScaleTransition(Duration.millis(durationMillis), node);
			scaleDown.setToX(1.0);
			scaleDown.setToY(1.0);
			scaleDown.play();
		});
	}

	// Creates a fade transition for a node.
	public static FadeTransition createFadeTransition(Node node, double durationMillis, double fromValue,
			double toValue) {
		FadeTransition ft = new FadeTransition(Duration.millis(durationMillis), node);
		ft.setFromValue(fromValue);
		ft.setToValue(toValue);
		return ft;
	}
}
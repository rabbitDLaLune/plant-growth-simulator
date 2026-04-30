package Game.PlantGrowthSimulator;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class FarmerObject extends GameObject {
	private double width;
	private double height;

	public FarmerObject(GraphicsContext gc, double x, double y) {
		super(gc, x, y);
		// Set image dimensions
		this.width = 125;
		this.height = 123;
		// Load farmer image
		this.img = new Image(getClass().getResourceAsStream("farmer.png"));
	}

	@Override
	public void update() {
		// Draw image if available
		if (img != null) {
			gc.drawImage(img, x, y, width, height);
		}
	}
}

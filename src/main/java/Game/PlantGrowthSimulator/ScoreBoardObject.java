package Game.PlantGrowthSimulator;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class ScoreBoardObject extends GameObject {
	private double width;
	private double height;

	public ScoreBoardObject(GraphicsContext gc, double x, double y) {
		super(gc, x, y);
		// Set score board dimensions
		this.width = 175;
		this.height = 218;
		// Load score board image
		this.img = new Image(getClass().getResourceAsStream("score-board.png"));
	}

	@Override
	public void update() {
		// Draw image if available
		if (img != null) {
			gc.drawImage(img, x, y, width, height);
		}
	}
}

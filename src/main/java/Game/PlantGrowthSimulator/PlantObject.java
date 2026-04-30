package Game.PlantGrowthSimulator;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PlantObject extends GameObject {
	private int stage;
	private Image[] stageImages;
	private ImageView imageView;

	public PlantObject(Image[] stageImages, double x, double y) {
		super(null, x, y);
		this.stageImages = stageImages;
		this.stage = 0;
		if (stageImages != null && stageImages.length > 0) {
			imageView = new ImageView(stageImages[0]);
		}
		imageView.setFitWidth(260);
		imageView.setFitHeight(340);
		imageView.setLayoutX(x);
		imageView.setLayoutY(y);
	}

	// Returns the ImageView node so it can be added to the scene graph.
	public ImageView getNode() {
		return imageView;
	}

	// Updates the plant's stage and image.
	public void setStage(int stage) {
		if (stage >= 0 && stage < stageImages.length) {
			this.stage = stage;
			imageView.setImage(stageImages[stage]);
		}
	}

	public int getStage() {
		return stage;
	}
}

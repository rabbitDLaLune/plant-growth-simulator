package Game.PlantGrowthSimulator;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class TextBoxObject extends GameObject {
	private double width;
    private double height;
    
    public TextBoxObject(GraphicsContext gc, double x, double y) {
        super(gc, x, y);
        this.width = 550;
        this.height = 120;
        this.img = new Image(getClass().getResourceAsStream("text-box.png"));
    }
    
    @Override
    public void update() {
        if (img != null) {
            gc.drawImage(img, x, y, width, height);
        }
    }
}
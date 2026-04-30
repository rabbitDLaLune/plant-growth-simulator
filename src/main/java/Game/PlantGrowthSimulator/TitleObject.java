package Game.PlantGrowthSimulator;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class TitleObject extends GameObject {

    public TitleObject(GraphicsContext gc, double x, double y) {
        super(gc, x, y);
        // Load the title image from resources.
        this.img = new Image(getClass().getResourceAsStream("title.png"));
    }
    
    @Override
    public void update() {
        if (img != null) {
            gc.drawImage(img, x, y, 400, 400 * (img.getHeight() / img.getWidth()));
        }
    }
    
    // Change the object's position.
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    // Hide the title by not drawing it.
    public void hide() {
        this.img = null;
    }
}
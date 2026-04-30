package Game.PlantGrowthSimulator;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class GrassObject extends GameObject {
    private double width;
    private double height;

    public GrassObject(GraphicsContext gc, double x, double y, Image img) {
        super(gc, x, y);
        this.img = img;
        this.width = 47;  // default width for grass
        this.height = 47; // default height for grass
    }

    @Override
    public void update() {
        if (img != null) {
            gc.drawImage(img, x, y, width, height);
        }
    }
    
    // Returns true if the provided (clickX, clickY) coordinates are within this grass object.
    public boolean contains(double clickX, double clickY) {
        return clickX >= x && clickX <= (x + width) && clickY >= y && clickY <= (y + height);
    }
    
    // Getter for x
    public double getX() {
        return x;
    }
    
    // Getter for y
    public double getY() {
        return y;
    }
}

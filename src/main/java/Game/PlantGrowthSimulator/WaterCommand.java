package Game.PlantGrowthSimulator;

public class WaterCommand implements Command {
    private GameModel model;
    private GameView view;
    
    public WaterCommand(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
    }
    
    @Override
    public void execute() {
        System.out.println("Water command executed: Plant is being watered.");
     // Trigger the water effect in the view.
        view.showWaterEffect();
    }
}

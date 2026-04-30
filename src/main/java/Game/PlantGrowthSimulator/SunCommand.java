package Game.PlantGrowthSimulator;

public class SunCommand implements Command {
    private GameModel model;
    private GameView view;
    
    public SunCommand(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
    }
    
    @Override
    public void execute() {
        // Logic for giving the plant sunlight.
        System.out.println("Sun command executed: Plant is receiving sunlight.");
        // Trigger the sun effect in the view.
        view.showSunEffect();
    }
}


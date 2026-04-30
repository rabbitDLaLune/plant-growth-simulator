package Game.PlantGrowthSimulator;

public class FertilizerCommand implements Command {
	private GameModel model;
	private GameView view;

	public FertilizerCommand(GameModel model, GameView view) {
		// Initialize model and view
		this.model = model;
		this.view = view;
	}

	@Override
	public void execute() {
		// Execute fertilizer command action
		System.out.println("Fertilizer command executed: Plant is receiving fertilizer.");
		// Trigger fertilizer effect in the view
		view.showFertilizerEffect();
	}
}

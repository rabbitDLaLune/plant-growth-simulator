package Game.PlantGrowthSimulator;

import java.util.HashMap;
import java.util.Map;

public class GameController {
	private GameModel model;
	private GameView view;

	// Map to hold command objects
	private Map<String, Command> commands = new HashMap<>();

	public GameController(GameModel model, GameView view) {
		this.model = model;
		this.view = view;
		initCommands(); // Initialize commands
	}

	private void initCommands() {
		// Map commands to their corresponding actions
		commands.put("Sun", new SunCommand(model, view));
		commands.put("Water", new WaterCommand(model, view));
		commands.put("Fertilizer", new FertilizerCommand(model, view));
	}

	// Handle start button press
	public void handleStart() {
		view.showGameScreen();
	}

	// Handle plant selection and advance game stage
	public void handlePlantSelection(String plantType) {
		model.setSelectedPlantType(plantType);
		model.advanceStage(); // Advance from stage 0 to 1
		view.showGameControls();
	}

	// Execute a command based on its name
	public void executeCommand(String commandName) {
		Command cmd = commands.get(commandName);
		if (cmd != null) {
			cmd.execute();
		}
	}

	public GameModel getModel() {
		return model;
	}

	// Set the selected plant type in the model
	public void setPlantType(String plantType) {
		model.setSelectedPlantType(plantType);
	}
}

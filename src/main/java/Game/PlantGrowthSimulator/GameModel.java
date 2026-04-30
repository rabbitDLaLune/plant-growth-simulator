package Game.PlantGrowthSimulator;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GameModel {
	// Stage starts at 0 and advances when a seed is selected.
	private IntegerProperty stage = new SimpleIntegerProperty(0);
	// Selected plant type.
	private StringProperty selectedPlantType = new SimpleStringProperty("");
	// Total score.
	private IntegerProperty totalScore = new SimpleIntegerProperty(0);

	public int getStage() {
		return stage.get();
	}

	public void setStage(int stage) {
		this.stage.set(stage);
	}

	public IntegerProperty stageProperty() {
		return stage;
	}

	// Increment stage and log the change.
	public void advanceStage() {
		setStage(getStage() + 1);
		System.out.println("Advancing to stage: " + getStage());
	}

	public String getSelectedPlantType() {
		return selectedPlantType.get();
	}

	public void setSelectedPlantType(String selectedPlantType) {
		this.selectedPlantType.set(selectedPlantType);
		System.out.println("Plant type set to: " + selectedPlantType);
	}

	public StringProperty selectedPlantTypeProperty() {
		return selectedPlantType;
	}

	public int getTotalScore() {
		return totalScore.get();
	}

	public void setTotalScore(int totalScore) {
		this.totalScore.set(totalScore);
	}

	public IntegerProperty totalScoreProperty() {
		return totalScore;
	}

	// Reset the game stage and score.
	public void resetGame() {
		setStage(0);
		setTotalScore(0);
		System.out.println("Game reset.");
	}
}

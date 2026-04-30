package Game.PlantGrowthSimulator;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Random;

public class GameView {
	private Stage stage;
	private Scene scene;
	private Pane root;
	private GameController controller;
	private GameModel model;
	private Canvas canvas;
	private GraphicsContext gc;

	// Buttons.
	private Button playButton;
	private Button sunButton;
	private Button waterButton;
	private Button fertilizerButton;
	private Button actionButton;

	// UI Labels.
	private Label instructionLabel;
	private Label miniGameInstructionLabel;
	private Label centerCountdownLabel;
	private Label topCountdownLabel;
	private Label finalScoreLabel;
	private Label grassInstructionLabel;
	private Label stageDescriptionLabel;
	// Label for displaying stage challenge information at the top left.
	private Label stageAdvanceLabel;
	// Label for displaying a running list of stage scores.
	private Label scoreListLabel;

	// Stage descriptions (for stages 1 to 6).
	private String[] stageDescriptions = {
			"Nice job planting the seed! 🌱 Let’s give it some water and "
					+ "\nsunlight so it can wake up and start growing!",

			"Look at those tiny leaves! 🍃 Your plant is learning to make its"
					+ "\nown food using sunlight. You're doing great!",

			"Whoa, leafy power! 🍃 Your plant is growing fast.. it’s getting " + "\nready for flowers!",

			"Flowers! 🌼 Bees love them, and they help your plant make " + "\nyummy fruit!",

			"Fruit time! 🍅 Look at your plant grow! It’s almost ready to eat!",

			"Delicious! 🍓 Your fruit is ripe and ready to eat. You grew it" + "\nfrom start to finish!" };

	// Click counters (for mini-game button presses).
	private int sunClickCount = 0;
	private int waterClickCount = 0;
	private int fertilizerClickCount = 0;
	private int penaltyClickCount = 0;

	// Grass management.
	private boolean grassSpawned = false;
	private ArrayList<GrassObject> grassList = new ArrayList<>();
	private Random random = new Random();

	// Requirements and timer arrays (indices 0-5 correspond to stages 1-6).
	private int[] expectedSun = { 1, 1, 2, 2, 2, 3 };
	private int[] expectedWater = { 1, 1, 1, 2, 2, 2 };
	private int[] expectedFertilizer = { 0, 1, 1, 1, 2, 2 };
	private int[] stageTime = { 4, 6, 8, 10, 12, 15 };

	// Game objects.
	private String selectedPlantType;
	private PlantObject plant;
	private FarmerObject farmer;
	private TextBoxObject textBox;
	private TitleObject titleObj;
	private ScoreBoardObject scoreBoardObject;

	// Flags for mini-game control.
	private boolean miniGameActive = false;
	private boolean countdownStarted = false;
	private boolean waitingForNextStage = false;
	private boolean waitingForStage1 = false;

	// Media player for background music.
	private MediaPlayer mediaPlayer;

	public GameView(Stage stage) {
		this.stage = stage;
		root = new Pane();

		// Set background image.
		Image backgroundImage = new Image(getClass().getResourceAsStream("background.png"));
		BackgroundSize backgroundSize = new BackgroundSize(805, 625, false, false, false, false);
		BackgroundImage bgImage = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
		root.setBackground(new Background(bgImage));

		scene = new Scene(root, 800, 620);
		stage.setScene(scene);
		stage.setTitle("Plant Growth Simulator");
	}

	public void setController(GameController controller) {
		this.controller = controller;
		// Get reference to the model.
		this.model = controller.getModel();
	}

	public GraphicsContext getGraphicsContext() {
		if (canvas == null) {
			canvas = new Canvas(800, 620);
			root.getChildren().add(canvas);
			gc = canvas.getGraphicsContext2D();

			// Handle mouse clicks for grass removal.
			canvas.setOnMouseClicked(ev -> {
				double clickX = ev.getX();
				double clickY = ev.getY();
				ArrayList<GrassObject> toRemove = new ArrayList<>();
				for (GrassObject g : grassList) {
					if (g.contains(clickX, clickY)) {
						toRemove.add(g);
					}
				}
				if (!toRemove.isEmpty()) {
					grassList.removeAll(toRemove);
					MediaPlayer player = createMediaPlayer("/Game/PlantGrowthSimulator/grass-pluck.mp3", 0.6, 1);
					if (player != null) {
					    player.play();
					}
					redrawCanvas();
				}
			});

			// Change cursor when over grass.
			canvas.setOnMouseMoved(ev -> {
				boolean overGrass = false;
				for (GrassObject g : grassList) {
					if (g.contains(ev.getX(), ev.getY())) {
						overGrass = true;
						break;
					}
				}
				canvas.setCursor(overGrass ? javafx.scene.Cursor.HAND : javafx.scene.Cursor.DEFAULT);
			});
		}
		return gc;
	}

	// Utility method to update the stage challenge label dynamically.
	private void updateStageChallengeLabel() {
		int stageNumber = model.getStage();
		int remainingSun = Math.max(0, expectedSun[stageNumber - 1] - sunClickCount);
		int remainingWater = Math.max(0, expectedWater[stageNumber - 1] - waterClickCount);
		int remainingFertilizer = Math.max(0, expectedFertilizer[stageNumber - 1] - fertilizerClickCount);
		if (stageAdvanceLabel != null) {
			stageAdvanceLabel.setText("Stage Challenges\nSun: " + remainingSun + "\nWater: " + remainingWater
					+ "\nFertilizer: " + remainingFertilizer);
		}
	}

	// Redraws game objects on the canvas.
	private void redrawCanvas() {
		gc.clearRect(0, 0, 800, 620);
		// Redraw persistent objects.
		if (farmer != null) {
			farmer.update();
		}
		if (textBox != null) {
			textBox.update();
		}
		for (GrassObject g : grassList) {
			g.update();
		}
		// Also update the title if it is still visible.
		if (titleObj != null) {
			titleObj.update();
		}
		// If the score board is active, update it as well.
		if (scoreBoardObject != null) {
			scoreBoardObject.update();
		}
	}

	// New helper method to create and return a MediaPlayer for a given resource.
	private MediaPlayer createMediaPlayer(String resourcePath, double volume, int cycleCount) {
	    try (InputStream musicStream = getClass().getResourceAsStream(resourcePath)) {
	        if (musicStream == null) {
	            System.err.println("Music resource not found: " + resourcePath);
	            return null;
	        }
	        // Create a temporary file for the music resource.
	        File tempFile = File.createTempFile("temp-music", ".mp3");
	        tempFile.deleteOnExit();
	        Files.copy(musicStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

	        // Load the media from the temporary file's URI.
	        Media media = new Media(tempFile.toURI().toString());
	        MediaPlayer player = new MediaPlayer(media);
	        player.setVolume(volume);
	        player.setCycleCount(cycleCount);
	        return player;
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        return null;
	    }
	}


	private void resetClickCounters() {
		sunClickCount = 0;
		waterClickCount = 0;
		fertilizerClickCount = 0;
		penaltyClickCount = 0;
	}

	// Shows the start screen.
	public void showStartScreen() {
		root.getChildren().clear();
		// Ensure the canvas is added and have the GraphicsContext.
		GraphicsContext gc = getGraphicsContext();

		// Create the TitleObject.
		titleObj = new TitleObject(gc, (800 - 400) / 2, 80);
		titleObj.update();

		// Create a play button.
		playButton = ButtonFactory.createButton("Play");
		playButton.setLayoutX((800 - 120) / 2);
		playButton.setLayoutY((620 - 45) / 1.43);
		playButton.setOnAction(e -> {
			// Play the click sound.
			MediaPlayer player = createMediaPlayer("/Game/PlantGrowthSimulator/button-click-sound.mp3", 0.2, 1);
			if (player != null) {
			    player.play();
			}
 
			// Start background music with loop using the helper method.
	        mediaPlayer = createMediaPlayer("/Game/PlantGrowthSimulator/background_music.mp3", 0.2, MediaPlayer.INDEFINITE);
	        if (mediaPlayer != null) {
	            mediaPlayer.play();
	        }
	        
			// Hide the title.
			titleObj.hide();
			redrawCanvas();
			controller.handleStart();
		});
		root.getChildren().add(playButton);
		stage.show();
	}

	// Shows the plant selection screen.
	public void showGameScreen() {
		// Clear all nodes from the root
		root.getChildren().clear();
		// Add canvas if not already present
		if (canvas == null) {
			getGraphicsContext();
		} else if (!root.getChildren().contains(canvas)) {
			root.getChildren().add(canvas);
		}

		// Create farmer and text box objects
		farmer = new FarmerObject(getGraphicsContext(), 14, 575 - 75);
		textBox = new TextBoxObject(getGraphicsContext(), 150, 575 - 75);
		redrawCanvas();

		// Create buttons for plant selection
		Button cucumberButton = ButtonFactory.createButton("Cucumber");
		cucumberButton.setLayoutX((800 - 65) / 2.4);
		cucumberButton.setLayoutY((620 - 45) / 2.4);
		Button tomatoButton = ButtonFactory.createButton("Tomato");
		tomatoButton.setLayoutX((800 - 65) / 1.8);
		tomatoButton.setLayoutY((620 - 45) / 2.4);

		// Create instruction label
		instructionLabel = new Label(
				"Welcome to 'Plant Growth Simulator'." + "\nGo ahead and pick a plant seed above to get started...");
		instructionLabel.setLayoutX(180);
		instructionLabel.setLayoutY(520);
		instructionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		// Set action for cucumber button
		cucumberButton.setOnAction(e -> {
			MediaPlayer player = createMediaPlayer("/Game/PlantGrowthSimulator/button-click-sound.mp3", 0.2, 1);
		    if (player != null) {
		        player.play();
		    }
			selectedPlantType = "Cucumber";
			root.getChildren().removeAll(cucumberButton, tomatoButton, instructionLabel);
			controller.handlePlantSelection(selectedPlantType);
		});
		// Set action for tomato button
		tomatoButton.setOnAction(e -> {
			MediaPlayer player = createMediaPlayer("/Game/PlantGrowthSimulator/button-click-sound.mp3", 0.2, 1);
		    if (player != null) {
		        player.play();
		    }
			selectedPlantType = "Tomato";
			root.getChildren().removeAll(cucumberButton, tomatoButton, instructionLabel);
			controller.handlePlantSelection(selectedPlantType);
		});

		// Add buttons and label to the root
		root.getChildren().addAll(cucumberButton, tomatoButton, instructionLabel);

		// Initialize score list label if needed
		initializeScoreListLabel();
	}

	// Displays a temporary effect.
	private void showTemporaryEffect(String imageFile, double fitWidth, double fitHeight, double layoutX,
			double layoutY, boolean mouseTransparent) {
		Image image = new Image(getClass().getResourceAsStream(imageFile));
		ImageView effect = new ImageView(image);
		effect.setFitWidth(fitWidth);
		effect.setFitHeight(fitHeight);
		effect.setLayoutX(layoutX);
		effect.setLayoutY(layoutY);
		effect.setMouseTransparent(mouseTransparent);
		root.getChildren().add(effect);

		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
			root.getChildren().remove(effect);
		}));
		timeline.setCycleCount(1);
		timeline.play();
	}

	public void showSunEffect() {
		double layoutX = (800 - 675) / 2.0;
		double layoutY = (620 - 680) / 2.0;
		showTemporaryEffect("sun-ray.png", 680, 650, layoutX, layoutY, true);
	}

	public void showWaterEffect() {
		double layoutX = (800 - 500) / 2.0;
		double layoutY = (620 - 250) / 2.0;
		showTemporaryEffect("watering-plant.png", 330, 255, layoutX, layoutY, false);
	}

	public void showFertilizerEffect() {
		double layoutX = (800 - 160) / 2.0;
		double layoutY = (620 - 250) / 2.0;
		showTemporaryEffect("fertilizer-drop.png", 330, 255, layoutX, layoutY, false);
	}

	// Shows game controls after seed selection.
	public void showGameControls() {
		if (instructionLabel != null) {
			root.getChildren().remove(instructionLabel);
			instructionLabel = null;
		}

		String[] stageImageFiles = null;
		if ("Tomato".equals(selectedPlantType)) {
			stageImageFiles = new String[] { "tomato-stage-1.png", "tomato-stage-2.png", "tomato-stage-3.png",
					"tomato-stage-4.png", "tomato-stage-5.png", "tomato-stage-6.png" };
		} else if ("Cucumber".equals(selectedPlantType)) {
			stageImageFiles = new String[] { "cucumber-stage-1.png", "cucumber-stage-2.png", "cucumber-stage-3.png",
					"cucumber-stage-4.png", "cucumber-stage-5.png", "cucumber-stage-6.png" };
		}
		if (stageImageFiles != null) {
			Image[] plantImages = new Image[stageImageFiles.length];
			for (int i = 0; i < stageImageFiles.length; i++) {
				plantImages[i] = new Image(getClass().getResourceAsStream(stageImageFiles[i]));
			}
			plant = new PlantObject(plantImages, 275, 85);
			FadeTransition fadeIn = Animation.createFadeTransition(plant.getNode(), 800, 0, 1);
			fadeIn.play();
			root.getChildren().add(plant.getNode());
			plant.getNode().toBack();

		}
		redrawCanvas();

		// Reset grass.
		grassSpawned = false;
		grassList.clear();
		if (grassInstructionLabel != null) {
			root.getChildren().remove(grassInstructionLabel);
			grassInstructionLabel = null;
		}

		sunButton = ButtonFactory.createButton("Sun");
		waterButton = ButtonFactory.createButton("Water");
		fertilizerButton = ButtonFactory.createButton("Fertilizer");

		sunButton.setLayoutX(800 - 175);
		sunButton.setLayoutY(15);
		waterButton.setLayoutX(20);
		waterButton.setLayoutY(205);
		fertilizerButton.setLayoutX(20);
		fertilizerButton.setLayoutY(295);

		// Update the challenge label on Sun button click.
		sunButton.setOnAction(e -> {
			MediaPlayer player = createMediaPlayer("/Game/PlantGrowthSimulator/button-click-sound.mp3", 0.2, 1);
		    if (player != null) {
		        player.play();
		    }
			if (miniGameActive) {
				sunClickCount++;
				int stageNumber = model.getStage();
				int expected = expectedSun[stageNumber - 1];
				if (sunClickCount > expected) {
					penaltyClickCount++;
				}
				checkForGrassSpawn();
				updateStageChallengeLabel();
			}
			controller.executeCommand("Sun");
		});

		// Update the challenge label on Water button click.
		waterButton.setOnAction(e -> {
			MediaPlayer player = createMediaPlayer("/Game/PlantGrowthSimulator/button-click-sound.mp3", 0.2, 1);
		    if (player != null) {
		        player.play();
		    }
			if (miniGameActive) {
				waterClickCount++;
				int stageNumber = model.getStage();
				int expected = expectedWater[stageNumber - 1];
				if (waterClickCount > expected) {
					penaltyClickCount++;
				}
				checkForGrassSpawn();
				updateStageChallengeLabel();
			}
			controller.executeCommand("Water");
		});

		// Update the challenge label on Fertilizer button click.
		fertilizerButton.setOnAction(e -> {
			MediaPlayer player = createMediaPlayer("/Game/PlantGrowthSimulator/button-click-sound.mp3", 0.2, 1);
		    if (player != null) {
		        player.play();
		    }
			if (miniGameActive) {
				fertilizerClickCount++;
				int stageNumber = model.getStage();
				int expected = expectedFertilizer[stageNumber - 1];
				if (fertilizerClickCount > expected) {
					penaltyClickCount++;
				}
				checkForGrassSpawn();
				updateStageChallengeLabel();
			}
			controller.executeCommand("Fertilizer");
		});

		root.getChildren().addAll(sunButton, waterButton, fertilizerButton);

		String selectionMessage = "You chose " + selectedPlantType;
		miniGameInstructionLabel = new Label(selectionMessage + ", great choice! 🌱 The first stage is The Seed."
				+ "\nLook at the top left! 👀 Click on the challenge to help your plant"
				+ "\ngrow before the timer ends.");
		miniGameInstructionLabel.setLayoutX(180);
		miniGameInstructionLabel.setLayoutY(520);
		miniGameInstructionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		root.getChildren().add(miniGameInstructionLabel);

		// Initialize the score list label if it doesn't exist.
		initializeScoreListLabel();

		waitingForStage1 = true;
		// In showGameControls (after seed selection) the action button ("Start") is
		// shown.
		showActionButton("Start");
	}

	private void checkForGrassSpawn() {
		int stageNumber = model.getStage();
		if (stageNumber >= 3 && !grassSpawned) {
			int reqSun = expectedSun[stageNumber - 1];
			int reqWater = expectedWater[stageNumber - 1];
			int reqFertilizer = expectedFertilizer[stageNumber - 1];
			if (sunClickCount >= reqSun && waterClickCount >= reqWater && fertilizerClickCount >= reqFertilizer) {
				spawnGrass();
				grassSpawned = true;
			}
		}
	}

	private void spawnGrass() {
		// Remove stage description label if it exists
		if (stageDescriptionLabel != null) {
			root.getChildren().remove(stageDescriptionLabel);
			stageDescriptionLabel = null;
		}

		// Get current stage and determine how many grass objects to spawn
		int stageNumber = model.getStage();
		int grassToSpawn = 1 + (stageNumber - 2);

		// Load the grass image
		Image grassImg = new Image(getClass().getResourceAsStream("grass.png"));

		// Fixed dimensions from GrassObject
		double grassWidth = 47;
		double grassHeight = 47;

		// Define the spawn area boundaries (adjusted so the entire grass fits within)
		double minX = 200;
		double maxX = 600 - grassWidth;
		double minY = 390;
		double maxY = 430 - grassHeight;

		// Spawn grass objects at random positions within the defined area without
		// overlapping
		for (int i = 0; i < grassToSpawn; i++) {
			double x = 0, y = 0;
			int attempts = 0;
			int maxAttempts = 100;
			boolean overlapping;

			do {
				overlapping = false;
				// Generate random position within adjusted boundaries
				x = minX + random.nextDouble() * (maxX - minX);
				y = minY + random.nextDouble() * (maxY - minY);

				// Check for overlap with previously spawned grass objects
				for (GrassObject g : grassList) {
					double gx = g.getX();
					double gy = g.getY();
					// Check using axis-aligned bounding box collision detection
					if (x < gx + grassWidth && x + grassWidth > gx && y < gy + grassHeight && y + grassHeight > gy) {
						overlapping = true;
						break;
					}
				}
				attempts++;
			} while (overlapping && attempts < maxAttempts);

			// Only add the grass if a valid non-overlapping position was found
			if (attempts < maxAttempts) {
				GrassObject grass = new GrassObject(getGraphicsContext(), x, y, grassImg);
				grassList.add(grass);
			} else {
				System.out.println("Failed to place grass without overlap after " + maxAttempts + " attempts.");
			}
		}

		// Create and position the grass instruction label
		grassInstructionLabel = new Label("Oh no, the overgrown grass is crowding out your plants and"
				+ "\nstealing their nutrients! " + "\nClick the grass to remove them!");
		grassInstructionLabel.setLayoutX(180);
		grassInstructionLabel.setLayoutY(520);
		grassInstructionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		root.getChildren().add(grassInstructionLabel);

		// Redraw the canvas to update the display
		redrawCanvas();
	}

	private void startMiniGame(int timerSeconds) {
		// Remove mini-game instructions and any existing stage label
		root.getChildren().remove(miniGameInstructionLabel);
		if (stageDescriptionLabel != null) {
			root.getChildren().remove(stageDescriptionLabel);
		}
		int stageNumber = model.getStage();
		// Set and display stage description based on the current stage
		stageDescriptionLabel = new Label(stageDescriptions[stageNumber - 1]);
		stageDescriptionLabel.setLayoutX(180);
		stageDescriptionLabel.setLayoutY(520);
		stageDescriptionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		root.getChildren().add(stageDescriptionLabel);

		// Create and position the count down label at the center
		centerCountdownLabel = new Label();
		centerCountdownLabel.setStyle("-fx-font-size: 60; -fx-text-fill: red; -fx-font-weight: bold;");
		centerCountdownLabel.setLayoutX((scene.getWidth() - 95) / 2);
		centerCountdownLabel.setLayoutY((scene.getHeight() - 180) / 2);
		root.getChildren().add(centerCountdownLabel);

		// Time line for the count down sequence
		Timeline countdownTimeline = new Timeline(
				new KeyFrame(Duration.seconds(0), event -> centerCountdownLabel.setText("  3")), // Display "3"
				new KeyFrame(Duration.seconds(1), event -> centerCountdownLabel.setText("  2")), // Display "2"
				new KeyFrame(Duration.seconds(2), event -> centerCountdownLabel.setText("  1")), // Display "1"
				new KeyFrame(Duration.seconds(3), event -> centerCountdownLabel.setText("GO!")), // Display "GO!"
				new KeyFrame(Duration.seconds(4), event -> {
					root.getChildren().remove(centerCountdownLabel); // Remove count down label
					startStageCountdown(timerSeconds); // Start the stage count down timer
				}));
		countdownTimeline.play(); // Begin count down
	}

	private void startStageCountdown(int seconds) {
		miniGameActive = true;
		topCountdownLabel = new Label(String.valueOf(seconds));
		topCountdownLabel.setStyle("-fx-font-size: 40; -fx-font-weight: bold;");
		topCountdownLabel.setLayoutX((scene.getWidth() - 35) / 2);
		topCountdownLabel.setLayoutY(10);
		root.getChildren().add(topCountdownLabel);

		Timeline stageTimeline = new Timeline();
		for (int i = 0; i <= seconds; i++) {
			int timeLeft = seconds - i;
			stageTimeline.getKeyFrames().add(
					new KeyFrame(Duration.seconds(i), event -> topCountdownLabel.setText(String.valueOf(timeLeft))));
		}
		stageTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(seconds), event -> {
			miniGameActive = false;
			root.getChildren().remove(topCountdownLabel);

			// Show the score list label after the stage time finishes.
			if (scoreListLabel != null) {
				scoreListLabel.setVisible(true);
			}

			if (stageDescriptionLabel != null) {
				root.getChildren().remove(stageDescriptionLabel);
				stageDescriptionLabel = null;
			}

			if (grassInstructionLabel != null) {
				root.getChildren().remove(grassInstructionLabel);
				grassInstructionLabel = null;
			}

			int stageNumber = model.getStage();
			int expectedSunValue = expectedSun[stageNumber - 1];
			int expectedWaterValue = expectedWater[stageNumber - 1];
			int expectedFertilizerValue = expectedFertilizer[stageNumber - 1];
			int stageScore = 100;
			stageScore -= 5
					* (Math.abs(expectedSunValue - sunClickCount) + Math.abs(expectedWaterValue - waterClickCount)
							+ Math.abs(expectedFertilizerValue - fertilizerClickCount));
			stageScore -= 5 * penaltyClickCount;
			if (stageScore < 0) {
				stageScore = 0;
			}

			if (grassList.size() > 0) {
				double penaltyPercent = Math.min(grassList.size() * 0.05, 0.20);
				stageScore = (int) (stageScore * (1 - penaltyPercent));
				grassList.clear();
			}

			model.setTotalScore(model.getTotalScore() + stageScore);

			// Update the running list of scores.
			initializeScoreListLabel();
			String currentListText = scoreListLabel.getText();
			currentListText += "Stage " + stageNumber + " Score: " + stageScore + "\n";
			if (stageNumber == 6) {
				int aggregateScore = model.getTotalScore() / 6;
				currentListText += "Overall Score: " + aggregateScore + "\n";
			}
			scoreListLabel.setText(currentListText);

			if (stageNumber == 1) {
				finalScoreLabel = new Label("🌱 The next stage is Seedling."
						+ "\nA small seedling grows leaves. It’s the baby plant stage.");
				waitingForNextStage = true;
				countdownStarted = false;
			} else if (stageNumber == 2) {
				finalScoreLabel = new Label("🌿 The next stage is Vegetative Stage."
						+ "\nThe plant grows taller, gets more leaves, and builds strength.");
				waitingForNextStage = true;
				countdownStarted = false;
			} else if (stageNumber == 3) {
				finalScoreLabel = new Label("🌼 The next stage is Flowering."
						+ "\nThe plant now grows flowers. Flowers are super important.");
				waitingForNextStage = true;
				countdownStarted = false;
			} else if (stageNumber == 4) {
				finalScoreLabel = new Label("🍅 The next stage is Fruiting."
						+ "\nThe flowers turn into fruits. Inside the fruit are new seeds.");
				waitingForNextStage = true;
				countdownStarted = false;
			} else if (stageNumber == 5) {
				finalScoreLabel = new Label(
						"🍓 The next stage is Ripening / Maturity." + "\nThe fruit changes color and gets sweeter.");
				waitingForNextStage = true;
				countdownStarted = false;
			} else {
				int aggregateScore = model.getTotalScore() / 6;
				String rating;
				if (aggregateScore < 60) {
					rating = "Uh-oh, your crops need some love!";
				} else if (aggregateScore < 80) {
					rating = "Not bad, farmer! You're doing a decent job.";
				} else {
					rating = "Amazing! You're a superstar farmer.";
				}
				finalScoreLabel = new Label(
						"🎉 Congratulations! " + "\nYou Completed All 6 Stages of Plant Growth! 🌟" + "\n" + rating);
				countdownStarted = false;
			}
			finalScoreLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
			finalScoreLabel.setLayoutX(180);
			finalScoreLabel.setLayoutY(520);
			root.getChildren().add(finalScoreLabel);

			// Display the centered score board using ScoreBoardObject.
			displayScoreBoard();

			// Show the corresponding action button based on game state.
			if (waitingForNextStage) {
				showActionButton("Start");
			} else if (model.getStage() == 6 && finalScoreLabel != null
					&& root.getChildren().contains(finalScoreLabel)) {
				MediaPlayer player = createMediaPlayer("/Game/PlantGrowthSimulator/complete.mp3", 0.6, 1);
				if (player != null) {
				    player.play();
				}
				showActionButton("Again");
			}
		}));

		stageTimeline.play();
	}

	// Creates and draws the centered score board using ScoreBoardObject.
	private void displayScoreBoard() {
		scoreBoardObject = new ScoreBoardObject(getGraphicsContext(), 590, 220);
		// Immediately draw the score board.
		scoreBoardObject.update();
	}

	// Starts the next stage (called when the action button is pressed after a stage
	// finishes).
	private void startNextStage() {
		if (finalScoreLabel != null && root.getChildren().contains(finalScoreLabel)) {
			root.getChildren().remove(finalScoreLabel);
		}
		if (stageDescriptionLabel != null) {
			root.getChildren().remove(stageDescriptionLabel);
			stageDescriptionLabel = null;
		}
		sunClickCount = 0;
		waterClickCount = 0;
		fertilizerClickCount = 0;
		penaltyClickCount = 0;

		grassSpawned = false;
		grassList.clear();
		if (grassInstructionLabel != null) {
			root.getChildren().remove(grassInstructionLabel);
			grassInstructionLabel = null;
		}

		redrawCanvas();

		// Advance stage and update plant image.
		model.advanceStage();
		int stageNumber = model.getStage();

		if (plant != null && stageNumber >= 2 && stageNumber <= 6) {
			FadeTransition fadeOut = Animation.createFadeTransition(plant.getNode(), 400, 1, 0);
			fadeOut.setOnFinished(e -> {
				plant.setStage(stageNumber - 1);
				Animation.createFadeTransition(plant.getNode(), 400, 0, 1).play();
			});
			fadeOut.play();
		}

		if (stageNumber <= 6) {
			countdownStarted = false;
			startMiniGame(stageTime[stageNumber - 1]);
		}
	}

	// Returns to plant selection and resets the score.
	public void goBackToPlantSelection() {
		model.resetGame();
		// Reset the total score in the model.
		model.setTotalScore(0);
		grassList.clear();
		if (grassInstructionLabel != null) {
			root.getChildren().remove(grassInstructionLabel);
			grassInstructionLabel = null;
		}
		if (plant != null) {
			root.getChildren().remove(plant.getNode());
			plant = null;
		}
		if (stageAdvanceLabel != null) {
			root.getChildren().remove(stageAdvanceLabel);
			stageAdvanceLabel = null;
		}
		// Reset the score list label so it will be created again.
		scoreListLabel = null;
		// Clear all children and reset click counters before showing game screen
		root.getChildren().clear();
		resetClickCounters();
		showGameScreen();
	}

	public Stage getStage() {
		return stage;
	}

	// Helper method for score list label.
	private void initializeScoreListLabel() {
		if (scoreListLabel == null) {
			scoreListLabel = new Label();
			scoreListLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
			scoreListLabel.setLayoutX(608);
			scoreListLabel.setLayoutY(240);
			root.getChildren().add(scoreListLabel);
		}
	}

	// Helper methods to show and handle the on-screen action button.
	private void showActionButton(String type) {
		// Remove any existing action button.
		if (actionButton != null) {
			root.getChildren().remove(actionButton);
			actionButton = null;
		}
		// Create the button using ButtonFactory ("Start" or "Again")
		actionButton = ButtonFactory.createButton(type);

		// Define separate offsets for "Start" and "Again".
		double xOffset = 0;
		double yOffset = 0;
		if (type.equals("Start")) {
			xOffset = 20;
			yOffset = 10;
		} else if (type.equals("Again")) {
			xOffset = -35;
			yOffset = 10;
		}

		// Position the button using the offsets.
		actionButton.setLayoutX((scene.getWidth() - 198) + xOffset);
		actionButton.setLayoutY((scene.getHeight() - 65) + yOffset);

		actionButton.setOnAction(e -> {
			 // Play click sound on action button press.
			MediaPlayer player = createMediaPlayer("/Game/PlantGrowthSimulator/button-click-sound.mp3", 0.2, 1);
			if (player != null) {
			    player.play();
			}
			handleActionButtonClick();
			root.getChildren().remove(actionButton);
			actionButton = null;
		});
		root.getChildren().add(actionButton);
	}

	private void handleActionButtonClick() {
		// Hide score list if visible.
		if (scoreListLabel != null) {
			scoreListLabel.setVisible(false);
		}
		// Remove the score board if it is active.
		if (scoreBoardObject != null) {
			scoreBoardObject = null;
			redrawCanvas();
		}

		if (waitingForStage1) {
			waitingForStage1 = false;
			int stageNumber = model.getStage();
			int requiredSun = expectedSun[stageNumber - 1];
			int requiredWater = expectedWater[stageNumber - 1];
			int requiredFertilizer = expectedFertilizer[stageNumber - 1];
			stageAdvanceLabel = new Label("Stage Challenges\nSun: " + requiredSun + "\nWater: " + requiredWater
					+ "\nFertilizer: " + requiredFertilizer);
			stageAdvanceLabel.setLayoutX(8);
			stageAdvanceLabel.setLayoutY(1);
			stageAdvanceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
			root.getChildren().add(stageAdvanceLabel);
			startMiniGame(stageTime[0]);
		} else if (waitingForNextStage) {
			waitingForNextStage = false;
			if (stageAdvanceLabel != null) {
				root.getChildren().remove(stageAdvanceLabel);
			}
			startNextStage();
			int stageNumber = model.getStage();
			int requiredSun = expectedSun[stageNumber - 1];
			int requiredWater = expectedWater[stageNumber - 1];
			int requiredFertilizer = expectedFertilizer[stageNumber - 1];
			stageAdvanceLabel = new Label("Stage Challenges\nSun: " + requiredSun + "\nWater: " + requiredWater
					+ "\nFertilizer: " + requiredFertilizer);
			stageAdvanceLabel.setLayoutX(8);
			stageAdvanceLabel.setLayoutY(1);
			stageAdvanceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
			root.getChildren().add(stageAdvanceLabel);
		} else if (model.getStage() == 6 && finalScoreLabel != null && root.getChildren().contains(finalScoreLabel)) {
			goBackToPlantSelection();
		}
	}
}

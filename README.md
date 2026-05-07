# Plant Growth Simulator

Plant Growth Simulator is a Java-based educational game developed as part of an assignment for the Design Pattern module. The game is designed for primary school students and introduces basic plant growth concepts through simple interactive gameplay, animations, and visual feedback.

The project demonstrates the use of object-oriented programming principles and several software design patterns to create a more organized, maintainable, and modular application structure.

## Project Purpose

This project was created for academic learning purposes as part of a Design Pattern module assignment. The main purpose of the project is to apply software design patterns in a practical Java game application.

The game allows users to interact with a virtual plant by applying sunlight, water, and fertilizer. These actions help the plant grow through different stages while giving users a simple and engaging learning experience.

## Design Patterns Used

This project applies the following design patterns:

### 1. Model-View-Controller (MVC)

The MVC pattern is used to separate the game logic, user interface, and user input handling.

- **Model**: Handles the game data and logic, such as plant stage, selected plant type, and score.
- **View**: Handles the visual display, animations, buttons, images, and game interface.
- **Controller**: Handles user interactions and connects the model with the view.

Using MVC makes the project easier to maintain because the game logic and interface are separated clearly.

### 2. Command Pattern

The Command pattern is used to handle user actions in the game, such as:

- Giving sunlight to the plant
- Watering the plant
- Applying fertilizer

Each action is represented as a command class. This makes the actions easier to manage, modify, and extend in the future.

For example, commands such as `SunCommand`, `WaterCommand`, and `FertilizerCommand` are used to perform specific plant interaction actions.

### 3. Factory Pattern

The Factory pattern is used through the `ButtonFactory` class to create different types of buttons in the game.

Instead of creating and styling buttons repeatedly in different parts of the program, the button creation process is centralized in one class. This helps ensure that all buttons have consistent styling, images, sizes, and animations.

This also reduces code repetition and makes it easier to add or update buttons in the future.

## GameObject Structure

A key requirement of the assignment is that every object drawn on the screen extends the provided `GameObject` class.

Several game objects extend this base class, including:

- `GrassObject`
- `FarmerObject`
- `PlantObject`
- `TextBoxObject`
- `TitleObject`
- `ScoreBoardObject`

This structure allows all drawable objects to share a common foundation while still allowing each subclass to define its own specific behavior.

## Features

- Simple educational game for kids
- Interactive plant growth simulation
- Child-friendly gameplay and interface
- Basic plant growth learning concept
- Plant growth stages with visual feedback
- User actions such as sunlight, water, and fertilizer
- Score and progress tracking
- JavaFX-based graphical interface
- Demonstrates object-oriented programming concepts
- Applies MVC, Command, and Factory design patterns
- Runnable JAR file included for easy testing

## Screenshot

![Plant Growth Simulator](screenshots/javaw_NcGNYxmx1l.png)

## Technologies Used

- Java
- JavaFX
- Maven
- Eclipse

## License

This project is licensed under the MIT License. See the `LICENSE` file for more details.

## How to Run

Download the JAR file from the `release` folder.

Run the following command:



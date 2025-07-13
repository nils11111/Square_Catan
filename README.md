# Square Catan - The Settlers of Catan

A Java implementation of the classic board game "The Settlers of Catan" using square tiles instead of hexagonal tiles, built with JavaFX for the graphical user interface.

## Overview

This project is a complete implementation of the 1995 board game "The Settlers of Catan" by Klaus Teuber. The game has been adapted to use square tiles instead of hexagonal tiles (as allowed by the project requirements for a 7-point deduction). The application provides a fully functional, intuitive, and maintainable Java-based implementation with a modern graphical user interface.

## Features

### Core Game Features
- **Complete Catan Game Rules**: Implements all rules from the original 1995 game
- **Square Tile Board**: 6x6 grid with 36 tiles (19 playable tiles + 17 additional tiles)
- **Resource Management**: Wood, Brick, Ore, Grain, and Wool resources
- **Building System**: Settlements, Cities, and Roads with proper resource costs
- **Dice Rolling**: Two-dice system with resource production
- **Player Management**: Support for 2-4 players
- **Victory Conditions**: First player to reach 10 victory points wins
- **Setup Phase**: Proper forward and backward settlement/road placement
- **Trade System**: Player-to-player resource trading

### User Interface Features
- **Modern JavaFX GUI**: Clean, intuitive interface
- **Visual Game Board**: Color-coded tiles with resource legends
- **Interactive Building**: Click-to-build settlements, cities, and roads
- **Real-time Updates**: Live display of resources, buildings, and game status
- **Player Information Panel**: Detailed view of all players' resources and buildings
- **Game Controls**: Dice rolling, trading, and turn management
- **Status Display**: Current player, phase, and game progress

### Technical Features
- **Object-Oriented Design**: Clean separation of model, view, and controller
- **Extensible Architecture**: Easy to add new features and modifications
- **Error Handling**: Comprehensive validation and user feedback
- **Resource Management**: Automatic resource cost checking and deduction
- **Game State Management**: Proper phase transitions and turn management

## Project Structure

```
src/main/java/com/catan/
├── Main.java                 # Application entry point
├── model/                    # Game logic and data models
│   ├── GameBoard.java        # Board representation and tile management
│   ├── GameState.java        # Overall game state and turn management
│   ├── Player.java           # Player data and resource management
│   ├── Vertex.java           # Corner points for settlements/cities
│   ├── Edge.java             # Road placement locations
│   ├── TerrainType.java      # Terrain types and resource production
│   ├── ResourceType.java     # Resource definitions
│   └── BuildingCosts.java    # Building cost definitions
└── ui/                       # User interface components
    ├── GameWindow.java       # Main application window
    ├── GameBoardView.java    # Visual game board representation
    ├── PlayerInfoPanel.java  # Player information display
    └── ControlPanel.java     # Game controls and actions
```

## Game Rules Implementation

### Setup Phase
1. **Forward Round**: Players 1→2→3→4 place one settlement and one road each
2. **Backward Round**: Players 4→3→2→1 place one settlement and one road each
3. **Resource Collection**: Each player receives resources from tiles adjacent to their second settlement

### Main Game Phase
1. **Dice Rolling**: Current player rolls two dice
2. **Resource Production**: All players with settlements/cities on tiles with the rolled number receive resources
3. **Building Phase**: Players can build settlements, cities, and roads using collected resources
4. **Trading**: Players can trade resources with each other
5. **Turn Progression**: Play passes to the next player

### Building Costs
- **Settlement**: 1 Wood + 1 Brick + 1 Grain + 1 Wool
- **City**: 2 Grain + 3 Ore (upgrades existing settlement)
- **Road**: 1 Wood + 1 Brick

### Victory Conditions
- First player to reach 10 victory points wins
- Settlements: 1 victory point each
- Cities: 2 victory points each (1 additional point when upgrading from settlement)

## Installation and Setup

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher
- JavaFX (included in Java 11+)

### Building the Project
```bash
# Clone the repository
git clone <repository-url>
cd Square_Catan

# Build the project
mvn clean compile

# Run the application
mvn javafx:run
```

### Alternative: Using JAR File
```bash
# Create executable JAR
mvn clean package

# Run the JAR file
java -jar target/Square_Catan-1.0.jar
```

## How to Play

### Starting a Game
1. Launch the application
2. The game automatically starts with 4 players
3. The setup phase begins automatically

### Setup Phase
1. **Forward Round**: Each player places one settlement and one road
   - Click on a corner (circle) to place a settlement
   - Click on a road (thick line) to place a road
   - The game automatically switches to the next player after both are placed

2. **Backward Round**: Each player places one more settlement and road
   - Same process as forward round
   - After completion, the main game phase begins

### Main Game Phase
1. **Roll Dice**: Click the "🎲 ROLL DICE 🎲" button
2. **Collect Resources**: Resources are automatically distributed based on dice roll
3. **Build**: Click on corners or roads to build (if you have sufficient resources)
4. **Trade**: Use the "Trade" button to exchange resources with other players
5. **End Turn**: Turn automatically passes to the next player after dice rolling

### Building Rules
- **Settlements**: Can be built on any unoccupied corner
- **Cities**: Can only be built by upgrading your own settlements
- **Roads**: Must connect to your existing buildings or road network (except in setup phase)

### Resource Production
- **Forest**: Produces Wood
- **Hills**: Produces Brick
- **Mountains**: Produces Ore
- **Fields**: Produces Grain
- **Pasture**: Produces Wool
- **Desert**: Produces no resources

## Technical Architecture

### Design Patterns
- **Model-View-Controller (MVC)**: Clear separation of game logic and UI
- **Observer Pattern**: UI components update automatically when game state changes
- **Factory Pattern**: Used for creating game board elements
- **Strategy Pattern**: Different building validation strategies for setup vs. play phases

### Key Classes and Responsibilities

#### Model Layer
- **GameState**: Manages overall game flow, turns, and phase transitions
- **GameBoard**: Represents the game board, tiles, vertices, and edges
- **Player**: Manages player resources, buildings, and victory points
- **Vertex/Edge**: Represent building locations and road connections

#### View Layer
- **GameWindow**: Main application window and layout management
- **GameBoardView**: Visual representation of the game board
- **PlayerInfoPanel**: Displays player information and resources
- **ControlPanel**: Game controls and user actions

#### Controller Layer
- **GameState**: Acts as controller, managing game logic and user actions
- **Event Handlers**: Handle user interactions and update the model

### Data Flow
1. User interacts with UI components
2. UI components trigger events
3. GameState processes events and updates model
4. Model changes trigger UI updates
5. UI reflects new game state

## Extensibility and Maintenance

### Adding New Features
The codebase is designed for easy extension:

1. **New Resources**: Add to `ResourceType` enum and update `TerrainType` mappings
2. **New Buildings**: Add to `BuildingType` enum and define costs in `BuildingCosts`
3. **New Game Rules**: Extend `GameState` with new logic
4. **UI Enhancements**: Add new components to the UI package

### Code Quality
- **Comprehensive JavaDoc**: All public methods and classes documented
- **Clean Code Principles**: Meaningful names, small methods, single responsibility
- **Error Handling**: Proper validation and user feedback
- **Modular Design**: Easy to test and maintain individual components

## Testing

### Manual Testing Scenarios
1. **Setup Phase**: Verify proper forward/backward settlement placement
2. **Resource Production**: Test dice rolling and resource distribution
3. **Building Validation**: Test resource costs and placement rules
4. **Trade System**: Test player-to-player trading
5. **Victory Conditions**: Test win detection and game end

### Automated Testing
The project includes unit tests for core game logic:
```bash
# Run tests
mvn test
```

## Performance Considerations

- **Efficient Board Representation**: 2D arrays for fast tile/vertex/edge access
- **Minimal UI Updates**: Only update changed components
- **Memory Management**: Proper cleanup of game objects
- **Responsive UI**: Non-blocking operations for smooth user experience

## Known Limitations

1. **No AI Players**: Computer opponents are not implemented (as per requirements)
2. **No Network Play**: Multiplayer is local only
3. **No Development Cards**: Special cards are not implemented (as per requirements)
4. **No Sound Effects**: Audio features are not implemented (as per requirements)
5. **Square Tiles**: Uses square tiles instead of hexagonal tiles (7-point deduction)

## Future Enhancements

Potential improvements for future versions:
1. **AI Players**: Add computer opponents with different difficulty levels
2. **Network Multiplayer**: Enable online play
3. **Development Cards**: Implement special cards and abilities
4. **Expansions**: Add "Cities & Knights" or "Seafarers" expansions
5. **Save/Load**: Game state persistence
6. **Statistics**: Player performance tracking
7. **Customizable Rules**: Configurable game parameters

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- **Original Game**: "The Settlers of Catan" by Klaus Teuber (1995)
- **JavaFX**: Modern Java GUI framework
- **Maven**: Build and dependency management
- **Open Source Community**: Various libraries and tools used in development

## Contact

For questions, issues, or contributions, please contact the development team or create an issue in the project repository.

---

**Note**: This implementation follows the original 1995 Catan rules with the specified simplifications and uses square tiles instead of hexagonal tiles as allowed by the project requirements. 
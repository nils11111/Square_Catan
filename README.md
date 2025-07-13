# Square Catan - Die Siedler von Catan

Ein Java-basiertes Catan-Spiel mit quadratischen Feldern statt Hexagonen, entwickelt mit JavaFX.

## Beschreibung

Dieses Spiel ist eine digitale Umsetzung des klassischen Brettspiels "Die Siedler von Catan" (1995) mit folgenden Anpassungen:
- Quadratische Felder statt Hexagone
- Vereinfachte Regeln (ohne Entwicklungskarten, Sonderkarten, Erweiterungen)
- 2-4 Spieler
- Deutsche Benutzeroberfläche

## Spielregeln

### Ziel
Als erster Spieler 10 Siegpunkte erreichen.

### Siegpunkte
- Siedlung: 1 Siegpunkt
- Stadt: 1 Siegpunkt (ersetzt Siedlung)

### Ressourcen
- 🌲 Holz (Wald)
- 🧱 Lehm (Hügel)
- ⛏️ Erz (Berge)
- 🌾 Getreide (Felder)
- 🐑 Wolle (Weiden)

### Gebäude
- **Siedlung**: Kostet 1 Holz, 1 Lehm, 1 Getreide, 1 Wolle
- **Stadt**: Kostet 2 Getreide, 3 Erz
- **Straße**: Kostet 1 Holz, 1 Lehm

## Installation und Ausführung

### Voraussetzungen
- Java 17 oder höher
- Maven 3.6 oder höher

### Kompilierung und Ausführung

1. **Projekt klonen oder herunterladen**

2. **Maven-Abhängigkeiten installieren:**
   ```bash
   mvn clean install
   ```

3. **Spiel starten:**
   ```bash
   mvn javafx:run
   ```

   Oder alternativ:
   ```bash
   java -jar target/square-catan-1.0.0.jar
   ```

## Spielanleitung

1. **Aufbauphase**: Jeder Spieler platziert seine ersten Siedlungen und Straßen
2. **Hauptspiel**: 
   - Würfeln und Ressourcen sammeln
   - Gebäude bauen
   - Mit anderen Spielern handeln
   - Zum nächsten Spieler weitergehen

## Projektstruktur

```
src/main/java/com/catan/
├── Main.java                 # Hauptanwendung
├── model/                    # Spiellogik
│   ├── GameState.java       # Spielzustand
│   ├── GameBoard.java       # Spielbrett
│   ├── Player.java          # Spieler
│   ├── ResourceType.java    # Ressourcentypen
│   └── TerrainType.java     # Geländetypen
└── ui/                      # Benutzeroberfläche
    ├── GameWindow.java      # Hauptfenster
    ├── GameBoardView.java   # Spielbrett-Anzeige
    ├── PlayerInfoPanel.java # Spielerinformationen
    └── ControlPanel.java    # Steuerungselemente
```

## Technische Details

- **Framework**: JavaFX 17
- **Build-Tool**: Maven
- **Java-Version**: 17
- **Architektur**: MVC-Pattern

## Entwicklung

### Hinzufügen neuer Features
1. Erweitern Sie die entsprechenden Model-Klassen
2. Aktualisieren Sie die UI-Komponenten
3. Testen Sie die Funktionalität

### Bekannte Einschränkungen
- Vereinfachte Ressourcenverteilung
- Keine Computergegner
- Keine Netzwerkfunktionalität
- Keine Entwicklungskarten

## Lizenz

Dieses Projekt wurde für Bildungszwecke entwickelt.

## Credits

Basierend auf dem Brettspiel "Die Siedler von Catan" von Klaus Teuber (1995). 
# 🎮 Super Mario Java

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)

[🇪🇸 Versión en Español](README_ES.md)

## Description

**Super Mario Java** is a recreation of the classic Super Mario Bros game developed entirely in Java using Swing and AWT. This project is an educational implementation that recreates the mechanics, physics, and gameplay of Nintendo's iconic platform game developed in 80s.

## Game Objective

The objective is to control Mario through a level full of obstacles and enemies to reach the flag at the end of the level. During the journey, the player must:

- **Defeat enemies** by jumping on them or using fireballs
- **Collect coins** by hitting special blocks
- **Obtain power-ups** to increase Mario's abilities
- **Complete the level** before time runs out
- **Survive** with a 3-life system

## ✨ Features

### 🎮 Game Mechanics

- **Realistic physics system** with gravity and precise collisions
- **Three Mario states**:
  - 🟥 **Small Mario**: Initial and vulnerable state
  - 🟦 **Super Mario**: Big Mario after collecting a mushroom
  - 🔥 **Fire Mario**: Can throw fireballs after collecting a fire flower
  
- **Smooth controls**:
  - Left/right movement (A/D or arrows)
  - Jump (W/Up arrow/Space)
  - Sprint (Shift)
  - Crouch (S/Down arrow - only when big)
  - Throw fireballs (X - Fire mode only)
  - Pause (ESC/P)

### 👾 Enemies and Obstacles

- **Goombas**: Classic enemies that patrol the level
- **Collision detection**: Precise hitbox system
- **Enemy death**: With animation and sound effect
- **Score system**: Defeated enemy counter

### 🎁 Power-Ups

- **Super Mushroom**: Makes Mario grow
- **Fire Flower**: Grants the ability to throw fireballs
- **Spawning animations**: Power-ups emerge from blocks
- **Independent physics**: Mushrooms move, flowers remain static

### 🧱 Block System

- **Lucky Blocks** (blocks with ?) - Contain coins or power-ups
- **Brick blocks** - Can be broken when Mario is big
- **Bump animation**: Blocks jump when hit
- **Destruction particles**: Visual effects when breaking bricks
- **Pipes**: Decorative obstacles with collision

### 🎵 Audio

- **Background music**: Classic Ground Theme
- **Sound effects**:
  - Jump (small and big)
  - Coin collection
  - Power-up
  - Block breaking
  - Enemy death
  - Player death
  - Game Over
  - Stage Clear
  - Time warning (Hurry Up!)

### 🎨 Graphics and Animations

- **Animated sprites** for all Mario actions
- **Camera system** that follows the player
- **Smooth animations** at 60 FPS
- **Particles and visual effects**:
  - Brick fragments
  - Coin animation
  - Fireballs with trail
- **User interface** with custom retro font (Press Start 2P)

### 🎯 Game System

- **Life system**: 3 lives with transition screen
- **Timer**: 400 seconds to complete the level
- **Coin counter**: Tracks collected coins
- **Temporary invulnerability**: After taking damage
- **Game Over**: Final screen when losing all lives
- **Victory**: Level completion animation with flag and castle
- **Main menu**:
  - Start game
  - Choose player name
  - Exit

### 🗺️ Level Design

- **Extensive world**: 204 × 15 tile map
- **Horizontal scrolling**: Camera follows the player
- **Decorative elements**: Clouds, sky, castle
- **End flag**: Visual level objective
- **Varied design**: Platforms, stairs, pipes

## 🛠️ How It Was Made

### Project Architecture

The project follows an object-oriented architecture organized in packages:

#### 📦 `Main` Package
- **`Main.java`**: Application entry point
- **`GamePanel.java`**: Main game panel, handles game loop and rendering
- **`Menu.java`**: Main menu system
- **`KeyHandler.java`**: Keyboard input management
- **`SoundManager.java`**: Sound effects system
- **`MusicManager.java`**: Background music system
- **`SpriteManager.java`**: Sprite and graphic resource manager

#### 📦 `Entity` Package
- **`Player.java`**: Player logic, physics, animations
- **`Enemy.java`**: Enemy behavior
- **`PowerUp.java`**: Power-ups (mushrooms and flowers)
- **`Fireball.java`**: Fire Mario projectiles
- **`BrickParticle.java`**: Broken brick particles
- **`CoinAnimation.java`**: Coin animation
- **`BlockBump.java`**: Hit block animation
- **`FlagPole.java`**: End level flag
- **`Castle.java`**: Goal castle

#### 📦 `tile` Package
- **`TileManager.java`**: Tile and map system
- **`Tile.java`**: Individual tile class

### Technologies Used

- **Language**: Java 8+
- **Graphics**: Java Swing (JPanel, Graphics2D)
- **Audio**: Java Sound API (javax.sound.sampled)
- **Images**: ImageIO for PNG and GIF loading
- **Fonts**: TrueType Font (Press Start 2P)

### Technical Features

- **Game Loop**: 60 FPS implementation with delta time
- **Collision system**: Rectangle bounds based
- **Custom physics**: Gravity, velocity, and acceleration
- **Tile-based rendering**: Tile-based rendering system
- **Rendering optimization**: Only draws tiles visible in camera
- **Resource management**: Efficient sprite and sound loading
- **Concurrent programming**: Threads for audio and game loop

### Physics System

```java
- Gravity: 0.26
- Jump velocity: -10
- Max fall speed: 10.0
- Normal speed: 6
- Sprint speed: 10
```

### Collision System

- Tile-based collision detection
- Player-enemy collisions
- Player-power-up collisions
- Projectile-enemy collisions
- Block collision detection

## 🎮 System Requirements

- Java Development Kit (JDK) 8 or higher
- Operating System: Windows, macOS, or Linux
- Minimum RAM: 256 MB
- Disk space: 50 MB

## 🚀 Installation and Execution

### Compile from source code

```bash
# Navigate to project directory
cd Super-Mario-Java/src

# Compile
javac Main/Main.java

# Run
java Main.Main
```

### Run from JAR (if available)

```bash
java -jar SuperMarioJava.jar
```

## 🎮 Controls

| Action | Keys |
|--------|------|
| Move left | A / ← |
| Move right | D / → |
| Jump | W / ↑ / Space |
| Crouch | S / ↓ |
| Run | Shift |
| Shoot fireball | X |
| Pause | ESC / P |
| Enter (menus) | Enter |

## 👥 Authors

- **Facundo Nicolas Gandolfo**
- **Lautaro Uriel Borges Cardoso**
- **Benjamin Salomon Paredes**

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

Copyright (c) 2024 Facundo Nicolas Gandolfo

## 🙏 Acknowledgments

- Nintendo for the original Super Mario Bros game
- Sprite and sound resources from the community
- Teachers who supported during the project development

## 📸 Screenshots

_The game faithfully recreates the classic Super Mario Bros experience with graphics, sounds, and mechanics from the original game._

---

⭐ If you like this project, give it a star on GitHub!

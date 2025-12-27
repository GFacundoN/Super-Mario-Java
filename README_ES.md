# 🎮 Super Mario Java

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)

## 📝 Descripción

**Super Mario Java** es una recreación del clásico juego Super Mario Bros desarrollada completamente en Java utilizando Swing y AWT. Este proyecto es una implementación educativa que recrea la mecánica, física y jugabilidad del icónico juego de plataformas de Nintendo desarrollado en los 80.

## 🎯 Objetivo del Juego

El objetivo es controlar a Mario a través de un nivel lleno de obstáculos y enemigos para llegar hasta la bandera al final del nivel. Durante el recorrido, el jugador debe:

- **Derrotar enemigos** saltando sobre ellos o usando bolas de fuego
- **Recolectar monedas** golpeando bloques especiales
- **Obtener power-ups** para aumentar las habilidades de Mario
- **Completar el nivel** antes de que se acabe el tiempo
- **Sobrevivir** con un sistema de 3 vidas

## ✨ Características

### 🎮 Mecánicas de Juego

- **Sistema de física realista** con gravedad y colisiones precisas
- **Tres estados de Mario**:
  - 🟥 **Mario pequeño**: Estado inicial y vulnerable
  - 🟦 **Super Mario**: Mario grande después de recoger un hongo
  - 🔥 **Fire Mario**: Puede lanzar bolas de fuego tras recoger una flor de fuego
  
- **Controles fluidos**:
  - Movimiento izquierda/derecha (A/D o flechas)
  - Salto (W/Flecha arriba/Espacio)
  - Sprint (Shift)
  - Agacharse (S/Flecha abajo - solo cuando es grande)
  - Lanzar bolas de fuego (X - solo en modo Fire)
  - Pausa (ESC/P)

### 👾 Enemigos y Obstáculos

- **Goombas**: Enemigos clásicos que patrullan el nivel
- **Detección de colisiones**: Sistema preciso de hitboxes
- **Muerte del enemigo**: Con animación y efecto de sonido
- **Sistema de puntos**: Contador de enemigos eliminados

### 🎁 Power-Ups

- **Super Hongo**: Hace crecer a Mario
- **Flor de Fuego**: Otorga la habilidad de lanzar bolas de fuego
- **Animaciones de spawning**: Los power-ups emergen de los bloques
- **Física independiente**: Los hongos se mueven, las flores permanecen estáticas

### 🧱 Sistema de Bloques

- **Lucky Blocks** (bloques con ?) - Contienen monedas o power-ups
- **Bloques de ladrillo** - Pueden romperse cuando Mario es grande
- **Animación de bump**: Los bloques saltan al ser golpeados
- **Partículas de destrucción**: Efectos visuales al romper ladrillos
- **Tuberías**: Obstáculos decorativos con colisión

### 🎵 Audio

- **Música de fondo**: Tema clásico del Ground Theme
- **Efectos de sonido**:
  - Salto (pequeño y grande)
  - Recolección de monedas
  - Power-up
  - Rotura de bloques
  - Muerte del enemigo
  - Muerte del jugador
  - Game Over
  - Stage Clear
  - Advertencia de tiempo (Hurry Up!)

### 🎨 Gráficos y Animaciones

- **Sprites animados** para todas las acciones de Mario
- **Sistema de cámara** que sigue al jugador
- **Animaciones fluidas** a 60 FPS
- **Partículas y efectos visuales**:
  - Fragmentos de ladrillos
  - Animación de monedas
  - Bolas de fuego con trail
- **Interfaz de usuario** con fuente retro personalizada (Press Start 2P)

### 🎯 Sistema de Juego

- **Sistema de vidas**: 3 vidas con pantalla de transición
- **Temporizador**: 400 segundos para completar el nivel
- **Contador de monedas**: Rastrea las monedas recolectadas
- **Invulnerabilidad temporal**: Después de recibir daño
- **Game Over**: Pantalla final al perder todas las vidas
- **Victoria**: Animación de completar nivel con bandera y castillo
- **Menú principal**:
  - Empezar juego
  - Elegir nombre del jugador
  - Salir

### 🗺️ Diseño de Nivel

- **Mundo extenso**: Mapa de 204 × 15 tiles
- **Scroll horizontal**: Cámara que sigue al jugador
- **Elementos decorativos**: Nubes, cielo, castillo
- **Bandera al final**: Objetivo visual del nivel
- **Diseño variado**: Plataformas, escaleras, tuberías

## 🛠️ Cómo se Hizo

### Arquitectura del Proyecto

El proyecto sigue una arquitectura orientada a objetos organizada en paquetes:

#### 📦 Paquete `Main`
- **`Main.java`**: Punto de entrada de la aplicación
- **`GamePanel.java`**: Panel principal del juego, maneja el game loop y renderizado
- **`Menu.java`**: Sistema de menú principal
- **`KeyHandler.java`**: Gestión de entrada del teclado
- **`SoundManager.java`**: Sistema de efectos de sonido
- **`MusicManager.java`**: Sistema de música de fondo
- **`SpriteManager.java`**: Gestor de sprites y recursos gráficos

#### 📦 Paquete `Entity`
- **`Player.java`**: Lógica del jugador, física, animaciones
- **`Enemy.java`**: Comportamiento de enemigos
- **`PowerUp.java`**: Power-ups (hongos y flores)
- **`Fireball.java`**: Proyectiles de Fire Mario
- **`BrickParticle.java`**: Partículas de ladrillos rotos
- **`CoinAnimation.java`**: Animación de monedas
- **`BlockBump.java`**: Animación de bloques golpeados
- **`FlagPole.java`**: Bandera del final del nivel
- **`Castle.java`**: Castillo objetivo

#### 📦 Paquete `tile`
- **`TileManager.java`**: Sistema de tiles y mapas
- **`Tile.java`**: Clase individual de tile

### Tecnologías Utilizadas

- **Lenguaje**: Java 8+
- **Gráficos**: Java Swing (JPanel, Graphics2D)
- **Audio**: Java Sound API (javax.sound.sampled)
- **Imágenes**: ImageIO para carga de PNG y GIF
- **Fuentes**: TrueType Font (Press Start 2P)

### Características Técnicas

- **Game Loop**: Implementación a 60 FPS con delta time
- **Sistema de colisiones**: Basado en Rectangle bounds
- **Física personalizada**: Gravedad, velocidad y aceleración
- **Tile-based rendering**: Sistema de renderizado basado en tiles
- **Optimización de renderizado**: Solo dibuja tiles visibles en cámara
- **Gestión de recursos**: Carga eficiente de sprites y sonidos
- **Programación concurrente**: Threads para audio y game loop

### Sistema de Física

```java
- Gravedad: 0.26
- Velocidad de salto: -10
- Velocidad máxima de caída: 10.0
- Velocidad normal: 6
- Velocidad sprint: 10
```

### Sistema de Colisiones

- Detección de colisiones tile-based
- Colisiones jugador-enemigo
- Colisiones jugador-power-up
- Colisiones proyectil-enemigo
- Detección de colisiones con bloques

## 🎮 Requisitos del Sistema

- Java Development Kit (JDK) 8 o superior
- Sistema operativo: Windows, macOS o Linux
- RAM mínima: 256 MB
- Espacio en disco: 50 MB

## 🚀 Instalación y Ejecución

### Compilar desde código fuente

```bash
# Navegar al directorio del proyecto
cd Super-Mario-Java/src

# Compilar
javac Main/Main.java

# Ejecutar
java Main.Main
```

### Ejecutar desde JAR (si está disponible)

```bash
java -jar SuperMarioJava.jar
```

## 🎮 Controles

| Acción | Teclas |
|--------|--------|
| Mover izquierda | A / ← |
| Mover derecha | D / → |
| Saltar | W / ↑ / Espacio |
| Agacharse | S / ↓ |
| Correr | Shift |
| Disparar bola de fuego | X |
| Pausa | ESC / P |
| Enter (menús) | Enter |

## 👥 Autores

- **Facundo Nicolas Gandolfo**
- **Lautaro Uriel Borges Cardoso**
- **Benjamin Salomon Paredes**

## 📄 Licencia

Este proyecto está licenciado bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

Copyright (c) 2024 Facundo Nicolas Gandolfo

## 🙏 Agradecimientos

- Nintendo por el juego original Super Mario Bros
- Recursos de sprites y sonidos de la comunidad
- Profesores que apoyaron durante el desarrollo del proyecto

## 📸 Capturas de Pantalla

_El juego recrea fielmente la experiencia clásica de Super Mario Bros con gráficos, sonidos y mecánicas del juego original._

---

⭐ Si te gusta este proyecto, ¡dale una estrella en GitHub!

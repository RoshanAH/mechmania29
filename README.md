<div align="center">

<a href="https://mechmania.org"><img width="25%" src="https://github.com/MechMania-29/Website/blob/main/images/mm29_logo.png" alt="MechMania 29"></a>

### [website](https://mechmania.org) | [python-starterpack](https://github.com/MechMania-29/python-starterpack) | java-starterpack | [visualizer](https://github.com/MechMania-29/visualizer) | [engine](https://github.com/MechMania-29/engine) | [wiki](https://github.com/MechMania-29/Wiki)

# MechMania Java Starterpack

Welcome to MechMania! The java starterpack will allow you to write a java bot to compete against others.
Two bots will be faced against each other, and then the [engine](https://github.com/MechMania-29/engine) will run a simulation to see who wins!
After the engine runs you'll get a gamelog (a large json file) that you can use in the [visualizer](https://github.com/MechMania-29/visualizer) to
visualize the game's progress and end result.

</div>

---

## Installation

To start, make sure you have Java 17+ installed. You can check by running:

```sh
java --version
```

You'll also need Python 3.9+, which you can check by running:

```sh
python --version
```

To install the engine, run:

```sh
python engine.py
```

and you should see an engine.jar appear in engine/engine.jar!

If you don't, you can manually install it by following the instructions on the [engine](https://github.com/MechMania-29/engine) page.

## Getting started

If you haven't read the [wiki](https://github.com/MechMania-29/Wiki) yet, do that first! This starterpack provides the basics for you to get started. All the files you need to worry about editing are located in the `strategy/` directory. `ChooseStrategy.java` will select the specific strategy to use. You can use this to select different strategies based on whether you're a zombie or not. Each strategy has to implement 4 functions which will determine how your bot responds in each phase. Let's explain them.
- `public Map<CharacterClassType, Integer> decideCharacterClasses(List<CharacterClassType> possibleClasses, int numToPick, int maxPerSameClass)`
  - This function will return what classes you'll select when you're the human.
  - `possibleClasses` gives you the list of possible classes you can choose from, `numToPick` gives the total number you can pick, and `maxPerSameClass` defines the max of how many characters can be in the same class.
  - You will return a dictionary pairing `CharacterClassType` to the count you want of that.
  - For example, if you wanted 5 medics, you could simply do:
  - ```java
    return Map.of(
      CharacterClassType.MEDIC, 5
    )
    ```
- `public List<MoveAction> decideMoves(Map<String, List<MoveAction>> possibleMoves, GameState gameState)`
  - This function will return the moves that each character will make.
  - `possibleMoves` maps each character id to it's possible MoveActions it can take. You can use this to validate if a move is possible, or pick from this list.
  - `gameState` is the current state of all characters and terrain on the map, you can use this to inform which move you want to make for each character
  - A MoveAction just defines where the character will end up. You don't have to compute the possible moves manually - we give you the possible ones.
  - A character id is just a unqiue string that represents a specific character. You can get a character by id with `gameState.characters().get(id)` -> `Character`
  - You will return a list of moves to take, which should effectively be a move for each character.
- `public List<AttackAction> decideAttacks(Map<String, List<AttackAction>> possibleAttacks, GameState gameState)`
  - This function will return the attacks that each character will make.
  - `possibleAttacks` maps each character id to it's possible AttackActions it can take. You can use this to validate if a move is possible, or pick from this list.
  - `gameState` is the same as above. Use it to inform your actions.
  - An AttackAction can be on terrain or a character, so be careful not to just attack everything. See the file it's defined in for more info.
  - You will return a list of attacks to make, which should be a attack for each character that can attack.
- `public List<AbilityAction> decideAbilities(Map<String, List<AbilityAction>> possibleAbilities, GameState gameState)`
  - This function will return the abilities that each character will make.
  - `possibleAbilities` maps each character id to it's possible AbilityActions it can take.
  - `gameState` is same as above.
  - An AbilityAction can be building a barricade or healing. Use type to determine which.
  - Healing targets a character and building targets a position, so consider that accordingly.

**Several useful tips:**
- Read the docs! Reading the wiki is really important as well as the rest of this README. Don't make this harder!
- All code for MechMania is open source, take advantage of that! For example, [the map can be found on the engine](https://github.com/MechMania-29/engine/blob/main/src/main/resources/maps/main.json).
- You [only have 2.5 seconds](https://github.com/MechMania-29/engine/blob/main/src/main/java/mech/mania/engine/Config.java#L112) to make a decision for each phase! Don't try anything too complicated. (O^4 = bad)
- You cannot import any external libraries.

## Usage

To run your client, you have two options:

You can build & then run directly with java, like:

```sh
./gradlew build
java -jar build/libs/starterpack.jar [commands here]
```

or run with gradle directly:
```sh
./gradlew run --args "[commands here]"
```

We'll show the longer version in these examples but keep in mind you can do both.

### Run your bot against itself

```sh
./gradlew build
java -jar build/libs/starterpack.jar run self
```

### Run your bot against the human computer (your bot plays zombies)

```sh
./gradlew build
java -jar build/libs/starterpack.jar run humanComputer
```

### Run your bot against the zombie computer (your bot plays humans)

```sh
./gradlew build
java -jar build/libs/starterpack.jar run zombieComputer
```

### Serve your bot to a port

You shouldn't need to do this, unless none of the other methods work.
<details>
<summary>Expand instructions</summary>

To serve your bot to a port, you can run it like this:

```sh
./gradlew build
java -jar build/libs/starterpack.jar serve [port]
```

Where port is the port you want to serve to, like 9001 for example:

```sh
./gradlew build
java -jar build/libs/starterpack.jar serve 9001
```

A full setup with the engine might look like (all 3 commands in separate terminal windows):

```sh
java -jar build/libs/starterpack.jar serve 9001
java -jar build/libs/starterpack.jar serve 9001
java -jar engine.jar 9001 9002
```

</details>

## Uploading

Using the cli, you can upload your bot using:

```ssh
./gradlew build
mm29 upload build/libs/starterpack.jar
```

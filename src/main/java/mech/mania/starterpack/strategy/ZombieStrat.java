package mech.mania.starterpack.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mech.mania.starterpack.game.GameState;
import mech.mania.starterpack.game.character.Character;
import mech.mania.starterpack.game.character.MoveAction;
import mech.mania.starterpack.game.character.action.AbilityAction;
import mech.mania.starterpack.game.character.action.AttackAction;
import mech.mania.starterpack.game.character.action.CharacterClassType;
import mech.mania.starterpack.game.util.Position;


public class ZombieStrat extends Strategy{

  // ID of zombie: int

  final Map<String, List<Position>> breakTargets = new HashMap<>();

    @Override
    public Map<CharacterClassType, Integer> decideCharacterClasses(
            List<CharacterClassType> possibleClasses,
            int numToPick,
            int maxPerSameClass
    ) {

      return null;
    }

    @Override
    public List<MoveAction> decideMoves(
            Map<String, List<MoveAction>> possibleMoves,
            GameState gameState
    ) {

      final Map<Position, Integer> terrain = AStar.mapTerrain(gameState);
      // TODO implement targets here

      List<MoveAction> out = new ArrayList<>();

      for (String id : possibleMoves.keySet()){
        final Character zombie = gameState.characters().get(id);
        if (!zombie.isStunned()){
          List<Position> path = AStar.path(zombie.position(), new Position(0, 99), gameState, terrain);

          // for (Position p : path){ // printing path 
          //   System.out.print("(" + p.x() + ", " + p.y() + ") ");
          // }
          // System.out.println();

          final MoveAction move = new MoveAction(id, path.get(Math.min(path.size() - 1, 5)));

          boolean legal = false;
          for (MoveAction possible : possibleMoves.get(id)){
            if (possible.destination().equals(move.destination())){
              legal = true;
              break;
            }
          }

          if (!legal){
            // System.out.println("oopsies that wasnt legal"); // this works so we dont need to test
            // throw new IllegalArgumentException("Hey roshan you need to fix your pathfinding");

            breakTargets.putIfAbsent(id, new ArrayList<>());
            breakTargets.get(id).add(move.destination()); // we want to break where we want to go
          } else {
            out.add(move); 
          }
        }
      }

      return out;
    }

    @Override
    public List<AttackAction> decideAttacks(
            Map<String, List<AttackAction>> possibleAttacks,
            GameState gameState
    ) {
      final List<AttackAction> choices = new ArrayList<>();

      for (Map.Entry<String, List<AttackAction>> entry : possibleAttacks.entrySet()){
        final String id = entry.getKey();
        for (AttackAction attack : entry.getValue()){

        }
      }

      return choices;
    }

    @Override
    public List<AbilityAction> decideAbilities(
            Map<String, List<AbilityAction>> possibleAbilities,
            GameState gameState
    ) {
      return null;
    }
}

package mech.mania.starterpack.strategy;

import java.util.ArrayList;
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

  // TODO implement targets here

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
      AStar.mapTerrain(gameState);

      List<MoveAction> out = new ArrayList<>();

      for (Character zombie : gameState.characters().values()){
        if (zombie.isZombie() && !zombie.isStunned()){
          out.add(new MoveAction(zombie.id(), AStar.nextMove(zombie.position(), new Position(50, 50), 5, gameState))); 
        }
      }

      return out;
    }

    @Override
    public List<AttackAction> decideAttacks(
            Map<String, List<AttackAction>> possibleAttacks,
            GameState gameState
    ) {
        return new ArrayList<>();
    }

    @Override
    public List<AbilityAction> decideAbilities(
            Map<String, List<AbilityAction>> possibleAbilities,
            GameState gameState
    ) {
        return new ArrayList<>();
    }
}

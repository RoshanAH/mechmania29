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
import mech.mania.starterpack.game.character.action.AttackActionType;

import mech.mania.starterpack.game.character.action.CharacterClassType;
import mech.mania.starterpack.game.terrain.Terrain;
import mech.mania.starterpack.game.terrain.TerrainType;
import mech.mania.starterpack.game.util.Position;


public class ZombieStrat extends Strategy{

  // ID of zombie: int

  final static Map<String, List<String>> breakTargets = new HashMap<>();

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

      final Map<Position, Terrain> terrain = AStar.mapTerrain(gameState);
      final Map<String, Integer> targets = new HashMap<>(); // human id -> list of zombie ids

      final List<Character> humans = new ArrayList<>();

      for (Character character : gameState.characters().values()){
        if (!character.isZombie()){
          humans.add(character);
          targets.put(character.id(), 0);
        }
      }

      List<MoveAction> out = new ArrayList<>();

      for (Map.Entry<String, List<MoveAction>> entry : possibleMoves.entrySet()){
        final String id = entry.getKey();
        final List<MoveAction> possible = entry.getValue();
        final Character zombie = gameState.characters().get(id);

        Character target = humans.get(0);
        {
          int dist = AStar.dist(zombie.position(), target.position());
          int attackers = targets.get(target.id());
          for (Character human : humans){
            int newDist = AStar.dist(zombie.position(), human.position());
            int newAttackers = targets.get(human.id());

            if (newAttackers < attackers || (newAttackers == attackers && newDist < dist)){
              dist = newDist; 
              attackers = newAttackers;
              target = human;
            }
          }
        }

        targets.put(target.id(), targets.get(target.id()) + 1);

        if (possible.isEmpty()) continue;

        if (!zombie.isStunned()){
          List<Position> path = AStar.path(zombie.position(), target.position(), gameState, terrain, (it) -> {
            if (it.type() == TerrainType.RIVER)
              return Integer.MAX_VALUE;
            return it.health();
          });

          // for (Position p : path){ // printing path 
          //   System.out.print("(" + p.x() + ", " + p.y() + ") ");
          // }
          // System.out.println();


          int i = 0;
          for (; i <= Math.min(5, path.size() - 1); i++){
            boolean legal = false;
            Position next = path.get(i);

            for (MoveAction move : possible){
              if (move.destination().equals(next)){
                legal = true;
                break;
              }
            }

            if (!legal) break;
          }

          final Position nextPos = path.get(i - 1);
          final MoveAction targetMove = new MoveAction(id, nextPos);
          out.add(targetMove);

          if (i <= Math.min(5, path.size() - 1)){ // this means there is an obstruction
            breakTargets.putIfAbsent(id, new ArrayList<>());
            final Position breakPos = path.get(i);
            breakTargets.get(id).add("(" + breakPos.x() + ", " + breakPos.y() + ")"); // we want to break where we want to go (this is so scuffed)
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
        int attackedHealth = Integer.MAX_VALUE;
        boolean canEat = false;
        AttackAction attack = null;

        for (AttackAction possibleAttack : entry.getValue()){
          if (possibleAttack.type() == AttackActionType.CHARACTER) {

            String newId = possibleAttack.attackingId();
            int possibleAttackedHealth = gameState.characters().get(newId).health();

            if (possibleAttackedHealth < attackedHealth) {
              attackedHealth = possibleAttackedHealth;
              attack = possibleAttack;
            }

            canEat = true;
          }

          if (canEat) continue;

          final List<String> targets = breakTargets.get(id);
          if (targets == null) continue; // dont consider breaking if zombie hasnt requested anythign

          for (String terrainId : targets){
            // System.out.println(attack.attackingId() + " == " + terrainId);
            if (possibleAttack.attackingId().equals(terrainId)){
              // System.out.println("Im gonna break this !");
              attack = possibleAttack;
              targets.remove(terrainId);
              break;
            }
          }
        }

        if (attack != null)
          choices.add(attack);
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

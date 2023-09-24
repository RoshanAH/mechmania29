package mech.mania.starterpack.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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


public class HumanStrat extends Strategy {

  public static int panicRange = 15;
  public static Map<String, List<Position>> waypoints = new HashMap<>();
  final static Map<String, List<String>> breakTargets = new HashMap<>();
  public static boolean assignedJobs = false;

    @Override
    public Map<CharacterClassType, Integer> decideCharacterClasses(
            List<CharacterClassType> possibleClasses,
            int numToPick,
            int maxPerSameClass
    ) {
        // Selecting character classes following a specific distribution
        return Map.of(
                CharacterClassType.MEDIC, 3,
                CharacterClassType.TRACEUR, 5,
                CharacterClassType.DEMOLITIONIST, 3 ,
                CharacterClassType.MARKSMAN, 5 
        );
    }

    @Override
    public List<MoveAction> decideMoves(
            Map<String, List<MoveAction>> possibleMoves,
            GameState gameState
    ) {

      if (!assignedJobs) assignJobs(gameState);

      final Map<Position, Terrain> terrain = AStar.mapTerrain(gameState);
      List<MoveAction> out = new ArrayList<>();

      for (Map.Entry<String, List<MoveAction>> entry : possibleMoves.entrySet()){
        final String id = entry.getKey();
        final List<MoveAction> possible = entry.getValue();
        final Character human = gameState.characters().get(id);

        if (possible.isEmpty()) continue;

        boolean panic = false;

        Position closestZombiePos = null;
        int closestZombieDistance = Integer.MAX_VALUE;

        for (Character zombie : gameState.characters().values()){
          if (!zombie.isZombie()) continue;
          final int dist = AStar.dist(human.position(), zombie.position());
          if(closestZombiePos == null || dist < closestZombieDistance){
            closestZombiePos = zombie.position();
            closestZombieDistance = dist;
          }
          if (dist <= panicRange){
            panic = true;
          }
        }

        if (panic) {
          // Choose a move action that takes the character further from the closest zombie
          int moveDistance = -1;
          MoveAction moveChoice = possible.get(0);

          for (MoveAction m : possible) {
            final int distance = AStar.dist(m.destination(), closestZombiePos);

            if (distance > moveDistance) {
              moveDistance = distance;
              moveChoice = m;
            }
          }

          out.add(moveChoice);
          continue;
        }

        final List<Position> points = waypoints.get(id);
        if (points.isEmpty()) continue;
        List<Position> path = AStar.path(human.position(), points.get(0), gameState, terrain, getNav(human.classType()));


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

        if (human.position().equals(points.get(0))){
          points.remove(0);
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
        AttackAction attack = null;
        boolean stun = false;

        for (AttackAction possibleAttack : entry.getValue()){

          if (possibleAttack.type() == AttackActionType.CHARACTER){
            Position pos = gameState.characters().get(id).position();
            AttackAction closestZombie = null;
            int closestZombieDistance = Integer.MAX_VALUE;

            // Find the closest zombie to attack
            for (AttackAction a : entry.getValue()) {
              if (a.type() == AttackActionType.CHARACTER) {
                Position attackeePos = gameState.characters().get(a.attackingId()).position();

                int distance = Math.abs(attackeePos.x() - pos.x()) +
                  Math.abs(attackeePos.y() - pos.y());

                if (distance < closestZombieDistance) {
                  closestZombie = a;
                  closestZombieDistance = distance;
                }
              }
            }

            if (closestZombie != null) {
              choices.add(closestZombie);
              stun = true;
            }
          }

          if (stun) continue;

          final List<String> targets = breakTargets.get(id);
          if (targets == null) continue; // dont consider breaking if human hasnt requested anythign

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
        List<AbilityAction> choices = new ArrayList<>();

        for (Map.Entry<String, List<AbilityAction>> entry : possibleAbilities.entrySet()) {
            String characterId = entry.getKey();
            Character human = gameState.characters().get(characterId);
            
            if (human.classType() == CharacterClassType.MEDIC){
              List<AbilityAction> abilities = entry.getValue();

              // Handle the case where there is no ability to be made, such as when stunned
              if (!abilities.isEmpty()) {
                AbilityAction humanTarget = abilities.get(0);
                int leastHealth = Integer.MAX_VALUE;

                // Find the human target with the least health to heal
                for (AbilityAction a : abilities) {
                  int health = gameState.characters().get(a.characterIdTarget()).health();

                  if (health < leastHealth) {
                    humanTarget = a;
                    leastHealth = health;
                  }
                }

                choices.add(humanTarget);
              }
            } else if (human.classType() == CharacterClassType.BUILDER){
              if (waypoints.get(characterId).isEmpty()){

              }
            }
        }

        return choices;
    }

    private void assignJobs(GameState state){
      int tracuer = 0;
      int demo = 0;

      for (Character c : state.characters().values()){
        if (c.isZombie()) continue;
        final String id = c.id();
        final List<Position> waypointList = new ArrayList<>();
        waypoints.put(c.id(), waypointList); 

        switch (c.classType()){
          case TRACEUR:
            switch (tracuer){
              case 0:
                waypointList.add(new Position(71, 50));
                waypointList.add(new Position(88, 21));
                break;
              case 1: 
                waypointList.add(new Position(15, 44));
                break;
              case 2: 
                waypointList.add(new Position(3, 49));
                waypointList.add(new Position(0, 0));
                break;
              case 3: 
                waypointList.add(new Position(48, 99));
                break;
              case 4: 
                waypointList.add(new Position(44, 80));
                break;
            }
            tracuer++;
            break;
          case DEMOLITIONIST:
            switch (demo){
              case 0:
                  waypointList.add(new Position(99, 27));
                  waypointList.add(new Position(93, 0));
                  waypointList.add(new Position(99, 0));
                break;
              case 1:
                  waypointList.add(new Position(77, 99));
                  waypointList.add(new Position(99, 99));
                break;
              case 2:
                  waypointList.add(new Position(83, 59));
                  waypointList.add(new Position(83, 52));
                break;
            }
            demo++;
            break;
          case MEDIC:
            waypointList.add(new Position(45, 99));
            break;
          case NORMAL:
            waypointList.add(new Position(45, 99));
            break;
          case MARKSMAN:
            waypointList.add(new Position(45, 99));
            break;
        }
      }

      assignedJobs = true;
    }

    private Function<Terrain, Integer> getNav(CharacterClassType type){
      switch(type){
        case DEMOLITIONIST:
          return (it) -> 6;
        case TRACEUR:
          return (it) -> {
            if (it.type() == TerrainType.BARRICADE)
              return 0;
            else
              return Integer.MAX_VALUE;
          };
        default:
          return (it) -> Integer.MAX_VALUE;
      }
    }
}

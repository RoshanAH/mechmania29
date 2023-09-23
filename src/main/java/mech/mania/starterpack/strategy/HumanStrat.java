package mech.mania.starterpack.strategy;

import mech.mania.starterpack.game.GameState;
import mech.mania.starterpack.game.character.Character;
import mech.mania.starterpack.game.character.MoveAction;
import mech.mania.starterpack.game.character.action.AbilityAction;
import mech.mania.starterpack.game.character.action.AttackAction;
import mech.mania.starterpack.game.character.action.AttackActionType;
import mech.mania.starterpack.game.character.action.CharacterClassType;
import mech.mania.starterpack.game.util.Position;

import java.util.*;


public class HumanStrat extends Strategy {

    @Override
    public Map<CharacterClassType, Integer> decideCharacterClasses(
            List<CharacterClassType> possibleClasses,
            int numToPick,
            int maxPerSameClass
    ) {
        // Selecting character classes following a specific distribution
        return Map.of(
                CharacterClassType.MARKSMAN, 5,
                CharacterClassType.MEDIC, 4,
                CharacterClassType.TRACEUR, 4,
                CharacterClassType.DEMOLITIONIST, 2
        );
    }

    @Override
    public List<MoveAction> decideMoves(
            Map<String, List<MoveAction>> possibleMoves,
            GameState gameState
    ) {
        List<MoveAction> choices = new ArrayList<>();

        for (Map.Entry<String, List<MoveAction>> entry : possibleMoves.entrySet()) {
            String characterId = entry.getKey();
            List<MoveAction> moves = entry.getValue();

            // Handle the case where there is no move to be made, such as when stunned
            if (!moves.isEmpty()) {
                Position pos = gameState.characters().get(characterId).position();
                Position closestZombiePos = pos;
                int closestZombieDistance = Integer.MAX_VALUE;

                // Find the closest zombie
                for (Character c : gameState.characters().values()) {
                    if (!c.isZombie()) {
                        continue;  // Ignore fellow humans
                    }

                    int distance = Math.abs(c.position().x() - pos.x()) +
                            Math.abs(c.position().y() - pos.y());

                    if (distance < closestZombieDistance) {
                        closestZombiePos = c.position();
                        closestZombieDistance = distance;
                    }
                }

                int moveDistance = -1;
                MoveAction moveChoice = moves.get(0);

                // Choose a move action that takes the character further from the closest zombie
                for (MoveAction m : moves) {
                    int distance = Math.abs(m.destination().x() - closestZombiePos.x()) +
                            Math.abs(m.destination().y() - closestZombiePos.y());

                    if (distance > moveDistance) {
                        moveDistance = distance;
                        moveChoice = m;
                    }
                }

                choices.add(moveChoice);
            }
        }

        return choices;
    }

    @Override
    public List<AttackAction> decideAttacks(
            Map<String, List<AttackAction>> possibleAttacks,
            GameState gameState
    ) {
        List<AttackAction> choices = new ArrayList<>();

        for (Map.Entry<String, List<AttackAction>> entry : possibleAttacks.entrySet()) {
            String characterId = entry.getKey();
            List<AttackAction> attacks = entry.getValue();

            // Handle the case where there is no attack to be made, such as when stunned
            if (!attacks.isEmpty()) {
                Position pos = gameState.characters().get(characterId).position();
                AttackAction closestZombie = null;
                int closestZombieDistance = Integer.MAX_VALUE;

                // Find the closest zombie to attack
                for (AttackAction a : attacks) {
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
                }
            }
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
        }

        return choices;
    }
}

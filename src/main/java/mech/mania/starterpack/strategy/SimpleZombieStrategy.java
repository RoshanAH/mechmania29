package mech.mania.starterpack.strategy;

import mech.mania.starterpack.game.GameState;
import mech.mania.starterpack.game.character.Character;
import mech.mania.starterpack.game.character.MoveAction;
import mech.mania.starterpack.game.character.action.AbilityAction;
import mech.mania.starterpack.game.character.action.AttackAction;
import mech.mania.starterpack.game.character.action.AttackActionType;
import mech.mania.starterpack.game.character.action.CharacterClassType;
import mech.mania.starterpack.game.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SimpleHumanStrategy implementation for bot decision-making.
 */
public class SimpleZombieStrategy extends Strategy {
    @Override
    public Map<CharacterClassType, Integer> decideCharacterClasses(List<CharacterClassType> possibleClasses, int numToPick, int maxPerSameClass) {
        return null;
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

            if (!moves.isEmpty()) {
                Position pos = gameState.characters().get(characterId).position();
                Position closestHumanPos = pos;
                int closestHumanDistance = Integer.MAX_VALUE;

                for (Character c : gameState.characters().values()) {
                    if (c.isZombie()) {
                        continue;
                    }

                    int distance = Math.abs(c.position().x() - pos.y())
                            + Math.abs(c.position().x() - pos.y());

                    if (distance < closestHumanDistance) {
                        closestHumanPos = c.position();
                        closestHumanDistance = distance;
                    }
                }

                int moveDistance = Integer.MAX_VALUE;
                MoveAction moveChoice = moves.get(0);

                for (MoveAction m : moves) {
                    int distance = Math.abs(m.destination().x() - closestHumanPos.x())
                            + Math.abs(m.destination().y() - closestHumanPos.y());

                    if (distance < moveDistance) {
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

            if (!attacks.isEmpty()) {
                Position pos = gameState.characters().get(characterId).position();
                AttackAction closestZombie = null;
                int closestZombieDistance = 404;

                for (AttackAction a : attacks) {
                    if (a.type() == AttackActionType.CHARACTER) {
                        Position attackeePos = gameState.characters().get(a.attackingId()).position();
                        int distance = Math.abs(attackeePos.x() - pos.x())
                                + Math.abs(attackeePos.y() - pos.y());

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
    public List<AbilityAction> decideAbilities(Map<String, List<AbilityAction>> possibleAbilities, GameState gameState) {
        return null;
    }
}

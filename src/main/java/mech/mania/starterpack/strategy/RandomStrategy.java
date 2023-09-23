package mech.mania.starterpack.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import mech.mania.starterpack.game.GameState;
import mech.mania.starterpack.game.character.MoveAction;
import mech.mania.starterpack.game.character.action.AbilityAction;
import mech.mania.starterpack.game.character.action.AttackAction;
import mech.mania.starterpack.game.character.action.CharacterClassType;

/**
 * Random strategy implementation for bot decision-making.
 */
public class RandomStrategy extends Strategy {
    private final Random random = new Random();

    @Override
    public Map<CharacterClassType, Integer> decideCharacterClasses(
            List<CharacterClassType> possibleClasses,
            int numToPick,
            int maxPerSameClass
    ) {
        Map<CharacterClassType, Integer> choices = new HashMap<>();
        int pickedSoFar = 0;

        while (pickedSoFar < numToPick) {
            CharacterClassType selected = possibleClasses.get(random.nextInt(possibleClasses.size()));

            choices.putIfAbsent(selected, 0);

            if (choices.get(selected) < maxPerSameClass) {
                choices.put(selected, choices.get(selected) + 1);
                pickedSoFar++;
            }
        }

        return choices;
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

            // NOTE: You will have to handle the case where there is no move to be made, such as when stunned
            if (!moves.isEmpty()) {
                choices.add(moves.get(random.nextInt(moves.size())));
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
            
            // NOTE: You will have to handle the case where there is no attack to be made, such as when stunned
            if (!attacks.isEmpty()) {
                choices.add(attacks.get(random.nextInt(attacks.size())));
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

            // NOTE: You will have to handle the case where there is no ability to be made, such as when stunned
            if (!abilities.isEmpty()) {
                choices.add(abilities.get(random.nextInt(abilities.size())));
            }
        }

        return choices;
    }
}

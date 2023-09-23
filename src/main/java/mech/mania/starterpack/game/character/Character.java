package mech.mania.starterpack.game.character;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import mech.mania.starterpack.game.character.action.CharacterClassType;
import mech.mania.starterpack.game.util.Position;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Character(
        String id,
        Position position,
        boolean isZombie,
        CharacterClassType classType,
        int health,
        boolean isStunned
) {}

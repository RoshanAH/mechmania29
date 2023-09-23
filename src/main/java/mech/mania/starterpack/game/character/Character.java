package mech.mania.starterpack.game.character;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import mech.mania.starterpack.game.character.action.CharacterClassType;
import mech.mania.starterpack.game.util.Position;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Character(
        String id,
        Position position,
        @JsonProperty("zombie")
        boolean isZombie,
        @JsonProperty("class")
        CharacterClassType classType,
        int health,
        @JsonProperty("stunned")
        boolean isStunned
) {}

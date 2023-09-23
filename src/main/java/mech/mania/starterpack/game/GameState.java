package mech.mania.starterpack.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import mech.mania.starterpack.game.terrain.Terrain;
import mech.mania.starterpack.game.character.Character;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GameState(
        int turn,
        @JsonProperty("characterStates")
        Map<String, Character> characters,
        @JsonProperty("terrainStates")
        Map<String, Terrain> terrains
) {}

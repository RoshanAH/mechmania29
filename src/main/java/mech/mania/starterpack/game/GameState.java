package mech.mania.starterpack.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import mech.mania.starterpack.game.terrain.Terrain;
import mech.mania.starterpack.game.character.Character;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GameState(
        int turn,
        Map<String, Character> characters,
        Map<String, Terrain> terrains
) {}

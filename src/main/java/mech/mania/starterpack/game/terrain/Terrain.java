package mech.mania.starterpack.game.terrain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import mech.mania.starterpack.game.util.Position;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Terrain(String id, Position position, int health, boolean canAttackThrough) {}
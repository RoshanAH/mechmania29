package mech.mania.starterpack.game.terrain;

public enum TerrainType {
    WALL("WALL"),
    BARRICADE("BARRICADE"),
    TREE("TREE"),
    RIVER("RIVER");

    private final String value;

    TerrainType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}

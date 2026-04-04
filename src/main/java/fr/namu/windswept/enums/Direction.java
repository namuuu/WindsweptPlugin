package fr.namu.windswept.enums;

public enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

     public Direction getOpposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case EAST -> WEST;
            case SOUTH -> NORTH;
            case WEST -> EAST;
        };
    }

    public int getXOffset() {
        return switch (this) {
            case NORTH, SOUTH -> 0;
            case EAST -> 1;
            case WEST -> -1;
        };
    }

    public int getZOffset() {
        return switch (this) {
            case EAST, WEST -> 0;
            case NORTH -> -1;
            case SOUTH -> 1;
        };
    }
}

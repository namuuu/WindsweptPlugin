package fr.namu.windswept.enums;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public enum DiceValue {
    ONE(0f, 180f),
    TWO(0f, 0f), // ?
    THREE(0f, 90f),
    FOUR(0f, 90f),
    FIVE(0f, 0f), // good
    SIX(0f, 180f);

    private final float yaw;
    private final float pitch;

    DiceValue(float v, float v1) {
        this.yaw = v;
        this.pitch = v1;
    }

    public static Vector3f applyPositionFix(int value, float size) {
        float halfSize = size / 2f;

        return switch (value) {
            case 1 -> new Vector3f(halfSize, -halfSize, -halfSize);
            case 2 -> new Vector3f(-halfSize, halfSize, halfSize);
            case 3, 6 -> new Vector3f(-halfSize, halfSize, -halfSize);
            case 4 -> new Vector3f(-halfSize, -halfSize, halfSize);
            case 5 -> new Vector3f(-halfSize, -halfSize, -halfSize);
            default -> new Vector3f(0, 0, 0);
        };
    }

    public static Quaternionf getRotation(int value) {

        return switch (value) {

            case 1 -> new Quaternionf().rotateZ((float) Math.toRadians(90));

            case 2 -> new Quaternionf().rotateX((float) Math.toRadians(180));

            case 3 -> new Quaternionf().rotateX((float) Math.toRadians(90));

            case 4 -> new Quaternionf().rotateX((float) Math.toRadians(-90));

            case 5 -> new Quaternionf();

            case 6 -> new Quaternionf().rotateZ((float) Math.toRadians(-90));

            default -> throw new IllegalStateException();
        };
    }
}

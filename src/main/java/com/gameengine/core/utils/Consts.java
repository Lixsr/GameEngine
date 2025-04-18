package com.gameengine.core.utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Consts {
    public static final String TITLE = "GameEngine";

    public static final float FOV = (float) Math.toRadians(60);
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000f;

    public static final int MAX_SPOT_LIGHTS = 5;
    public static final int MAX_POINT_LIGHTS = 5;
    public static final float SIZE = 800.0f;
    public static final float ROTATION_SPEED = 90.0f;
    public static final float MOVEMENT_SPEED = 2f;




    public static final float SPECULAR_POWER = 10f;
    public static final Vector3f AMBIENT_LIGHT = new Vector3f(0.7f, 0.7f, 0.7f);
    public static final Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    public static final float MOUSE_SENSITIVITY = 0.2f;
    public static final float CAMERA_MOVE_SPEED = 0.05f;
    public static final Vector3f[] faceNormals = {
            new Vector3f(1, 0, 0),  // Right (+x)
            new Vector3f(-1, 0, 0), // Left (-x)
            new Vector3f(0, 1, 0),  // Top (+y)
            new Vector3f(0, -1, 0), // Bottom (-y)
            new Vector3f(0, 0, 1),  // Front (+z)
            new Vector3f(0, 0, -1)  // Back (-z)
    };
    public static final String[] faceNames = {"right", "left", "top", "bottom", "front", "back"};
}

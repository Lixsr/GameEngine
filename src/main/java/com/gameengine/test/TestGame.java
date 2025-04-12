package com.gameengine.test;

import com.gameengine.core.*;
import com.gameengine.core.entity.Entity;
import com.gameengine.core.entity.Model;
import com.gameengine.core.entity.Texture;
import com.gameengine.core.lighting.DirectionalLight;
import com.gameengine.core.utils.Consts;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class TestGame implements ILogic {
    private static final float CAMERA_MOVE_SPEED = 0.05f;

    private final RenderManager renderer;
    private final WindowManager window;
    private final ObjectLoader loader;

    private Entity entity;
    private Camera camera;

    Vector3f cameraInc;

    private float lightAngle;
    private DirectionalLight directionalLight;


    public TestGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f();
        lightAngle = -90;
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        Model model = loader.loadOBJModel("/models/bunny.obj");
        model.setTexture(new Texture(loader.loadTexture("textures/grassblock.png")), 1f);
        entity = new Entity(model, new Vector3f(0, 0, -5), new Vector3f(0, 0, 0), 1);
        float lightIntensity = 0.0f;
        Vector3f lightPosition = new Vector3f(-1, -10, 0);
        Vector3f lightColour = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColour, lightPosition, lightIntensity);
    };

    @Override
    public void input() {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            cameraInc.z = -1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            cameraInc.x = -1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_Q)) {
            cameraInc.y = -1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_E)) {
            cameraInc.y = 1;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        camera.movePosition(cameraInc.x * Consts.CAMERA_STEP, cameraInc.y * Consts.CAMERA_STEP, cameraInc.z * Consts.CAMERA_STEP);
        entity.incRotation(0.0f, 0.5f, 0.0f);

        if(mouseInput.isRightButtonPress()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY, 0);
        }
//        entity.incRotation(0, 0.25f, 0);
        lightAngle += 0.5f;
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 360)
                lightAngle = -90;
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        }else {
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }
        double angleRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angleRad);
        directionalLight.getDirection().y = (float) Math.cos(angleRad);
    }

    @Override
    public void render() {
        renderer.render(entity, camera, directionalLight);

    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}

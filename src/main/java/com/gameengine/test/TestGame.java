package com.gameengine.test;

import com.gameengine.core.*;
import com.gameengine.core.entity.Entity;
import com.gameengine.core.entity.Model;
import com.gameengine.core.entity.Texture;
import com.gameengine.core.lighting.DirectionalLight;
import com.gameengine.core.lighting.PointLight;
import com.gameengine.core.lighting.SpotLight;
import com.gameengine.core.rendering.RenderManager;
import com.gameengine.core.utils.Consts;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import java.util.Random;


public class TestGame implements ILogic {
    private static final float CAMERA_MOVE_SPEED = 0.05f;

    private final RenderManager renderer;
    private final WindowManager window;
    private final ObjectLoader loader;

    private List<Entity> entities;
    private Camera camera;

    Vector3f cameraInc;

    private float lightAngle, spotAngle= 0, spotInc = 1;
    private DirectionalLight directionalLight;
    private PointLight[] pointLights;
    private SpotLight[] spotLights;


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

        // better cube rendering
        float[] vertices = new float[] {
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
        };
        float[] textureCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.0f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };
        int[] indices = new int[]{
                0, 1, 3, 3, 1, 2,
                8, 10, 11, 9, 8, 11,
                12, 13, 7, 5, 12, 7,
                14, 15, 6, 4, 14, 6,
                16, 18, 19, 17, 16, 19,
                4, 6, 7, 5, 4, 7,
        };
        Model model = loader.loadModel(vertices, textureCoords, indices);

//        Model model = loader.loadOBJModel("/models/cube.obj");
        model.setTexture(new Texture(loader.loadTexture("textures/grassblock.png")), 1f);
        entities = new ArrayList<Entity>();
        Random rnd = new Random();
        for (int i = 0; i < 200; i++) {
            float x = rnd.nextFloat() * 100 - 50;
            float y = rnd.nextFloat() * 100 - 50;
            float z = rnd.nextFloat() * -300;
            entities.add(new Entity(model, new Vector3f(x, y, z),
                    new Vector3f(rnd.nextFloat() * 180, rnd.nextFloat() * 180, 0), 1));
        }
        entities.add(new Entity(model, new Vector3f(0, 0, -2f),
                new Vector3f(0, 0, 0), 1));

        float lightIntensity = 1.0f;
        // point light
        Vector3f lightPosition = new Vector3f(-0.5f, -0.5f, -3.2f);
        Vector3f lightColour = new Vector3f(1, 1, 1);
        PointLight pointLight = new PointLight(lightColour, lightPosition, lightIntensity, 0, 0, 1);

        // spot light
        Vector3f coneDir = new Vector3f(0, 0, -1);
        float cutoff = (float) (Math.cos(Math.toRadians(140)));
        SpotLight spotLight = new SpotLight(new PointLight(lightColour, new Vector3f(0, 0, -3.6f), lightIntensity, 0, 0, 0.2f), coneDir, cutoff);

        SpotLight spotLight1 = new SpotLight(pointLight, coneDir, cutoff);
        spotLight1.getPointLight().setPosition(new Vector3f(0.5f, 0.5f, -3.6f));

        // directional light
        lightPosition = new Vector3f(-1, -10, 0);
        lightColour = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColour, lightPosition, lightIntensity);

        pointLights = new PointLight[]{pointLight};
        spotLights = new SpotLight[]{spotLight, spotLight1};

    }

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
//        if (window.isKeyPressed(GLFW.GLFW_KEY_O)) {
//            pointLight.getPosition().x += 0.1f;
//        }
//        if (window.isKeyPressed(GLFW.GLFW_KEY_P)) {
//            pointLight.getPosition().x -= 0.1f;
//        }
        float lightPos = spotLights[0].getPointLight().getPosition().z;
        if (window.isKeyPressed(GLFW.GLFW_KEY_N)) {
            spotLights[0].getPointLight().getPosition().z = lightPos +  0.1f;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_M)) {
            spotLights[0].getPointLight().getPosition().z = lightPos -  0.1f;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        camera.movePosition(cameraInc.x * Consts.CAMERA_STEP, cameraInc.y * Consts.CAMERA_STEP, cameraInc.z * Consts.CAMERA_STEP);

        if(mouseInput.isRightButtonPress()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY, 0);
        }

        spotAngle += spotInc * 0.05f;
        if (spotAngle > 4) {
            spotInc = -1;
        } else if (spotAngle <= -4) {
            spotInc = 1;
        }

        double spotAngleRed = Math.toRadians(spotAngle);
        Vector3f coneDir = spotLights[0].getPointLight().getPosition();
        coneDir.y = (float) Math.sin(spotAngleRed);
        

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

        for (Entity entity : entities) {
            renderer.processEntity(entity);
        }
    }

    @Override
    public void render() {
        renderer.render(camera, directionalLight, pointLights, spotLights);

    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}

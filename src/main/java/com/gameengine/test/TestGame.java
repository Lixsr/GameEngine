package com.gameengine.test;
import com.gameengine.core.*;
import com.gameengine.core.builder.BlockBuilder;
import com.gameengine.core.entity.*;
import com.gameengine.core.entity.terrain.BlendMapTerrain;
import com.gameengine.core.entity.terrain.TerrainTexture;
import com.gameengine.core.lighting.DirectionalLight;
import com.gameengine.core.lighting.PointLight;
import com.gameengine.core.lighting.SpotLight;
import com.gameengine.core.rendering.RenderManager;
import com.gameengine.core.entity.terrain.Terrain;
import com.gameengine.core.utils.Consts;
import com.gameengine.core.utils.RaycastHit;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;


public class TestGame implements ILogic {
    private final RenderManager renderer;
    private final WindowManager window;
    private final ObjectLoader loader;
    private SceneManager sceneManager;
    private Camera camera;
    Vector3f cameraInc;
    private BlockBuilder blockBuilder;

    public TestGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera(new Vector3f(0, 2, 0), new Vector3f(0, 0, 0));
        cameraInc = new Vector3f();
        sceneManager = new SceneManager(-90);
        blockBuilder = new BlockBuilder(sceneManager, camera, "textures/grassblock.png");

    }
    @Override
    public void init() throws Exception {
        renderer.init();
        glfwSetCursorPos(window.getWindowHandle(), GLFW_CURSOR, GLFW_CENTER_CURSOR);
        glfwSetInputMode(window.getWindowHandle(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        // better cube rendering
        Model model = loader.loadOBJModel("/models/cube.obj");
        model.setTexture(new Texture(loader.loadTexture("textures/grassblock.png")), 1f);
        model.getMaterial().setDisableCulling(true);
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("textures/terrain.png"));
        TerrainTexture redTexture = new TerrainTexture(loader.loadTexture("textures/flowers.png"));
        TerrainTexture greenTexture = new TerrainTexture(loader.loadTexture("textures/stone.png"));
        TerrainTexture blueTexture = new TerrainTexture(loader.loadTexture("textures/dirt.png"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("textures/blendMap.png"));

        BlendMapTerrain blendMapTerrain = new BlendMapTerrain(backgroundTexture, redTexture, greenTexture, blueTexture);
        // Add terrains
        Terrain terrain = new Terrain(new Vector3f(-300.5f, -0.5f, -800.5f), loader, new Material(
                new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), 0.1f), blendMapTerrain, blendMap);
        sceneManager.addTerrain(terrain);

        blockBuilder.randomBlocks();

        float lightIntensity = 1.0f;
        // point light
        Vector3f lightPosition = new Vector3f(-0.5f, -0.5f, -3.2f);
        Vector3f lightColour = new Vector3f(1, 1, 1);
        PointLight pointLight = new PointLight(lightColour, lightPosition, lightIntensity);
        PointLight.Attenuation attenuation = new PointLight.Attenuation(0, 0, 1);
        pointLight.setAttenuation(attenuation);

        lightIntensity = 50000f;
        // spot light
        Vector3f coneDir = new Vector3f(0, -50, 0);
        float cutoff = (float) (Math.cos(Math.toRadians(0.4)));
        SpotLight spotLight = new SpotLight(new PointLight(new Vector3f(0.25f, 0, 0f),
                new Vector3f(1f, 50f, -5f), lightIntensity, new PointLight.Attenuation(0f, 0f, 0.02f)), coneDir, cutoff);

        // spot light
        coneDir = new Vector3f(0, -50, 0);
        cutoff = (float) (Math.cos(Math.toRadians(0.4)));
        SpotLight spotLight1 = new SpotLight(new PointLight(new Vector3f(0, 0.25f, 0f),
                new Vector3f(1f, 50f, -5f), lightIntensity,new PointLight.Attenuation(0f, 0f, 0.02f)), coneDir, cutoff);

        // directional light
        lightIntensity = 1f;
        lightPosition = new Vector3f(-1, 0, 0);
        lightColour = new Vector3f(1, 1, 1);
        sceneManager.setDirectionalLight(new DirectionalLight(lightColour, lightPosition, lightIntensity));

        sceneManager.setPointLights(new PointLight[]{pointLight});
        sceneManager.setSpotLights(new SpotLight[]{spotLight, spotLight1});

    }

    @Override
    public void input() throws Exception {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        }
        if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        }
        if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Q)) {
            cameraInc.y = -1;
        }
        if (window.isKeyPressed(GLFW_KEY_E)) {
            cameraInc.y = 1;
        }

    }

    private boolean wasLeftButtonPressed = false;
    private boolean wasRightButtonPressed = false;
    private boolean wasRPressed = false;
    @Override
    public void update(float interval, MouseInput mouseInput) throws Exception {
        camera.movePosition(
                cameraInc.x * Consts.CAMERA_MOVE_SPEED,
                cameraInc.y * Consts.CAMERA_MOVE_SPEED,
                cameraInc.z * Consts.CAMERA_MOVE_SPEED
        );
        Vector2f rotVec = mouseInput.getDisplVec();
        camera.moveRotation(
                rotVec.x * Consts.MOUSE_SENSITIVITY,
                rotVec.y * Consts.MOUSE_SENSITIVITY,
                0
        );
        boolean isRPressed = window.isKeyPressed(GLFW_KEY_R);
        if (isRPressed && !wasRPressed) {
            blockBuilder.removeBlockWithoutAnimation();
        }
        wasRPressed = isRPressed;
        // Mouse click handling
        boolean isLeftButtonPressed = mouseInput.isLeftButtonPress();
        if (isLeftButtonPressed && !wasLeftButtonPressed) {
            blockBuilder.buildBlock();
        }
        wasLeftButtonPressed = isLeftButtonPressed; // Update state for next frame

        // Mouse click handling
        boolean isRightButtonPressed = mouseInput.isRightButtonPress();
        if (isRightButtonPressed && !wasRightButtonPressed) {
            blockBuilder.removeBlock();
        }
        wasRightButtonPressed = isRightButtonPressed;

        sceneManager.incSpotAngle(0.75f);
        if (sceneManager.getSpotAngle() > 9600) {
            sceneManager.setSpotInc(-1);
        } else if (sceneManager.getSpotAngle() <= -9600) {
            sceneManager.setSpotInc(1);
        }

        // Move the spot light over time
        double spotAngleRed = Math.toRadians(sceneManager.getSpotAngle());
        Vector3f coneDir = sceneManager.getSpotLights()[0].getPointLight().getPosition();
        coneDir.x = (float) Math.sin(spotAngleRed)/2;
        coneDir = sceneManager.getSpotLights()[1].getPointLight().getPosition();
        coneDir.x = (float) Math.cos(spotAngleRed)/2;

        sceneManager.incLightAngle(1.1f);
        if (sceneManager.getLightAngle() > 90) {
            sceneManager.getDirectionalLight().setIntensity(0);
            if (sceneManager.getLightAngle() >= 360)
                sceneManager.setLightAngle(-90);
        } else if (sceneManager.getLightAngle() <= -80 || sceneManager.getLightAngle() >= 80) {
            float factor = 1 - (Math.abs(sceneManager.getLightAngle()) - 80) / 10.0f;
            sceneManager.getDirectionalLight().setIntensity(factor);
            sceneManager.getDirectionalLight().getColor().y = Math.max(factor, 0.9f);
            sceneManager.getDirectionalLight().getColor().z = Math.max(factor, 0.5f);
        } else {
            sceneManager.getDirectionalLight().setIntensity(1);
            sceneManager.getDirectionalLight().getColor().x = 1;
            sceneManager.getDirectionalLight().getColor().y = 1;
            sceneManager.getDirectionalLight().getColor().z = 1;
        }

        double angRad = Math.toRadians(sceneManager.getLightAngle());
        sceneManager.getDirectionalLight().getDirection().x = (float) Math.sin(angRad);
        sceneManager.getDirectionalLight().getDirection().y = (float) Math.cos(angRad);

        for (Entity entity : sceneManager.getEntities()) {
            renderer.processEntity(entity);
        }
        for (Terrain terrain : sceneManager.getTerrains()) {
            renderer.processTerrain(terrain);
        }
    }

    @Override
    public void render() {
        renderer.render(camera, sceneManager);

    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}

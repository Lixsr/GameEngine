package com.gameengine.test;

import com.gameengine.core.*;
import com.gameengine.core.entity.*;
import com.gameengine.core.entity.terrain.BlendMapTerrain;
import com.gameengine.core.entity.terrain.TerrainTexture;
import com.gameengine.core.lighting.DirectionalLight;
import com.gameengine.core.lighting.PointLight;
import com.gameengine.core.lighting.SpotLight;
import com.gameengine.core.rendering.RenderManager;
import com.gameengine.core.entity.terrain.Terrain;
import com.gameengine.core.utils.Consts;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import java.util.Random;


public class TestGame implements ILogic {
    private final RenderManager renderer;
    private final WindowManager window;
    private final ObjectLoader loader;
    private SceneManager sceneManager;


    private Camera camera;

    Vector3f cameraInc;


    public TestGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f();
        sceneManager = new SceneManager(-90);

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

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("textures/terrain.png"));
        TerrainTexture redTexture = new TerrainTexture(loader.loadTexture("textures/flowers.png"));
        TerrainTexture greenTexture = new TerrainTexture(loader.loadTexture("textures/stone.png"));
        TerrainTexture blueTexture = new TerrainTexture(loader.loadTexture("textures/dirt.png"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("textures/blendMap.png"));

        BlendMapTerrain blendMapTerrain = new BlendMapTerrain(backgroundTexture, redTexture, greenTexture, blueTexture);

        // Add terrains
        Terrain terrain = new Terrain(new Vector3f(0, 1, -800), loader, new Material(
                new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), 0.1f), blendMapTerrain, blendMap);
        Terrain terrain2 = new Terrain(new Vector3f(-800, 1, -800), loader, new Material(
                new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), 0.1f), blendMapTerrain, blendMap);

        sceneManager.addTerrain(terrain);
        sceneManager.addTerrain(terrain2);

        Random rnd = new Random();
        for (int i = 0; i < 2000; i++) {
            float x = rnd.nextFloat() * 800;
            float z = rnd.nextFloat() * -800;
            sceneManager.addEntity(new Entity(model, new Vector3f(x, 2, z),
                    new Vector3f(0, 0, 0), 1));
        }

        sceneManager.addEntity(new Entity(model, new Vector3f(0, 2, -5),
                new Vector3f(0, 0, 0), 1));

        // Add entities
        // Terrain
        // Custom Terrain
//        Model model2 = loader.loadOBJModel("/models/complex_terrain.obj");
//        model2.setTexture(new Texture(loader.loadTexture("textures/grassblock.png")), 1f);
//        rnd = new Random();
//        for (int i = 0; i < 1; i++) {
//            float x = rnd.nextFloat() * 100 - 50;
//            float y = rnd.nextFloat() * 100 - 50;
//            float z = rnd.nextFloat() * -300;
//            entities.add(new Entity(model2, new Vector3f(0, -1, -800),
//                    new Vector3f(rnd.nextFloat() * 180, rnd.nextFloat() * 180, 0), 1));
//        }
//        entities.add(new Entity(model2, new Vector3f(0, 0, -2f),
//                new Vector3f(0, 0, 0), 1));

        float lightIntensity = 1.0f;
        // point light
        Vector3f lightPosition = new Vector3f(-0.5f, -0.5f, -3.2f);
        Vector3f lightColour = new Vector3f(1, 1, 1);
        PointLight pointLight = new PointLight(lightColour, lightPosition, lightIntensity, 0, 0, 1);

        // spot light
        Vector3f coneDir = new Vector3f(0, -50, 0);
        float cutoff = (float) (Math.cos(Math.toRadians(0.4)));
        lightIntensity = 50000f;
        SpotLight spotLight = new SpotLight(new PointLight(new Vector3f(0.25f, 0, 0f),
                new Vector3f(1f, 50f, -5f), lightIntensity,
                0f, 0f, 0.02f), coneDir, cutoff);

        // spot light
        coneDir = new Vector3f(0, -50, 0);
        cutoff = (float) (Math.cos(Math.toRadians(0.4)));
        lightIntensity = 50000f;
        SpotLight spotLight1 = new SpotLight(new PointLight(new Vector3f(0, 0.25f, 0f),
                new Vector3f(1f, 50f, -5f), lightIntensity,
                0f, 0f, 0.02f), coneDir, cutoff);

//        SpotLight spotLight1 = new SpotLight(pointLight, coneDir, cutoff);
//        spotLight1.getPointLight().setPosition(new Vector3f(0.5f, 0.5f, -3.6f));

        // directional light
        lightIntensity = 1f;
        lightPosition = new Vector3f(-1, 0, 0);
        lightColour = new Vector3f(1, 1, 1);
        sceneManager.setDirectionalLight(new DirectionalLight(lightColour, lightPosition, lightIntensity));

        sceneManager.setPointLights(new PointLight[]{pointLight});
        sceneManager.setSpotLights(new SpotLight[]{spotLight, spotLight1});

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
//        float lightPos = sceneManager.getSpotLights()[0].getPointLight().getPosition().z;
//        if (window.isKeyPressed(GLFW.GLFW_KEY_N)) {
//            sceneManager.getSpotLights()[0].getPointLight().getPosition().z = lightPos +  0.1f;
//        }
//        if (window.isKeyPressed(GLFW.GLFW_KEY_M)) {
//            sceneManager.getSpotLights()[0].getPointLight().getPosition().z = lightPos -  0.1f;
//        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        camera.movePosition(cameraInc.x * Consts.CAMERA_MOVE_SPEED, cameraInc.y * Consts.CAMERA_MOVE_SPEED, cameraInc.z * Consts.CAMERA_MOVE_SPEED);

        if(mouseInput.isRightButtonPress()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY, 0);
        }




        sceneManager.incSpotAngle(0.75f);
        if (sceneManager.getSpotAngle() > 9600) {
            sceneManager.setSpotInc(-1);
        } else if (sceneManager.getSpotAngle() <= -9600) {
            sceneManager.setSpotInc(1);
//            sceneManager.setSpotInc(1);
        }

        // Move the spot light over time
        double spotAngleRed = Math.toRadians(sceneManager.getSpotAngle());
//        Vector3f coneDir = sceneManager.getSpotLights()[0].getConeDirection();

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

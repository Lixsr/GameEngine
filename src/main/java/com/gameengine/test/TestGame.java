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
        camera = new Camera(new Vector3f(0, 2, 0), new Vector3f(0, 0, 0));
        cameraInc = new Vector3f();
        sceneManager = new SceneManager(-90);
    }
    @Override
    public void init() throws Exception {
        renderer.init();
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
        Terrain terrain = new Terrain(new Vector3f(0, 0, -800), loader, new Material(
                new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), 0.1f), blendMapTerrain, blendMap);
        Terrain terrain2 = new Terrain(new Vector3f(-800, 0, -800), loader, new Material(
                new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), 0.1f), blendMapTerrain, blendMap);

        sceneManager.addTerrain(terrain);
        sceneManager.addTerrain(terrain2);

        // Add entities
        Random rnd = new Random();
        for (int i = 0; i < 2000; i++) {
            int x = (int) Math.floor(rnd.nextFloat() * 800);
            int z = (int) Math.floor(rnd.nextFloat() * -800);
            sceneManager.addEntity(new Entity(model, new Vector3f(x, 0.5f, z),
                    new Vector3f(0, 0, 0), 1));
        }
        sceneManager.addEntity(new Entity(model, new Vector3f(0, 0.5f, -5),
                new Vector3f(0, 0, 0), 1));

        float lightIntensity = 1.0f;
        // point light
        Vector3f lightPosition = new Vector3f(-0.5f, -0.5f, -3.2f);
        Vector3f lightColour = new Vector3f(1, 1, 1);
        PointLight pointLight = new PointLight(lightColour, lightPosition, lightIntensity);
        PointLight.Attenuation attenuation = new PointLight.Attenuation(0, 0, 1);
        pointLight.setAttenuation(attenuation);

        // spot light
        Vector3f coneDir = new Vector3f(0, -50, 0);
        float cutoff = (float) (Math.cos(Math.toRadians(0.4)));
        lightIntensity = 50000f;
        SpotLight spotLight = new SpotLight(new PointLight(new Vector3f(0.25f, 0, 0f),
                new Vector3f(1f, 50f, -5f), lightIntensity, new PointLight.Attenuation(0f, 0f, 0.02f)), coneDir, cutoff);

        // spot light
        coneDir = new Vector3f(0, -50, 0);
        cutoff = (float) (Math.cos(Math.toRadians(0.4)));
        lightIntensity = 50000f;
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
    private void createBlockOnSide() throws Exception {
        Vector3f camPosition = camera.getPosition();
        Vector3f camRotation = camera.getRotation();

        // Direction based on camera rotation
        Vector3f camDirection = new Vector3f(
                (float) -Math.sin(Math.toRadians(camRotation.y)),
                (float) Math.sin(Math.toRadians(camRotation.x)),
                (float) -Math.cos(Math.toRadians(camRotation.y))
        );

        // Ray cast distance and step
        float rayLength = 5.0f;
        float stepSize = 0.1f;
        Vector3f checkPosition = new Vector3f(camPosition);

        Entity hitEntity = null;

        for (float i = 0; i < rayLength; i += stepSize) {
            checkPosition.add(new Vector3f(camDirection).mul(stepSize));
            float detectionRadius = 0.5f; // Adjust based on block size

            for (Entity entity : sceneManager.getEntities()) {
                Vector3f entityPos = entity.getPos();
                if (entityPos.distance(entityPos) <= detectionRadius) {
                    hitEntity = entity;
                }
            }
            if (hitEntity != null) break;
        }

        if (hitEntity != null) {
            Vector3f hitPos = new Vector3f(hitEntity.getPos());
            Vector3f newBlockPos = new Vector3f(hitPos).add(camDirection.normalize());

            Model blockModel = loader.loadOBJModel("/models/cube.obj");
            blockModel.setTexture(new Texture(loader.loadTexture("textures/grassblock.png")), 1f);
            blockModel.getMaterial().setDisableCulling(true);

            Entity newBlock = new Entity(blockModel, newBlockPos, new Vector3f(0, 0, 0), 1);
            sceneManager.addEntity(newBlock);
        }
    }
    public Vector3f raycastBlockHitPosition(Vector3f origin, Vector3f direction, float rayLength, float stepSize) {
        Vector3f currentPos = new Vector3f(origin);
        Vector3f step = new Vector3f(direction).normalize().mul(stepSize);
        for (float i = 0; i < rayLength; i += stepSize) {
            currentPos.add(step);
            Entity entity = getEntityAtPosition(currentPos);
            if (entity != null) {
                System.out.println(entity.getPos());
                return new Vector3f(entity.getPos());
            }
        }

        return null; // No hit
    }
    public Entity getEntityAtPosition(Vector3f position) {
        float detectionRadius = 3f;

        for (Entity entity : sceneManager.getEntities()) {
            Vector3f entityPos = entity.getPos();

            // If your entities are snapped to a grid, you can round the float
            if (Math.abs(entityPos.x - position.x) <= detectionRadius &&
                    Math.abs(entityPos.y - position.y) <= detectionRadius &&
                    Math.abs(entityPos.z - position.z) <= detectionRadius) {
                return entity;
            }
        }
        return null;
    }


    private boolean wasLeftButtonPressed = false;
    @Override
    public void update(float interval, MouseInput mouseInput) throws Exception {
        camera.movePosition(cameraInc.x * Consts.CAMERA_MOVE_SPEED, cameraInc.y * Consts.CAMERA_MOVE_SPEED, cameraInc.z * Consts.CAMERA_MOVE_SPEED);

        Vector2f rotVec = mouseInput.getDisplVec();
        camera.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY, 0);

        // Mouse click handling
        boolean isLeftButtonPressed = mouseInput.isLeftButtonPress();
        if (isLeftButtonPressed && !wasLeftButtonPressed) { // Detect transition to pressed
            Vector3f origin = camera.getPosition();
            Vector3f rotation = camera.getRotation();
            Vector3f direction = new Vector3f(
                    (float) -Math.sin(Math.toRadians(rotation.y)),
                    (float) Math.sin(Math.toRadians(rotation.x)),
                    (float) -Math.cos(Math.toRadians(rotation.y))
            );
            System.out.println("Block touched at " + raycastBlockHitPosition(origin, direction, 5.0f, 0.1f));
        }
        wasLeftButtonPressed = isLeftButtonPressed; // Update state for next frame
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

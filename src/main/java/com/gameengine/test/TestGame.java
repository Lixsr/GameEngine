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
    public Vector3f raycastBlockHitPosition(Vector3f origin, Vector3f rayDirection, float rayLength, float stepSize) {
        Entity closestEntity = null;
        float closestTMin = Float.POSITIVE_INFINITY;
        String closestFace = null;

        for (Entity entity : sceneManager.getEntities()) {
            Vector3f entityPos = entity.getPos();
            float blockSize = 1.0f;
            Vector3f min = new Vector3f(entityPos).sub(blockSize / 2.0f, blockSize / 2.0f, blockSize / 2.0f);
            Vector3f max = new Vector3f(entityPos).add(blockSize / 2.0f, blockSize / 2.0f, blockSize / 2.0f);

            // Quick AABB check to skip non-intersecting blocks
            if (rayIntersectsAABB(origin, rayDirection, min, max, rayLength) < 0) {
                continue;
            }

            // Define the six faces of the AABB
            Vector3f[] faceNormals = {
                    new Vector3f(1, 0, 0),  // Right (+x)
                    new Vector3f(-1, 0, 0), // Left (-x)
                    new Vector3f(0, 1, 0),  // Top (+y)
                    new Vector3f(0, -1, 0), // Bottom (-y)
                    new Vector3f(0, 0, 1),  // Front (+z)
                    new Vector3f(0, 0, -1)  // Back (-z)
            };
            Vector3f[] facePoints = {
                    new Vector3f(max.x, entityPos.y, entityPos.z), // Right
                    new Vector3f(min.x, entityPos.y, entityPos.z), // Left
                    new Vector3f(entityPos.x, max.y, entityPos.z), // Top
                    new Vector3f(entityPos.x, min.y, entityPos.z), // Bottom
                    new Vector3f(entityPos.x, entityPos.y, max.z), // Front
                    new Vector3f(entityPos.x, entityPos.y, min.z)  // Back
            };
            String[] faceNames = {"right", "left", "top", "bottom", "front", "back"};

            // Check each face
            for (int i = 0; i < 6; i++) {
                Vector3f normal = faceNormals[i];
                Vector3f pointOnPlane = facePoints[i];
                String faceName = faceNames[i];

                float t = rayPlaneIntersection(origin, rayDirection, normal, pointOnPlane);
                if (t >= 0 && t <= rayLength && t < closestTMin) {
                    Vector3f hitPoint = new Vector3f(rayDirection).mul(t).add(origin);
                    if (isPointOnFace(hitPoint, min, max, i)) {
                        closestTMin = t;
                        closestEntity = entity;
                        closestFace = faceName;
                    }
                }
            }
        }

        if (closestEntity != null) {
            System.out.println("Hit entity at: " + closestEntity.getPos() + ", face: " + closestFace);
            return new Vector3f(closestEntity.getPos());
        }

        return null;
    }
    private float rayPlaneIntersection(Vector3f rayOrigin, Vector3f rayDirection, Vector3f planeNormal, Vector3f pointOnPlane) {
        Vector3f dir = new Vector3f(rayDirection).normalize();
        float denom = planeNormal.dot(dir);
        if (Math.abs(denom) < 1e-4) { // Increase tolerance
            return -1;
        }
        Vector3f vec = new Vector3f(pointOnPlane).sub(rayOrigin);
        float t = vec.dot(planeNormal) / denom;
        if (t >= 0) {
            return t;
        }
        return -1;
    }
    private boolean isPointOnFace(Vector3f hitPoint, Vector3f min, Vector3f max, int faceIndex) {
        float epsilon = 1e-4f; // Increase tolerance

        switch (faceIndex) {
            case 0: // Right (+x)
                return Math.abs(hitPoint.x - max.x) < epsilon &&
                        hitPoint.y >= min.y - epsilon && hitPoint.y <= max.y + epsilon &&
                        hitPoint.z >= min.z - epsilon && hitPoint.z <= max.z + epsilon;
            case 1: // Left (-x)
                return Math.abs(hitPoint.x - min.x) < epsilon &&
                        hitPoint.y >= min.y - epsilon && hitPoint.y <= max.y + epsilon &&
                        hitPoint.z >= min.z - epsilon && hitPoint.z <= max.z + epsilon;
            case 2: // Top (+y)
                return Math.abs(hitPoint.y - max.y) < epsilon &&
                        hitPoint.x >= min.x - epsilon && hitPoint.x <= max.x + epsilon &&
                        hitPoint.z >= min.z - epsilon && hitPoint.z <= max.z + epsilon;
            case 3: // Bottom (-y)
                return Math.abs(hitPoint.y - min.y) < epsilon &&
                        hitPoint.x >= min.x - epsilon && hitPoint.x <= max.x + epsilon &&
                        hitPoint.z >= min.z - epsilon && hitPoint.z <= max.z + epsilon;
            case 4: // Front (+z)
                return Math.abs(hitPoint.z - max.z) < epsilon &&
                        hitPoint.x >= min.x - epsilon && hitPoint.x <= max.x + epsilon &&
                        hitPoint.y >= min.y - epsilon && hitPoint.y <= max.y + epsilon;
            case 5: // Back (-z)
                return Math.abs(hitPoint.z - min.z) < epsilon &&
                        hitPoint.x >= min.x - epsilon && hitPoint.x <= max.x + epsilon &&
                        hitPoint.y >= min.y - epsilon && hitPoint.y <= max.y + epsilon;
            default:
                return false;
        }
    }

    // Helper method for ray-AABB intersection
    private float rayIntersectsAABB(Vector3f rayOrigin, Vector3f rayDirection, Vector3f min, Vector3f max, float rayLength) {
        Vector3f dir = new Vector3f(rayDirection).normalize();
        Vector3f invDir = new Vector3f(
                Math.abs(dir.x) > 1e-6 ? 1.0f / dir.x : (dir.x >= 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY),
                Math.abs(dir.y) > 1e-6 ? 1.0f / dir.y : (dir.y >= 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY),
                Math.abs(dir.z) > 1e-6 ? 1.0f / dir.z : (dir.z >= 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY)
        );

        float tMin = (min.x - rayOrigin.x) * invDir.x;
        float tMax = (max.x - rayOrigin.x) * invDir.x;
        if (tMin > tMax) {
            float temp = tMin;
            tMin = tMax;
            tMax = temp;
        }

        float tYMin = (min.y - rayOrigin.y) * invDir.y;
        float tYMax = (max.y - rayOrigin.y) * invDir.y;
        if (tYMin > tYMax) {
            float temp = tYMin;
            tYMin = tYMax;
            tYMax = temp;
        }

        if (tMin > tYMax || tYMin > tMax) {
            return -1;
        }

        tMin = Math.max(tMin, tYMin);
        tMax = Math.min(tMax, tYMax);

        float tZMin = (min.z - rayOrigin.z) * invDir.z;
        float tZMax = (max.z - rayOrigin.z) * invDir.z;
        if (tZMin > tZMax) {
            float temp = tZMin;
            tZMin = tZMax;
            tZMax = temp;
        }

        if (tMin > tZMax || tZMin > tMax) {
            return -1;
        }

        tMin = Math.max(tMin, tZMin);
        tMax = Math.min(tMax, tZMax);

        if (tMin >= 0 && tMin <= rayLength) {
            return tMin;
        }

        return -1;
    }

    private boolean wasLeftButtonPressed = false;
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

        // Mouse click handling
        boolean isLeftButtonPressed = mouseInput.isLeftButtonPress();
        if (isLeftButtonPressed && !wasLeftButtonPressed) {
            Vector3f origin = camera.getPosition();
            Vector3f rotation = camera.getRotation();

            double yaw = Math.toRadians(rotation.y);
            double pitch = Math.toRadians(rotation.x);

            Vector3f direction = new Vector3f(
                    (float) (Math.cos(pitch) * Math.sin(yaw)),
                    (float) -Math.sin(pitch),
                    (float) -(Math.cos(pitch) * Math.cos(yaw))
            ).normalize();
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

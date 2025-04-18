package com.gameengine.core.builder;

import com.gameengine.core.Camera;
import com.gameengine.core.ObjectLoader;
import com.gameengine.core.entity.Entity;
import com.gameengine.core.entity.Model;
import com.gameengine.core.entity.SceneManager;
import com.gameengine.core.entity.Texture;
import com.gameengine.core.utils.Animation;
import com.gameengine.core.utils.Consts;
import com.gameengine.core.utils.RaycastHit;
import org.joml.Vector3f;

import java.util.Random;


public class BlockBuilder {
    private final SceneManager sceneManager;
    private final Camera camera;
    private String texture = "textures/grassblock.png";
    ObjectLoader loader;
    Model model = null;
    Model dirtModel = null;
    private boolean isTextureChanged = false;

    public BlockBuilder(SceneManager sceneManager, Camera camera, String texture) {
        this.sceneManager = sceneManager;
        this.camera = camera;
        this.texture = texture;
        loader = new ObjectLoader();
    }
    public void buildBlock () throws Exception {
        Vector3f origin = camera.getPosition();
        Vector3f pos = RaycastHit.raycastBlockHitPosition(origin, getDirection(), 10.0f, 0.1f, sceneManager);
        Model model = getModel();
        if (pos != null) {
             sceneManager.addEntity(new Entity(model, pos.add(RaycastHit.getOffset()),
                    new Vector3f(0, 0, 0), 1));
        }
    }

    public void removeBlock () throws Exception {
        Vector3f origin = camera.getPosition();
        Vector3f pos = RaycastHit.raycastBlockHitPosition(origin, getDirection(), 15.0f, 0.1f, sceneManager);
        Random rnd = new Random();
        if (pos != null) {
            sceneManager.removeEntity(pos);
            Entity en = new Entity(getModel(), pos, new Vector3f(0, 0, 0), 0.3f);
            sceneManager.addEntity(en);
            Animation.rotateOverTime(en, Consts.ROTATION_SPEED/2);
            for (int i = 0; i < 24; i++) {
                // Generate small random offsets (±0.5 units)
                float offsetX = (rnd.nextFloat() - 0.5f) * 1.0f; // Range: -0.5 to +0.5
                float offsetY = (rnd.nextFloat() - 0.5f) * 1.0f; // Range: -0.5 to +0.5
                float offsetZ = (rnd.nextFloat() - 0.5f) * 1.0f; // Range: -0.5 to +0.5
                Vector3f newPos = new Vector3f(pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ);

                Entity newEntity = new Entity(getDirtModel(), newPos, new Vector3f(0, 0, 0), 0.12f);
                sceneManager.addEntity(newEntity);
                Animation.rotateOverTime(newEntity, Consts.ROTATION_SPEED);
                switch (i%9){
                    case 0:
                        newPos = new Vector3f(newPos.x, newPos.y, -newPos.z);
                        break;
                    case 1:
                        newPos = new Vector3f(newPos.x, -newPos.y, newPos.z);
                        break;
                    case 2:
                        newPos = new Vector3f(-newPos.x, newPos.y, -newPos.z);
                        break;
                    case 3:
                        newPos = new Vector3f(-newPos.x, -newPos.y, newPos.z);
                        break;
                    case 4:
                        newPos = new Vector3f(newPos.x, -newPos.y, -newPos.z);
                        break;
                    case 5:
                        newPos = new Vector3f(-newPos.x, newPos.y, -newPos.z);
                        break;
                    case 6:
                        newPos = new Vector3f(-newPos.x, -newPos.y, -newPos.z);
                        break;
                    case 7:
                        newPos = new Vector3f(newPos.x, newPos.y, newPos.z);
                        break;
                    case 8:
                        newPos = new Vector3f(newPos.x, newPos.y, -newPos.z);
                        break;
                    default:
                        break;
                }
                Animation.explosion(newEntity, Consts.MOVEMENT_SPEED, newPos.normalize());
            }
        }
    }
    public void removeBlockWithoutAnimation () throws Exception {
        Vector3f origin = camera.getPosition();
        Vector3f pos = RaycastHit.raycastBlockHitPosition(origin, getDirection(), 15.0f, 0.1f, sceneManager);
        if (pos != null) {
            sceneManager.removeEntity(pos);
        }
    }
    public void randomBlocks () throws Exception {
        Model model = getModel();
        // Add entities
        Random rnd = new Random();
        for (int i = 0; i < 2000; i++) {
            int x = (int) Math.floor(rnd.nextFloat() * 800);
            int z = (int) Math.floor(rnd.nextFloat() * -800);
            sceneManager.addEntity(new Entity(model, new Vector3f(x, 0f, z),
                    new Vector3f(0, 0, 0), 1));
        }
        sceneManager.addEntity(new Entity(model, new Vector3f(0, 0f, -5),
                new Vector3f(0, 0, 0), 1));
    }

    private Vector3f getDirection(){
        Vector3f rotation = camera.getRotation();
        double yaw = Math.toRadians(rotation.y);
        double pitch = Math.toRadians(rotation.x);
        Vector3f direction = new Vector3f(
                (float) (Math.cos(pitch) * Math.sin(yaw)),
                (float) -Math.sin(pitch),
                (float) -(Math.cos(pitch) * Math.cos(yaw))
        );
        return direction.normalize();
    }
    public String getTexture() {
        return texture;
    }
    public void setTexture(String texture) {
        this.texture = texture;
        isTextureChanged = true;
    }
    public Model getModel() throws Exception {
        if (model == null) {
            model = loader.loadOBJModel("/models/cube.obj");
            model.setTexture(new Texture(loader.loadTexture(texture)), 1f);
            model.getMaterial().setDisableCulling(true);
        }
        if (isTextureChanged) {
            model.setTexture(new Texture(loader.loadTexture(texture)), 1f);
            isTextureChanged = false;
        }
        return model;
    }
    public Model getDirtModel() throws Exception {
        if (dirtModel == null) {
            dirtModel = loader.loadOBJModel("/models/cube.obj");
            dirtModel.setTexture(new Texture(loader.loadTexture("textures/dirt.png")), 1f);
            dirtModel.getMaterial().setDisableCulling(true);
        }
        return dirtModel;
    }
}

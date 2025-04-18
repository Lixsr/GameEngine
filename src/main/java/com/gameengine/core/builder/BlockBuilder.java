package com.gameengine.core.builder;

import com.gameengine.core.Camera;
import com.gameengine.core.ObjectLoader;
import com.gameengine.core.entity.Entity;
import com.gameengine.core.entity.Model;
import com.gameengine.core.entity.SceneManager;
import com.gameengine.core.entity.Texture;
import com.gameengine.core.utils.RaycastHit;
import org.joml.Vector3f;

import java.util.Random;


public class BlockBuilder {
    private final SceneManager sceneManager;
    private final Camera camera;
    private String texture = "grassblock.png";

    public BlockBuilder(SceneManager sceneManager, Camera camera, String texture) {
        this.sceneManager = sceneManager;
        this.camera = camera;
        this.texture = texture;

    }
    public void buildBlock () throws Exception {
        Vector3f origin = camera.getPosition();
        Vector3f pos = RaycastHit.raycastBlockHitPosition(origin, getDirection(), 15.0f, 0.1f, sceneManager.getEntities());
        Model model = getModel();
        if (pos != null) {
             sceneManager.addEntity(new Entity(model, pos.add(RaycastHit.getOffset()),
                    new Vector3f(0, 0, 0), 1));
        }
    }

    public void removeBlock () throws Exception {
        Vector3f origin = camera.getPosition();
        Vector3f pos = RaycastHit.raycastBlockHitPosition(origin, getDirection(), 15.0f, 0.1f, sceneManager.getEntities());
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
            sceneManager.addEntity(new Entity(model, new Vector3f(x, 0.5f, z),
                    new Vector3f(0, 0, 0), 1));
        }
        sceneManager.addEntity(new Entity(model, new Vector3f(0, 0.5f, -5),
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
    }
    public Model getModel() throws Exception {
        ObjectLoader loader = new ObjectLoader();
        Model model = loader.loadOBJModel("/models/cube.obj");
        model.setTexture(new Texture(loader.loadTexture(texture)), 1f);
        model.getMaterial().setDisableCulling(true);
        return model;
    }

}

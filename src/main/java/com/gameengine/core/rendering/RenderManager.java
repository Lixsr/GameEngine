package com.gameengine.core.rendering;

import com.gameengine.core.Camera;
import com.gameengine.core.ShaderManager;
import com.gameengine.core.WindowManager;
import com.gameengine.core.entity.Entity;
import com.gameengine.core.entity.Model;
import com.gameengine.core.lighting.DirectionalLight;
import com.gameengine.core.lighting.PointLight;
import com.gameengine.core.lighting.SpotLight;
import com.gameengine.core.utils.Consts;
import com.gameengine.test.Launcher;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;



public class RenderManager {
    private final WindowManager window;
    private EntityRender entityRenderer;

    public RenderManager(){
        window = Launcher.getWindow();
    }
    public void init() throws Exception {
        entityRenderer = new EntityRender();
        entityRenderer.init();
    }


    public static void renderLights(PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight, ShaderManager shader){
        shader.setUniform("ambientLight", Consts.AMBIENT_LIGHT);
        shader.setUniform("specularPower", Consts.SPECULAR_POWER);
        int numLights = spotLights != null ? spotLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            shader.setUniform("spotLights", spotLights[i], i);
        }
        numLights = pointLights != null ? pointLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            shader.setUniform("pointLights", pointLights[i], i);
        }
        shader.setUniform("directionalLight", directionalLight);

    }

    public void render(Camera camera, DirectionalLight directionalLight, PointLight[] pointLights, SpotLight[] spotLights) {
        clear();
        if(window.isResize()){
            GL11.glViewport(0,0, window.getWidth(), window.getHeight());
            window.setResize(false);
        }
        entityRenderer.render(camera, pointLights, spotLights, directionalLight);
    }

    public void processEntity(Entity entity){
        List<Entity> entityList = entityRenderer.getEntities().get(entity.getModel());
        if (entityList != null) {
            entityList.add(entity);
        }else {
            List<Entity> newEntityList = new ArrayList<>();
            newEntityList.add(entity);
            entityRenderer.getEntities().put(entity.getModel(), newEntityList);
        }


    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        entityRenderer.cleanup();
    }
}

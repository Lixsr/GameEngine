package com.gameengine.core.rendering;

import com.gameengine.core.Camera;
import com.gameengine.core.ShaderManager;
import com.gameengine.core.WindowManager;
import com.gameengine.core.entity.Entity;
import com.gameengine.core.entity.SceneManager;
import com.gameengine.core.lighting.DirectionalLight;
import com.gameengine.core.lighting.PointLight;
import com.gameengine.core.lighting.SpotLight;
import com.gameengine.core.entity.terrain.Terrain;
import com.gameengine.core.utils.Consts;
import com.gameengine.test.Launcher;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;



public class RenderManager {
    private final WindowManager window;
    private EntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;
    private CrosshairRenderer crosshairRenderer;
    private static boolean isCulling = false;

    public RenderManager(){
        window = Launcher.getWindow();
    }


    public void init() throws Exception {
        entityRenderer = new EntityRenderer();
        terrainRenderer = new TerrainRenderer();
        crosshairRenderer = new CrosshairRenderer();
        entityRenderer.init();
        terrainRenderer.init();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        crosshairRenderer.init();

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

    public void render(Camera camera, SceneManager scene) {
        clear();
        if(window.isResize()){
            GL11.glViewport(0,0, window.getWidth(), window.getHeight());
            window.setResize(false);
        }
        entityRenderer.render(camera, scene.getPointLights(), scene.getSpotLights(), scene.getDirectionalLight());
        terrainRenderer.render(camera, scene.getPointLights(), scene.getSpotLights(), scene.getDirectionalLight());
        crosshairRenderer.render(camera, scene.getPointLights(), scene.getSpotLights(), scene.getDirectionalLight());
    }

    public static void enableCulling(){
        if (!isCulling) {
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glCullFace(GL11.GL_BACK);
            isCulling = true;
        }
    }
    public static void disableCulling(){
        GL11.glDisable(GL11.GL_CULL_FACE);
        isCulling = false;
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
    public void processTerrain(Terrain terrain){
        terrainRenderer.getTerrain().add(terrain);
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        entityRenderer.cleanup();
        terrainRenderer.cleanup();
        crosshairRenderer.cleanup();
    }
}

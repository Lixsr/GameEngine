package com.gameengine.core.rendering;

import com.gameengine.core.Camera;
import com.gameengine.core.entity.Model;
import com.gameengine.core.lighting.DirectionalLight;
import com.gameengine.core.lighting.PointLight;
import com.gameengine.core.lighting.SpotLight;

public interface IRenderer<T> {
    public void init() throws Exception;
    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight);
    abstract void bind(Model model);
    public void unbind();
    public void prepare(T t, Camera camera);
    public void cleanup();
}

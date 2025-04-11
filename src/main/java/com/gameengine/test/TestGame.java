package com.gameengine.test;

import com.gameengine.core.ILogic;
import com.gameengine.core.ObjectLoader;
import com.gameengine.core.RenderManager;
import com.gameengine.core.WindowManager;
import com.gameengine.core.entity.Model;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class TestGame implements ILogic {
    private int direction = 0;
    private float colour = 0.0f;
    private final RenderManager renderer;
    private final WindowManager window;
    private final ObjectLoader loader;

    private Model model;


    public TestGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
    }

    @Override
    public void init() throws Exception {
        renderer.init();
        float[] SampleVertices = {
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
                -0.5f, 0.5f, 0f
        };
        model = loader.loadModel(SampleVertices);
    }

    @Override
    public void input() {
        if (window.isKeyPressed(GLFW.GLFW_KEY_W))
            direction = 1;
        else if (window.isKeyPressed(GLFW.GLFW_KEY_S))
            direction = -1;
        else
            direction = 0;
    }

    @Override
    public void update() {
        colour += direction * 0.01f;
        if (colour > 1)
            colour = 1.0f;
        else if (colour <= 0)
            colour = 0.0f;
    }

    @Override
    public void render() {
        if(window.isResize()){
            GL11.glViewport(0,0, window.getWidth(), window.getHeight());
            window.setResize(true);
        }

        window.setClearColour(colour, colour, colour, 0.0f);
        renderer.render(model);

    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
}

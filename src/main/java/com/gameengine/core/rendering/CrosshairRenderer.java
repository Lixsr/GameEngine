package com.gameengine.core.rendering;

import com.gameengine.core.Camera;
import com.gameengine.core.ShaderManager;
import com.gameengine.core.lighting.DirectionalLight;
import com.gameengine.core.lighting.PointLight;
import com.gameengine.core.lighting.SpotLight;
import com.gameengine.core.utils.Utils;
import com.gameengine.core.WindowManager;
import com.gameengine.test.Launcher;
import org.joml.Vector2f;
import org.lwjgl.opengl.*;

public class CrosshairRenderer implements IRenderer {
    private ShaderManager shader;
    private int vaoID;
    private int vboID;
    private final WindowManager window;

    public CrosshairRenderer() throws Exception {
        shader = new ShaderManager();
        window = Launcher.getWindow();
    }

    @Override
    public void init() throws Exception {
        // Create shader
        shader.createVertexShader(Utils.loadResource("/shaders/crosshair_vertex.vs"));
        shader.createFragmentShader(Utils.loadResource("/shaders/crosshair_fragment.fs"));
        shader.link();
        shader.createUniform("iResolution");

        // Fullscreen quad (NDC space)
        float[] quadVertices = {
                -1f,  1f,
                -1f, -1f,
                1f, -1f,
                -1f,  1f,
                1f, -1f,
                1f,  1f
        };

        vaoID = GL30.glGenVertexArrays();
        vboID = GL15.glGenBuffers();

        GL30.glBindVertexArray(vaoID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, quadVertices, GL15.GL_STATIC_DRAW);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 2 * Float.BYTES, 0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);  // Prevent crosshair from being occluded

        shader.bind();
        shader.setUniform("iResolution", new Vector2f(window.getWidth(), window.getHeight()));

        GL30.glBindVertexArray(vaoID);
        GL20.glEnableVertexAttribArray(0);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        shader.unbind();

        GL11.glEnable(GL11.GL_DEPTH_TEST);  // Re-enable for 3D
    }

    @Override
    public void bind(com.gameengine.core.entity.Model model) {
        // Not needed for fullscreen quad
    }

    @Override
    public void unbind() {
        // Not needed for fullscreen quad
    }

    @Override
    public void prepare(Object entity, Camera camera) {
        // Not used
    }

    @Override
    public void cleanup() {
        shader.cleanup();
        GL30.glDeleteVertexArrays(vaoID);
        GL15.glDeleteBuffers(vboID);
    }
}

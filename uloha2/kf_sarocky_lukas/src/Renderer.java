import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import solids.Grid;
import solids.GridEnum;


import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Renderer extends AbstractRenderer {
    //shadre
    private int shaderDittering;

    //textura
    private OGLTexture2D textureForObjects;

    //grid
    private Grid grid;

    //uniforms
    private int uScale;
    private int uModel;
    private int uRandom;
    private int uDiffusion;


    //premenne
    private float scale = 0.5f;
    private float thresholdDiff = 0.5f;
    private float thresholdRandom = 0.5f;
    private int mode = 3;


    @Override
    public void init() {

        shaderDittering = ShaderUtils.loadProgram("/dit");
        glUseProgram(shaderDittering);

        grid = new Grid(50, 50, GridEnum.LIST);

        uModel = glGetUniformLocation(shaderDittering, "uModel");
        uScale = glGetUniformLocation(shaderDittering, "uScale");
        uRandom = glGetUniformLocation(shaderDittering, "uRandom");
        uDiffusion = glGetUniformLocation(shaderDittering, "uDiffusion");

        try {
            textureForObjects = new OGLTexture2D("textures/sasuke3.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void display() {
        glUseProgram(shaderDittering);

        glViewport(0, 0, width, height * 2);
        glUniform1i(uModel, 0);
        grid.getBuffers().draw(GL_TRIANGLES, shaderDittering);

        if (mode == 1) {
            glUniform1i(uModel, 1);
            glUniform1f(uDiffusion, thresholdDiff);
            grid.getBuffers().draw(GL_TRIANGLES, shaderDittering);
        } else if (mode == 2) {
            glUniform1i(uModel, 2);
            glUniform1f(uRandom, thresholdRandom);
            grid.getBuffers().draw(GL_TRIANGLES, shaderDittering);
        } else if (mode == 3) {
            glUniform1i(uModel, 3);
            glUniform1f(uScale, scale);
            grid.getBuffers().draw(GL_TRIANGLES, shaderDittering);
        }
    }

    private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                switch (key) {
                    case GLFW_KEY_ESCAPE -> glfwSetWindowShouldClose(window, true);
                    case GLFW_KEY_1 -> mode = 1;
                    case GLFW_KEY_2 -> mode = 2;
                    case GLFW_KEY_3 -> mode = 3;
                }
            }
        }
    };

    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double dx, double dy) {
            switch (mode) {
                case 1:
                    if (dy > 0) {
                        thresholdDiff = thresholdDiff - 0.01f;
                    } else {
                        thresholdDiff = thresholdDiff + 0.01f;
                    }
                    break;
                case 2:
                    if (dy > 0) {
                        thresholdRandom = thresholdRandom - 0.01f;
                    } else {
                        thresholdRandom = thresholdRandom + 0.01f;
                    }
                    break;
                case 3:
                    if (dy > 0) {
                        scale = scale - 0.01f;
                    } else {
                        scale = scale + 0.01f;
                    }
                    break;
            }
        }
    };

    @Override
    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    @Override
    public GLFWScrollCallback getScrollCallback() {
        return scrollCallback;
    }
}
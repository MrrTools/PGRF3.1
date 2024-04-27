import lwjglutils.OGLRenderTarget;
import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
import lwjglutils.ToFloatArray;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import solids.Grid;
import solids.GridEnum;
import transforms.*;


import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer extends AbstractRenderer {

    //konstanty
    public static final float STEP = 0.2f;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    //shadre
    private int shaderProgramGrid;
    private int shaderProgramPostProcessing;

    //kamera
    private Camera camera;

    //projekce
    private Grid grid;
    private Grid postQuad;

    //textura
    private OGLTexture2D textureForObjects;

    //uniforms
    private int uView;
    private int uProj;
    private int uFunction;
    private int uModel;
    private int uLightPosition;
    private float uMorph;
    private int uColor;
    private int uTime;

    //transforamacie
    private Mat4 proj;
    private Mat4 lightTransl;
    Mat4 model;
    Mat4 rotation;
    Mat4 translation;


    //premenne
    private float speed = 1.0f;
    private float colorType = 0;
    private float functionType = 1;
    private int mode = 1;


    private boolean mousePressed = false;
    private double oldx, oldy;
    private OGLRenderTarget renderTarget;
    private int button;
    Vec3D lightPos;
    private double ligtSpeed;


    @Override
    public void init() {

        camera = new Camera()
                .withPosition(new Vec3D(3, 3, 3))
                .withAzimuth(5 / 4f * Math.PI)
                .withZenith(-1 / 5f * Math.PI);

        proj = new Mat4PerspRH(Math.PI / 3, height / (float) width, 0.1f, 100);

        model = new Mat4Identity();
        rotation = new Mat4Identity();
        translation = new Mat4Identity();

        grid = new Grid(50, 50, GridEnum.LIST);
        shaderProgramGrid = ShaderUtils.loadProgram("/grid");
        glUseProgram(shaderProgramGrid);

        postQuad = new Grid(2, 2, GridEnum.LIST);
        shaderProgramPostProcessing = ShaderUtils.loadProgram("/postProcessing");

        uView = glGetUniformLocation(shaderProgramGrid, "uView");
        uProj = glGetUniformLocation(shaderProgramGrid, "uProj");
        uFunction = glGetUniformLocation(shaderProgramGrid, "uFunction");
        uModel = glGetUniformLocation(shaderProgramGrid, "uModel");
        uColor = glGetUniformLocation(shaderProgramGrid, "colorType");
        uLightPosition = glGetUniformLocation(shaderProgramGrid, "lightPosition");
        uTime = glGetUniformLocation(shaderProgramGrid, "uTime");

        lightPos = new Vec3D(2, 2, 1.5);

        renderTarget = new OGLRenderTarget(800, 600);

        try {
            textureForObjects = new OGLTexture2D("textures/bricks.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        lightTransl = new Mat4Transl(lightPos);
    }

    @Override
    public void display() {
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.5f, 1.0f, 0.5f, 1.0f);

        glUseProgram(shaderProgramGrid);
        renderTarget.bind();

        glUniformMatrix4fv(uView, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(uProj, false, ToFloatArray.convert(proj));
        glUniform3fv(uLightPosition, ToFloatArray.convert(lightPos));
        glUniformMatrix4fv(uModel, false, ToFloatArray.convert(model));


        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUniform1f(uTime, (float) glfwGetTime());


        glUniform1f(uFunction, functionType);
        glUniform1f(uColor, colorType);

        textureForObjects.bind(shaderProgramGrid, "textureForObjects", 0);
        grid.getBuffers().draw(GL_TRIANGLES, shaderProgramGrid);


        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        ligtSpeed += 0.01;
        double lightX = 2 * Math.sin(ligtSpeed);
        double lightY = 2 * Math.cos(ligtSpeed);
        lightPos = new Vec3D(lightX, lightY, 1);

        lightTransl = new Mat4Transl(lightPos);

        uMorph = (float) Math.sin(glfwGetTime() * speed) * 0.5f + 0.5f;
        int uMorphLocation = glGetUniformLocation(shaderProgramGrid, "uMorph");
        glUniform1f(uMorphLocation, uMorph);

        glUniform1f(uFunction, 8f);

        glUniformMatrix4fv(uModel, false, ToFloatArray.convert(lightTransl));
        grid.getBuffers().draw(GL_TRIANGLES, shaderProgramGrid);
        renderPostProcessingQuad();


        glDisable(GL_DEPTH_TEST);

    }

    private void renderPostProcessingQuad() {
        glViewport(0, 0, width, height);
        glUseProgram(shaderProgramPostProcessing);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        renderTarget.getColorTexture().bind(shaderProgramPostProcessing, "textureScene", 0);
        postQuad.getBuffers().draw(GL_TRIANGLES, shaderProgramPostProcessing);

        switch (mode) {
            case 0 -> glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            case 1 -> glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            case 2 -> glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
        }
    }


    private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                switch (key) {
                    case GLFW_KEY_ESCAPE -> glfwSetWindowShouldClose(window, true);
                    case GLFW_KEY_0 -> functionType = 0;
                    case GLFW_KEY_1 -> functionType = 1;
                    case GLFW_KEY_2 -> functionType = 2;
                    case GLFW_KEY_3 -> functionType = 3;
                    case GLFW_KEY_4 -> functionType = 4;
                    case GLFW_KEY_5 -> functionType = 5;
                    case GLFW_KEY_6 -> functionType = 6;
                    case GLFW_KEY_7 -> mode = 0;
                    case GLFW_KEY_8 -> mode = 1;
                    case GLFW_KEY_9 -> mode = 2;
                    case GLFW_KEY_W -> camera = camera.up(STEP);
                    case GLFW_KEY_D -> camera = camera.right(STEP);
                    case GLFW_KEY_S -> camera = camera.down(STEP);
                    case GLFW_KEY_A -> camera = camera.left(STEP);
                    case GLFW_KEY_P -> proj = new Mat4PerspRH(Math.PI / 3, HEIGHT / (float) WIDTH, 0.1f, 100);
                    case GLFW_KEY_O -> proj = new Mat4OrthoRH(2.5, 2.5, 0.1, 20);
                    case GLFW_KEY_KP_ADD -> speed = speed + 0.5f;
                    case GLFW_KEY_KP_SUBTRACT -> {
                        speed = speed - 0.5f;
                        if (speed < 0.0f) {
                            speed = 0.0f;
                        }
                    }
                    case GLFW_KEY_C -> {
                        if (colorType == 7) {
                            colorType = 0;
                        } else {
                            colorType = colorType + 1;
                            System.out.println(colorType);
                        }
                    }


                }
            }
        }
    };

    private GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int w, int h) {
        }
    };

    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int buttonLocal, int action, int mods) {
            button = buttonLocal;
            double[] xPos = new double[1];
            double[] yPos = new double[1];
            glfwGetCursorPos(window, xPos, yPos);
            oldx = xPos[0];
            oldy = yPos[0];
            mousePressed = action == GLFW_PRESS;
        }

    };

    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (mousePressed) {
                if (button == GLFW_MOUSE_BUTTON_LEFT) {
                    camera = camera.addAzimuth(Math.PI / 2 * (oldx - x) / WIDTH);
                    camera = camera.addZenith(Math.PI / 2 * (oldy - y) / HEIGHT);


                    oldx = x;
                    oldy = y;
                } else if (button == GLFW_MOUSE_BUTTON_RIGHT) {

                    double rotX = (oldx - x) / 50;
                    double rotY = (oldy - y) / 50;
                    rotation = rotation.mul(new Mat4RotXYZ(rotX, 0, rotY));
                    model = rotation.mul(translation);
                    oldx = x;
                    oldy = y;
                }
            }
        }
    };

    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double dx, double dy) {
            if (dy > 0) {
                camera = camera.backward(1);
            } else {
                camera = camera.forward(1);
            }
        }
    };

    @Override
    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    @Override
    public GLFWWindowSizeCallback getWsCallback() {
        return wsCallback;
    }

    @Override
    public GLFWMouseButtonCallback getMouseCallback() {
        return mbCallback;
    }

    @Override
    public GLFWCursorPosCallback getCursorCallback() {
        return cpCallbacknew;
    }

    @Override
    public GLFWScrollCallback getScrollCallback() {
        return scrollCallback;
    }
}
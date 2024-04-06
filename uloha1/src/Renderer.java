import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
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
import static org.lwjgl.opengl.GL20.*;

public class Renderer extends AbstractRenderer {

    public static final float STEP = 0.2f;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private int shaderProgramTriangle, shaderProgramGrid;
    private OGLTexture2D textureForObjects;
    private Grid grid;
    private Camera camera;
    private Mat4 proj;
    Mat4 model;
    Mat4 rotation;
    Mat4 translation;
    private int functionType = 1;
    private int button;
    private boolean mousePressed = false;
    private double oldx, oldy;
    float colorType = 0;
    private float uMorph;
    private float speed = 1.0f;

    @Override
    public void init() {

        camera = new Camera()
                .withPosition(new Vec3D(0f, -2f, 2.f))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-45))
                .withFirstPerson(true);

        proj = new Mat4PerspRH(Math.PI / 4, height / (float) width, 0.1f, 100.f);

        model = new Mat4Identity();
        rotation = new Mat4Identity();
        translation = new Mat4Identity();

        shaderProgramTriangle = ShaderUtils.loadProgram("/triangle");
        glUseProgram(shaderProgramTriangle);

        int uColor = glGetUniformLocation(shaderProgramTriangle, "uColor");
        glUniform3fv(uColor, new float[]{0.0f, 1.0f, 0.0f});

        grid = new Grid(20, 20, GridEnum.LIST);
        shaderProgramGrid = ShaderUtils.loadProgram("/grid");
        glUseProgram(shaderProgramGrid);

        int uView = glGetUniformLocation(shaderProgramGrid, "uView");
        glUniformMatrix4fv(uView, false, camera.getViewMatrix().floatArray());

        int uProj = glGetUniformLocation(shaderProgramGrid, "uProj");
        glUniformMatrix4fv(uProj, false, proj.floatArray());

        int uFunction = glGetUniformLocation(shaderProgramGrid, "uFunction");
        glUniform1i(uFunction, functionType);

        int uColors = glGetUniformLocation(shaderProgramGrid, "uColors");
        glUniform1f(uColors, colorType);


        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);


        try {
            textureForObjects = new OGLTexture2D("textures/bricks.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        textureForObjects.bind(shaderProgramGrid, "textureForObjects", 0);

    }

    @Override
    public void display() {
        glClearColor(1.0f, 1.0f, 0.5f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        int uView = glGetUniformLocation(shaderProgramGrid, "uView");
        glUniformMatrix4fv(uView, false, camera.getViewMatrix().floatArray());

        int uProj = glGetUniformLocation(shaderProgramGrid, "uProj");
        glUniformMatrix4fv(uProj, false, proj.floatArray());

        int uFunction = glGetUniformLocation(shaderProgramGrid, "uFunction");
        glUniform1i(uFunction, functionType);

        int uTime = glGetUniformLocation(shaderProgramGrid, "uTime");
        glUniform1f(uTime, (float) glfwGetTime());

        grid.getBuffers().draw(GL_TRIANGLES, shaderProgramGrid);
        textureForObjects.bind(shaderProgramGrid, "textureForObjects", 0);

        int uColors = glGetUniformLocation(shaderProgramGrid, "colorType");
        glUniform1f(uColors, colorType);

        uMorph = (float) Math.sin(glfwGetTime() * speed) * 0.5f + 0.5f;
        int uMorphLocation = glGetUniformLocation(shaderProgramGrid, "uMorph");
        glUniform1f(uMorphLocation, uMorph);
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
                    case GLFW_KEY_7 -> glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                    case GLFW_KEY_8 -> glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                    case GLFW_KEY_9 -> glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
                    case GLFW_KEY_W -> camera = camera.up(STEP);
                    case GLFW_KEY_D -> camera = camera.right(STEP);
                    case GLFW_KEY_S -> camera = camera.down(STEP);
                    case GLFW_KEY_A -> camera = camera.left(STEP);
                    case GLFW_KEY_P -> proj = new Mat4PerspRH(Math.PI / 3, HEIGHT / (float) WIDTH, 0.1, 20);
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
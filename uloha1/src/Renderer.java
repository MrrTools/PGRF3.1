import lwjglutils.OGLBuffers;
import lwjglutils.ShaderUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import solids.Grid;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

import static org.lwjgl.opengl.GL20.*;

/**
 * @author PGRF FIM UHK
 * @version 2.0
 * @since 2019-09-02
 */
public class Renderer extends AbstractRenderer {
    private int shaderProgramTriangle, shaderProgramGrid;
    private OGLBuffers buffers;
    private Grid grid;
    private Camera camera;
    private Mat4 proj;

    @Override
    public void init() {

        camera = new Camera()
                .withPosition(new Vec3D(0f, -2f, 2.f))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-45))
                .withFirstPerson(true);

        proj = new Mat4PerspRH(Math.PI / 4, height / (float)width, 0.1f, 100.f);


        // vb
        float[] vb = {
             0.0f,  1.0f,   1.0f, 0.0f, 0.0f,
            -1.0f,  0.0f,   0.0f, 1.0f, 0.0f,
             1.0f, -1.0f,   0.0f, 0.0f, 1.0f
        };

        // ib
        int[] ib = {
            0, 1, 2
        };

        OGLBuffers.Attrib[] attributes = {
          new OGLBuffers.Attrib("inPosition", 2),
          new OGLBuffers.Attrib("inColor", 3),
        };

        buffers = new OGLBuffers(vb, attributes, ib);

        shaderProgramTriangle = ShaderUtils.loadProgram("/triangle");
        glUseProgram(shaderProgramTriangle);

        int uColor = glGetUniformLocation(shaderProgramTriangle, "uColor");
        glUniform3fv(uColor, new float[] {0.0f, 1.0f, 0.0f});

        grid = new Grid(15, 15);
        shaderProgramGrid = ShaderUtils.loadProgram("/grid");
        glUseProgram(shaderProgramGrid);

        int uView = glGetUniformLocation(shaderProgramGrid, "uView");
        glUniformMatrix4fv(uView, false, camera.getViewMatrix().floatArray());

        int uProj = glGetUniformLocation(shaderProgramGrid, "uProj");
        glUniformMatrix4fv(uProj, false, proj.floatArray());

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }

    @Override
    public void display() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        //buffers.draw(GL_TRIANGLES, shaderProgramTriangle);
        grid.getBuffers().draw(GL_TRIANGLES, shaderProgramGrid);
    }

    private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
        }
    };

    private GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int w, int h) {
        }
    };

    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {

        }

    };

    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
        }
    };

    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double dx, double dy) {
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
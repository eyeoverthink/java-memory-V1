package renderer;

import jade.Camera;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.AssetPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class DebugDraw {

    private static final int MAX_LINES = 10000;
    private static final int VERTEX_SIZE = 6;
    private static List<Line2D> lines = new ArrayList<>();
    private static float[] vertexArray = new float[MAX_LINES * 2 * VERTEX_SIZE];
    private static Shader shader;
    private static int vaoID;
    private static int vboID;
    private static boolean started = false;

    public static void start() {
        shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl");

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertexArray.length * Float.BYTES, GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, VERTEX_SIZE * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glLineWidth(2.0f);

        started = true;
    }

    public static void beginFrame() {
        if (!started) {
            start();
        }
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).beginFrame() < 0) {
                lines.remove(i);
                i--;
            }
        }
    }

    public static void draw() {
        if (lines.size() <= 0) return;

        int index = 0;
        for (Line2D line : lines) {
            if (index >= MAX_LINES) break;

            Vector2f from = line.getFrom();
            Vector2f to = line.getTo();
            Vector3f color = line.getColor();

            int offset = index * 2 * VERTEX_SIZE;

            vertexArray[offset]     = from.x;
            vertexArray[offset + 1] = from.y;
            vertexArray[offset + 2] = 0.0f;
            vertexArray[offset + 3] = color.x;
            vertexArray[offset + 4] = color.y;
            vertexArray[offset + 5] = color.z;

            vertexArray[offset + 6]  = to.x;
            vertexArray[offset + 7]  = to.y;
            vertexArray[offset + 8]  = 0.0f;
            vertexArray[offset + 9]  = color.x;
            vertexArray[offset + 10] = color.y;
            vertexArray[offset + 11] = color.z;

            index++;
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, Arrays.copyOfRange(vertexArray, 0, index * 2 * VERTEX_SIZE));

        Camera camera = Window.getCamera();
        shader.use();
        shader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        shader.uploadMat4f("uView", camera.getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_LINES, 0, index * 2);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        shader.detach();
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color, int lifetime) {
        if (lines.size() >= MAX_LINES) return;
        lines.add(new Line2D(from, to, color, lifetime));
    }

    public static void addLine2D(Vector2f from, Vector2f to, Vector3f color) {
        addLine2D(from, to, color, 1);
    }

    public static void addCircle(Vector2f center, float radius, Vector3f color, int lifetime) {
        int segments = 32;
        Vector2f[] points = new Vector2f[segments];
        float increment = (float) (2.0f * Math.PI / segments);

        for (int i = 0; i < segments; i++) {
            float angle = increment * i;
            float x = (float) (radius * Math.cos(angle)) + center.x;
            float y = (float) (radius * Math.sin(angle)) + center.y;
            points[i] = new Vector2f(x, y);
        }

        for (int i = 0; i < segments; i++) {
            addLine2D(points[i], points[(i + 1) % segments], color, lifetime);
        }
    }

    public static void addCircle(Vector2f center, float radius, Vector3f color) {
        addCircle(center, radius, color, 1);
    }
}

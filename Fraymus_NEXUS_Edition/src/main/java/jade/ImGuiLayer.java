package jade;

import imgui.ImFontAtlas;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiMouseCursor;
import imgui.gl3.ImGuiImplGl3;

import static org.lwjgl.glfw.GLFW.*;

public class ImGuiLayer {

    private long glfwWindow;
    private ImGuiImplGl3 imGuiGl3;
    private final long[] mouseCursors = new long[ImGuiMouseCursor.COUNT];

    public ImGuiLayer(long glfwWindow) {
        this.glfwWindow = glfwWindow;
    }

    public void initImGui() {
        ImGui.createContext();

        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.setIniFilename("imgui.ini");

        io.setKeyMap(ImGuiKey.Tab, GLFW_KEY_TAB);
        io.setKeyMap(ImGuiKey.LeftArrow, GLFW_KEY_LEFT);
        io.setKeyMap(ImGuiKey.RightArrow, GLFW_KEY_RIGHT);
        io.setKeyMap(ImGuiKey.UpArrow, GLFW_KEY_UP);
        io.setKeyMap(ImGuiKey.DownArrow, GLFW_KEY_DOWN);
        io.setKeyMap(ImGuiKey.PageUp, GLFW_KEY_PAGE_UP);
        io.setKeyMap(ImGuiKey.PageDown, GLFW_KEY_PAGE_DOWN);
        io.setKeyMap(ImGuiKey.Home, GLFW_KEY_HOME);
        io.setKeyMap(ImGuiKey.End, GLFW_KEY_END);
        io.setKeyMap(ImGuiKey.Insert, GLFW_KEY_INSERT);
        io.setKeyMap(ImGuiKey.Delete, GLFW_KEY_DELETE);
        io.setKeyMap(ImGuiKey.Backspace, GLFW_KEY_BACKSPACE);
        io.setKeyMap(ImGuiKey.Space, GLFW_KEY_SPACE);
        io.setKeyMap(ImGuiKey.Enter, GLFW_KEY_ENTER);
        io.setKeyMap(ImGuiKey.Escape, GLFW_KEY_ESCAPE);
        io.setKeyMap(ImGuiKey.KeyPadEnter, GLFW_KEY_KP_ENTER);
        io.setKeyMap(ImGuiKey.A, GLFW_KEY_A);
        io.setKeyMap(ImGuiKey.C, GLFW_KEY_C);
        io.setKeyMap(ImGuiKey.V, GLFW_KEY_V);
        io.setKeyMap(ImGuiKey.X, GLFW_KEY_X);
        io.setKeyMap(ImGuiKey.Y, GLFW_KEY_Y);
        io.setKeyMap(ImGuiKey.Z, GLFW_KEY_Z);

        ImFontAtlas fontAtlas = io.getFonts();
        fontAtlas.addFontDefault();
        fontAtlas.build();

        mouseCursors[ImGuiMouseCursor.Arrow] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.TextInput] = glfwCreateStandardCursor(GLFW_IBEAM_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeAll] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNS] = glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeEW] = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNESW] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNWSE] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.Hand] = glfwCreateStandardCursor(GLFW_HAND_CURSOR);
        mouseCursors[ImGuiMouseCursor.NotAllowed] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);

        ImGui.styleColorsDark();

        imGuiGl3 = new ImGuiImplGl3();
        imGuiGl3.init("#version 330 core");
    }

    public void startFrame(float dt) {
        ImGuiIO io = ImGui.getIO();

        int[] winWidth = new int[1];
        int[] winHeight = new int[1];
        int[] fbWidth = new int[1];
        int[] fbHeight = new int[1];
        glfwGetWindowSize(glfwWindow, winWidth, winHeight);
        glfwGetFramebufferSize(glfwWindow, fbWidth, fbHeight);

        io.setDisplaySize((float) winWidth[0], (float) winHeight[0]);
        io.setDisplayFramebufferScale(
                (float) fbWidth[0] / winWidth[0],
                (float) fbHeight[0] / winHeight[0]
        );

        io.setDeltaTime(dt > 0 ? dt : 1.0f / 60.0f);

        double[] mouseX = new double[1];
        double[] mouseY = new double[1];
        glfwGetCursorPos(glfwWindow, mouseX, mouseY);
        io.setMousePos((float) mouseX[0], (float) mouseY[0]);

        io.setMouseDown(0, glfwGetMouseButton(glfwWindow, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS);
        io.setMouseDown(1, glfwGetMouseButton(glfwWindow, GLFW_MOUSE_BUTTON_2) == GLFW_PRESS);
        io.setMouseDown(2, glfwGetMouseButton(glfwWindow, GLFW_MOUSE_BUTTON_3) == GLFW_PRESS);

        int cursor = ImGui.getMouseCursor();
        if (cursor >= 0 && cursor < mouseCursors.length && mouseCursors[cursor] != 0) {
            glfwSetCursor(glfwWindow, mouseCursors[cursor]);
        } else {
            glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }

        ImGui.newFrame();
    }

    public void endFrame() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    public void destroyImGui() {
        if (imGuiGl3 != null) {
            imGuiGl3.dispose();
        }
        ImGui.destroyContext();
    }
}

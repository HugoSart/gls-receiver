package com.hugovs.gls.extensions;

import com.hugovs.gls.receiver.AudioData;
import com.hugovs.gls.receiver.AudioServerExtension;
import com.hugovs.gls.receiver.DataListener;
import com.hugovs.gls.util.ByteUtils;
import com.hugovs.gls.util.StringUtils;
import com.hugovs.gls.util.SynchronizedData;
import org.apache.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * AudioServerExtension to show the received audio data as sound waves graphically.
 *
 * @author Hugo Sartori
 */
public class WaveDrawer extends AudioServerExtension implements DataListener {

    private static final Logger log = Logger.getLogger(WaveDrawer.class);
    private Thread thread;

    // LWJGL
    private long window;

    private SynchronizedData<byte[]> dataToRender = new SynchronizedData<>();

    @Override
    public void onDataReceived(AudioData audioData) {
        dataToRender.setData(audioData.getSamples());
    }

    @Override
    public void onServerStart() {
        thread = new Thread(this::run);
        thread.start();
    }

    @Override
    public void onServerClose() {
        thread.interrupt();
    }

    public void run() {
        log.info("Initializing WaveDrawer with LWJGL " + Version.getVersion() + ".");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();

    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        window = glfwCreateWindow(800, 600, "GLS Receiver", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        glfwSetWindowSizeCallback(window, (l, i, i1) -> {
            glViewport(0, 0, i, i1);
        });

    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.25f);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        double lastTime = glfwGetTime();
        log.info("Inside loop");

        glEnableClientState(GL_VERTEX_ARRAY);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {

            log.debug("Waiting for data");
            double currentTime = glfwGetTime();
            double deltaTime = currentTime - lastTime;
            lastTime = currentTime;

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            render(deltaTime);
            glfwSwapBuffers(window);

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            //syncFramerate.sync(120);
        }

        glDisableClientState(GL_VERTEX_ARRAY);
    }

    private void render(double deltaTime) {

        // Draw wave
        byte[] data = dataToRender.getData();
        if (data == null) return;
        glBegin(GL_LINE_LOOP);
        int length = data.length;
        glVertex2d(-2, 2);
        for (int i = 0; i < length; i++) {
            if (i % 2 == 0) continue;
            byte b = data[i];
            float x = (((float) i) / ((float) length)) * 2f - 1f;
            float y = (((float) b) / (127f));
            glVertex2d(x, y);
        }
        glVertex2d(2, 2);
        glEnd();

    }

}


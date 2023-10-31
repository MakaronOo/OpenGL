package com.example.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.example.opengl.MyRenderers.MyGLRenderer;
import com.example.opengl.MyRenderers.MySquareRenderer;

public class SquareSurfaceView extends GLSurfaceView {

    public SquareSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        MyGLRenderer renderer;
        renderer = new MySquareRenderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
    }
}

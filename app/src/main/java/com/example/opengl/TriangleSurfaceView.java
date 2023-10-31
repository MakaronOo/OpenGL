package com.example.opengl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.example.opengl.MyRenderers.MyTriangleRenderer;
import com.example.opengl.MyRenderers.MyGLRenderer;

public class TriangleSurfaceView extends GLSurfaceView {
    MyGLRenderer renderer;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX;
    private float previousY;
    public TriangleSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        renderer = new MyTriangleRenderer(context);


        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e){
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = x - previousX;
            float dy = y - previousY;

            // reverse direction of rotation above the mid-line
            if (y > (double)getHeight() / 2) {
                dx = dx * -1;
            }

            // reverse direction of rotation to left of the mid-line
            if (x < (double)getWidth() / 2) {
                dy = dy * -1;
            }

            renderer.setAngle(
                    renderer.getAngle() +
                            ((dx + dy) * TOUCH_SCALE_FACTOR));
            requestRender();
        }

        previousX = x;
        previousY = y;
        return true;
    }
}
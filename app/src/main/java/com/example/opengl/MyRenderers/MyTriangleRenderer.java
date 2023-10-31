package com.example.opengl.MyRenderers;

import android.content.Context;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.example.opengl.Figures.Figure;
import com.example.opengl.Figures.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyTriangleRenderer extends MyGLRenderer {
    Figure figure;
    private final Context context;

    private final float[] rotationMatrix = new float[16];
    public MyTriangleRenderer(Context context){
        super();
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        super.onSurfaceCreated(unused, config);
        figure = new Triangle(context);
        //figure = new Pyramid(context);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];
        super.onDrawFrame(unused);
        // Create a rotation transformation for the triangle
//        long time = SystemClock.uptimeMillis() % 1000L;
//        float mAngle = 0.090f * ((int) time);
//        //mAngle = 45.0f;
//        if(mAngle >= 90f) mAngle = 0f;
        Matrix.setRotateM(rotationMatrix, 0, mAngle, 0, 1, 0);

        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);

        // Draw figure
        figure.draw(scratch);
    }

}

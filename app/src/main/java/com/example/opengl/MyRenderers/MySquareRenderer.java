package com.example.opengl.MyRenderers;

import static java.lang.Math.*;

import android.opengl.Matrix;
import android.os.SystemClock;

import com.example.opengl.Figures.Cube;
import com.example.opengl.Figures.Square;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MySquareRenderer extends MyGLRenderer {
    //Square mSquare = new Square();
    Cube mSquare = new Cube();
    private final float[] rotationMatrix = new float[16];
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        super.onSurfaceCreated(unused, config);
        //mSquare = new Square();
        mSquare = new Cube();
    }
    float angle_ = 0.0f;
    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];
        super.onDrawFrame(unused);
        // Create a rotation transformation for the triangle
        long time = SystemClock.uptimeMillis() % 7200L;
        float angle = 0.1f * ((int) time);
        Matrix.setRotateM(rotationMatrix, 0, angle_, 1, 1, 0);
        angle_ = (angle_+1)%360;

        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0);

        // Draw triangle
        mSquare.draw(scratch);
    }
}


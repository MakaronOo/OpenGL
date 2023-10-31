package com.example.opengl.Figures;

import android.opengl.GLES20;

import com.example.opengl.MyRenderers.MyGLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Cube {

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float cubeCoords[] = {
            //forward
            -0.25f,  0.25f, 0.25f,   // top left
            -0.25f, -0.25f, 0.25f,   // bottom left
            0.25f, -0.25f, 0.25f,   // bottom right
            0.25f,  0.25f, 0.25f,     // top right
            //backward
            -0.25f,  0.25f, -0.25f,   // top left
            -0.25f, -0.25f, -0.25f,   // bottom left
            0.25f, -0.25f, -0.25f,   // bottom right
            0.25f,  0.25f, -0.25f,    // top right
            //left
            -0.25f,  0.25f, -0.25f,   // top left
            -0.25f, -0.25f, -0.25f,   // bottom left
            -0.25f, -0.25f, 0.25f,   // bottom right
            -0.25f,  0.25f, 0.25f,    // top right
            //right
            0.25f,  0.25f, 0.25f,   // top left
            0.25f, -0.25f, 0.25f,   // bottom left
            0.25f, -0.25f, -0.25f,   // bottom right
            0.25f,  0.25f, -0.25f,    // top right
            //up
            -0.25f,  0.25f, 0.25f,   // top left
            -0.25f, 0.25f, -0.25f,   // bottom left
            0.25f, 0.25f, -0.25f,   // bottom right
            0.25f,  0.25f, 0.25f,    // top right
            //down
            -0.25f,  -0.25f, 0.25f,   // top left
            -0.25f, -0.25f, -0.25f,   // bottom left
            0.25f, -0.25f, -0.25f,   // bottom right
            0.25f,  -0.25f, 0.25f,    // top right
    };

    private short drawOrder[] = {
            0, 1, 2, 0, 2, 3,
            4, 5, 6, 4, 6, 7,
            8,9,10,8,10,11,
            12,13,14,12,14,15,
            16,17,18,16,18,19,
            20,21,22,20,22,23
    }; // order to draw vertices


    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // The matrix must be included as a modifier of gl_Position.
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";


    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
    private final int mProgram; //for shaders

    private int positionHandle;
    private int colorHandle;

    private int vPMatrixHandle;

    private final int vertexCount = cubeCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    public Cube() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                cubeCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(cubeCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        //loading shaders
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
    }

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the cube
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer
        );

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
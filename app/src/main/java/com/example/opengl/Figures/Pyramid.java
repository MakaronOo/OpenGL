package com.example.opengl.Figures;

import static android.opengl.GLES20.glGenTextures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.example.opengl.MyRenderers.MyGLRenderer;
import com.example.opengl.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Pyramid implements Figure {

    private FloatBuffer vertexBuffer;
    private FloatBuffer texVertexBuffer;
    private ShortBuffer drawListBuffer;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static final int TEX_COORDS_PER_VERTEX = 2;
    static float vertexCoords[] = {
             0.0f, 0.7f, 0.0f,

            -0.5f, 0.0f,  0.5f, //left for
             0.5f, 0.0f,  0.5f, //right for
            -0.5f, 0.0f, -0.5f, //left back
             0.5f, 0.0f, -0.5f,  //right back
    };
    static float[] texVertexCoords = {
            0.5f, 0.0f,
            0.0f, 1.0f, //left
            1.0f, 1.0f, //right
            1.0f, 1.0f, //right
            0.0f, 1.0f, //left
    };
    private short drawOrder[] = {
            0, 1, 2,
            0, 1, 3,
            0, 1, 3,
            //0, 4, 2,
    };


    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "attribute vec2 texCoord;" +
                    "varying vec2 v_texCoord;" +
                    "uniform mat4 uMVPMatrix;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  v_texCoord = texCoord;" +
                    "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec2 v_texCoord;" +
                    "uniform sampler2D u_Texture;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D(u_Texture, v_texCoord);" +
                    "}";


    private final int mProgram;

    private int positionHandle;
    private int texCoordHandle;
    private int u_TextureHandle;
    private int uMVPMatrixHandle;
    private int textureHandle;

    private final int vertexCount = vertexCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4;
    private final int texVertexStride = TEX_COORDS_PER_VERTEX * 4;
    public Pyramid(Context context) {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                vertexCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertexCoords);
        vertexBuffer.position(0);

        // initialize texture vertex byte buffer for shape coordinates
        bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                texVertexCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        texVertexBuffer = bb.asFloatBuffer();
        texVertexBuffer.put(texVertexCoords);
        texVertexBuffer.position(0);

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

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        texCoordHandle = GLES20.glGetAttribLocation(mProgram, "texCoord");
        u_TextureHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        uMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        textureHandle = loadTexture(context, R.drawable.bill_texture);
    }
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(texCoordHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        GLES20.glVertexAttribPointer(texCoordHandle, TEX_COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                texVertexStride, texVertexBuffer);

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Активация текстурного блока
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
        GLES20.glUniform1i(u_TextureHandle, 0);

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer
        );

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
    }

    public int loadTexture(Context context, int resourceId) {
        final int[] textureIds = new int[1];
        glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            return 0;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resourceId, options);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);

        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textureIds[0];
    }
}
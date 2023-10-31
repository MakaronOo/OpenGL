package com.example.opengl.Figures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.example.opengl.MyRenderers.MyGLRenderer;
import com.example.opengl.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle implements Figure {
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

    private final FloatBuffer vertexBuffer;
    private final FloatBuffer texVertexBuffer;
    private final int mProgram;

    private int positionHandle;
    private int texCoordHandle;
    private int mTextureUniformHandle;
    private int mTextureDataHandle;

    // Координаты вершин треугольника
    static float triangleCoords[] = {
            0.0f, 0.6f, 0.0f,   // верхний центр
            -0.5f, -0.5f, 0.0f,  // нижний левый угол
            0.5f, -0.5f, 0.0f   // нижний правый угол
    };

    // Текстурные координаты
    static float textureCoords[] = {
            0.5f, 0.0f,  // верхний центр
            0.0f, 1.0f,  // нижний левый угол
            1.0f, 1.0f   // нижний правый угол
    };

    private final int COORDS_PER_VERTEX = 3;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private int mMVPMatrixHandle;
    private float[] mModelMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    public Triangle(Context c) {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();    // create a floating point buffer from the ByteBuffer
        vertexBuffer.put(triangleCoords);     // add the coordinates to the FloatBuffer
        vertexBuffer.position(0);   // set the buffer to read the first coordinate

        bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                textureCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        texVertexBuffer = bb.asFloatBuffer();
        texVertexBuffer.put(textureCoords);
        texVertexBuffer.position(0);

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);


        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // Получение атрибута в вершинном шейдере
        texCoordHandle = GLES20.glGetAttribLocation(mProgram, "texCoord");
        // get handle to fragment shader's vColor member
        //colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        mTextureDataHandle = loadTexture(c, R.drawable.bill_texture);
    }

    public void draw(float[] mvpMatrix) {
        // Добавляем вращение к матрице модели
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.rotateM(mModelMatrix, 0, 0, 0.0f, 1.0f, 0.0f);

        // Комбинирование матриц: MVP = Model * View * Projection
        Matrix.multiplyMM(mMVPMatrix, 0, mvpMatrix, 0, mModelMatrix, 0);

        // Добавление программы в OpenGL-ES
        GLES20.glUseProgram(mProgram);

        // Передача матрицы MVP в шейдер
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Передача координат вершин в шейдер
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // Включение атрибута вершин
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Передача текстурных координат в шейдер
        GLES20.glVertexAttribPointer(texCoordHandle, 2,
                GLES20.GL_FLOAT, false,
                2*4, texVertexBuffer);
        GLES20.glEnableVertexAttribArray(texCoordHandle);

        // Активация текстурного блока
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        // Рисование треугольника
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        // Отключение атрибута вершин
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    private int loadTexture(Context context, int resourceId) {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Загрузка изображения в Bitmap
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Привязка текстуры
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Установка параметров текстуры
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            // Загрузка изображения в текстуру
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Освобождение ресурсов Bitmap
            bitmap.recycle();
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }
}
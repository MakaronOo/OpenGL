package com.example.opengl;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class GLESDemo extends AppCompatActivity {

    private GLSurfaceView gLView;
    public int fig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("1");
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        assert args != null;
        fig = args.getInt("fig");

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        if(fig == 0) gLView = new TriangleSurfaceView(this);
        else gLView = new SquareSurfaceView(this);
        setContentView(gLView);
    }
}
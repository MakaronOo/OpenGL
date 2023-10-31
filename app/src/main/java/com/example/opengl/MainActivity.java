package com.example.opengl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickTriangle(View view){
        Intent intent = new Intent(this, GLESDemo.class);
        intent.putExtra("fig", 0);
        startActivity(intent);
    }
    public void onClickSquare(View view){
        Intent intent = new Intent(this, GLESDemo.class);
        intent.putExtra("fig", 1);
        startActivity(intent);
    }
}
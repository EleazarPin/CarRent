package com.example.carrent.carrent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class MainActivity extends AppCompatActivity {

    private Button btnComenzar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnComenzar = (Button)findViewById(R.id.btnComenzar);
        btnComenzar.setOnClickListener(botonListener);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
    }

    View.OnClickListener botonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;

            if(v.getId()==R.id.btnComenzar) {
                intent=new Intent(MainActivity.this, NavigationDrawer.class);
                startActivity(intent);
            }
            else
                Toast.makeText(getApplicationContext(),"Error en Listener de botones",Toast.LENGTH_LONG).show();

        }
    };
}

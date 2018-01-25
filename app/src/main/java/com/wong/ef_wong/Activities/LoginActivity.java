package com.wong.ef_wong.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wong.ef_wong.Controllers.ConnectionController;
import com.wong.ef_wong.R;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        (findViewById(R.id.btnToRegistrar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        Picasso.with(this).load(R.drawable.logo).resize(800,800).centerInside().into((ImageView)findViewById(R.id.loginBackground));
    }

    public void logIn(View view) {
        final String usuario = ((EditText) findViewById(R.id.etUsuario)).getText().toString();
        final String password = ((EditText) findViewById(R.id.etPassword)).getText().toString();
        final Handler handler = new Handler();
        final Runnable exceptionLogin = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, "Error en login", Toast.LENGTH_SHORT).show();
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    String datarow = new ConnectionController().getDATA("/Usuarios/" + usuario + ".json");
                    if (!datarow.equals("null")) {
                        JSONObject data = new JSONObject(datarow);
                        if (password.equals(data.getString("password"))) {
                            Bundle bundle = new Bundle();
                            bundle.putString("usuario", usuario);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtras(bundle);
                            System.out.println(bundle.toString());
                            finish();
                            startActivity(intent);
                        } else {
                            handler.post(exceptionLogin);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(exceptionLogin);
                }
            }
        }.start();

    }
}

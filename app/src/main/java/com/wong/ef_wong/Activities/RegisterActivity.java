package com.wong.ef_wong.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wong.ef_wong.Controllers.PhotoController;
import com.wong.ef_wong.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private ImageView iviFoto;
    private Button btnGuardar;
    private DatabaseReference root;
    private PhotoController fotoControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        iviFoto = (ImageView) findViewById(R.id.iviFoto);
        btnGuardar = (Button) findViewById(R.id.btnRegistrar);
        fotoControl = new PhotoController(this, iviFoto);
        root = FirebaseDatabase.getInstance().getReference().child("Usuarios");

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass1 = ((EditText) findViewById(R.id.etPassword1)).getText().toString();
                String pass2 = ((EditText) findViewById(R.id.etPassword2)).getText().toString();
                String nombUsuario = ((EditText) findViewById(R.id.etUsuario1)).getText().toString();

                if (!nombUsuario.isEmpty() && !pass1.isEmpty() && pass1.equals(pass2)) {
                    DatabaseReference message_root = root.child(nombUsuario);
                    Map<String, Object> map = new HashMap<String, Object>();
                    if (fotoControl.getFotoURL() != null) {
                        map.put("foto", fotoControl.getFotoURL());
                    }
                    map.put("password", pass1);
                    message_root.updateChildren(map);
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    finish();
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this, "Campos Incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Toast.makeText(RegisterActivity.this, "Subiendo la foto", Toast.LENGTH_SHORT).show();
            fotoControl.resizePhoto();
            btnGuardar.setEnabled(false);
            final Handler handler = new Handler();
            new Thread() {
                @Override
                public void run() {
                    fotoControl.uploadPhoto();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            btnGuardar.setEnabled(true);
                        }
                    });
                }
            }.start();
        }
    }

    public void tomarFotoOnClick(View view) {
        fotoControl.getPhoto();
    }

}

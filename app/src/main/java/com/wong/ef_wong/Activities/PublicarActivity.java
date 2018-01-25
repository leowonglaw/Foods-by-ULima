package com.wong.ef_wong.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wong.ef_wong.Controllers.GPSController;
import com.wong.ef_wong.Controllers.PhotoController;
import com.wong.ef_wong.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublicarActivity extends AppCompatActivity {


    private static final int REQUEST_IMAGE_CAPTURE = 100;

    private Menu menu;
    private DatabaseReference root;
    private Spinner spinner;
    private String[] contenido;
    private ArrayAdapter<String> adaptador;
    private EditText nombProducto, stock, descProducto, valor;
    private ImageView fotoProducto;
    private PhotoController photoController;
    private GPSController gpsController;
    public final boolean[] bool = {false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);


        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("Publicar");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_return);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        gpsController = new GPSController(this);

        nombProducto = (EditText) findViewById(R.id.nombProducto);
        stock = (EditText) findViewById(R.id.stock);
        descProducto = (EditText) findViewById(R.id.descProducto);
        fotoProducto = (ImageView) findViewById(R.id.fotoProducto);
        valor = (EditText) findViewById(R.id.valor);
        spinner = (Spinner) findViewById(R.id.spinner);
        contenido = new String[]{"Menú", "Sandwiches", "Postres", "Ensaladas", "Otros"};

        photoController = new PhotoController(this, fotoProducto);
        adaptador = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, contenido);
        spinner.setAdapter(adaptador);

        root = FirebaseDatabase.getInstance().getReference().child("Publicaciones");


    }
    private boolean validar() {
        if (!nombProducto.getText().toString().isEmpty() &&
                !stock.getText().toString().isEmpty() &&
                !descProducto.getText().toString().isEmpty() &&
                photoController.getFotoURL()!=null &&
                !photoController.getFotoURL().isEmpty() &&
                gpsController.getLocation()!=null &&
                bool[0]) {
            return true;
        } else {
            if(gpsController.getLocation()!=null && !bool[0]){
                validarUbicacion();
            }
            return false;
        }
    }

    private boolean contains(LatLng point, List<LatLng> points) {
        boolean result = false;
        for (int i = 0, j = points.size() - 1; i < points.size(); j = i++) {
            if ((points.get(i).longitude > point.longitude) != (points.get(j).longitude > point.longitude) &&
                    (point.latitude < (points.get(j).latitude - points.get(i).latitude) * (point.longitude - points.get(i).longitude) /
                            (points.get(j).longitude-points.get(i).longitude) + points.get(i).latitude)) {
                result = !result;
            }
        }
        return result;
    }

    public void validarUbicacion() {
        List<LatLng> points = new ArrayList<>();
        points.add(new LatLng(-12.084056, -76.973202));
        points.add(new LatLng(-12.084838, -76.973072));
        points.add(new LatLng(-12.084765, -76.972455));
        points.add(new LatLng(-12.084645, -76.972409));
        points.add(new LatLng(-12.084732, -76.972138));
        points.add(new LatLng(-12.085316, -76.972378));
        points.add(new LatLng(-12.086415, -76.970739));
        points.add(new LatLng(-12.085198, -76.969393));
        points.add(new LatLng(-12.084057, -76.969951));
        points.add(new LatLng(-12.083669, -76.970412));
        points.add(new LatLng(-12.084062, -76.973196));

        LatLng point = new LatLng(gpsController.getLocation().getLatitude(),gpsController.getLocation().getLongitude());

        if(!contains(point,points)) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Usted no esta en la Universidad de Lima. ¿Desea publicar de todos modos?");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Sí",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            bool[0] =true;
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            bool[0] =false;
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }else{
            bool[0] =true;
        }
    }



    public void tomarFoto(View view) {
        photoController.getPhoto();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Toast.makeText(PublicarActivity.this, "Subiendo la foto", Toast.LENGTH_SHORT).show();
            photoController.resizePhoto();
            ((MenuItem) menu.findItem(R.id.idPublicar)).setEnabled(false);
            final Handler handler = new Handler();
            new Thread() {
                @Override
                public void run() {
                    photoController.uploadPhoto();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ((MenuItem) menu.findItem(R.id.idPublicar)).setEnabled(true);
                        }
                    });
                }
            }.start();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case RESULT_OK: {
                if (!gpsController.mApiClient.isConnecting() && !gpsController.mApiClient.isConnected()) {
                    gpsController.mApiClient.connect();
                }else {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        gpsController.startLocationUpdates();
                    }
                }
            }
        }
    }

    @Override
    protected void onStart() {
        gpsController.mApiClient.connect();
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        gpsController.mApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(gpsController.mApiClient.isConnected())
            gpsController.startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gpsController.stopLocationUpdates();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publicar_bar, menu);
        this.menu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            finish();
        }else if(item.getItemId() == R.id.idPublicar){
            if (validar()) {
                DatabaseReference message_root = root.child(String.valueOf(new Date().getTime()));
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("titulo", nombProducto.getText().toString());
                map.put("descripcion", descProducto.getText().toString());
                map.put("foto", photoController.getFotoURL());
                if(getIntent().getExtras()!=null){
                    map.put("usuario",getIntent().getExtras().getString("usuario"));
                }else{
                    map.put("usuario", "Anonimous");
                }
                map.put("categoria", spinner.getSelectedItem());
                map.put("stock", Integer.parseInt(stock.getText().toString()));
                map.put("precio", Double.parseDouble(valor.getText().toString()));
                map.put("ubicacion", "Universidad de Lima");
                map.put("latitud", gpsController.getLocation().getLatitude());
                map.put("longitud", gpsController.getLocation().getLongitude());
                message_root.updateChildren(map);
                Intent intent = new Intent(PublicarActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
            } else {
                Toast.makeText(PublicarActivity.this, "Campos Incorrectos", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "¡Eso no me lo esperaba!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}

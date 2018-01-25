package com.wong.ef_wong.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.wong.ef_wong.Controllers.ConnectionController;
import com.wong.ef_wong.R;
import com.wong.ef_wong.beans.Publicacion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PubMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {

    private HashMap<String, String> hashMap;
    private HashMap<Long, Publicacion> arraySelect;
    private DatabaseReference root;
    private long actualTime;
    private Handler handler;
    private MapView mMapView;

    private long selected;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pub_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        actualTime = new Date().getTime();
        handler = new Handler();
        hashMap = new HashMap<>();
        arraySelect = new HashMap<>();
        root = FirebaseDatabase.getInstance().getReference().child("Publicaciones");
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);

    }

    public void setSelected(long key) {
        selected=key;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        final PolygonOptions polygonOptions = new PolygonOptions().add(
                new LatLng(-12.084056, -76.973202),
                new LatLng(-12.084838, -76.973072),
                new LatLng(-12.084765, -76.972455),
                new LatLng(-12.084645, -76.972409),
                new LatLng(-12.084732, -76.972138),
                new LatLng(-12.085316, -76.972378),
                new LatLng(-12.086415, -76.970739),
                new LatLng(-12.085198, -76.969393),
                new LatLng(-12.084057, -76.969951),
                new LatLng(-12.083669, -76.970412),
                new LatLng(-12.084062, -76.973196))
                .strokeColor(Color.RED)
                .fillColor(Color.argb(50,178,153,255));

        final LatLng ubicacionZoom = new LatLng(-12.084753, -76.970833);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionZoom, 18.0f));

        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot data = (DataSnapshot) iterator.next();
                    final Publicacion publicacion = (data).getValue(Publicacion.class);
                    publicacion.setKey(Long.parseLong(data.getKey()));
                    new Thread() {
                        @Override
                        public void run() {
                            String datarow;
                            try {
                                datarow = new ConnectionController().getDATA("/Usuarios/" + publicacion.getUsuario() + "/foto.json");
                                hashMap.put(publicacion.getUsuario(), datarow.replaceAll("\"", ""));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            arraySelect.put(publicacion.getKey(), publicacion);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    LatLng ubicacion = new LatLng(publicacion.getLatitud(), publicacion.getLongitud());

                                    Marker marker = googleMap.addMarker(new MarkerOptions()
                                            .position(ubicacion)
                                            .title(publicacion.getTitulo()).snippet(String.valueOf(publicacion.getKey())));
                                    if(selected==publicacion.getKey()){
                                        marker.showInfoWindow();
                                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 18.0f));
                                    }
                                    System.out.println(contains(ubicacion, polygonOptions.getPoints()));
                                }
                            });

                        }
                    }.start();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                System.out.println(marker.getTitle());
            }
        });
        googleMap.setInfoWindowAdapter(this);
        googleMap.addPolygon(polygonOptions);

    }
    public boolean contains(LatLng point,List<LatLng> points) {
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


    @Override
    public View getInfoWindow(Marker marker) {
        Publicacion publicacion = arraySelect.get(Long.valueOf(marker.getSnippet()));
        if (publicacion != null) {
            return publicacion.getView(getActivity(), null);
        } else {
            System.out.println("publicacion es null value del sinpet: " + marker.getSnippet());
            return null;
        }
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}

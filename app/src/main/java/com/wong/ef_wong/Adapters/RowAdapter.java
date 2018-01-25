package com.wong.ef_wong.Adapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wong.ef_wong.Controllers.ConnectionController;
import com.wong.ef_wong.R;
import com.wong.ef_wong.beans.Publicacion;

import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Angel on 28/11/2016.
 */

public class RowAdapter extends BaseAdapter {

    private ArrayList<Publicacion> arraySelect;
    private Context mContext;
    private DatabaseReference root;
    private long actualTime;
    private Handler handler;

    public RowAdapter(Context context) {
        handler = new Handler();
        arraySelect = new ArrayList<>();
        actualTime = new Date().getTime();
        root = FirebaseDatabase.getInstance().getReference().child("Publicaciones");
        mContext = context;
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
                                publicacion.setFotoUsuario(datarow.replaceAll("\"", ""));
                                arraySelect.add(publicacion);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    notifyDataSetChanged();
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
    }

    @Override
    public int getCount() {
        return arraySelect.size();
    }

    @Override
    public Object getItem(int position) {
        return arraySelect.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Publicacion publicacion = arraySelect.get(position);
        System.out.println(publicacion);
        return publicacion.getView(mContext,parent);
    }

}

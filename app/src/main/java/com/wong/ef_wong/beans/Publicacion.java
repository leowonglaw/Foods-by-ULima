package com.wong.ef_wong.beans;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.wong.ef_wong.R;

import java.util.Date;
import java.util.HashMap;
import com.squareup.picasso.Transformation;
/**
 * Created by Leo on 26/11/2016.
 */

public class Publicacion {


    private View view;
    private long key;
    private String usuario;
    private String foto;
    private String titulo;
    private String descripcion;
    private String categoria;
    private int stock;
    private float precio;
    private double latitud;
    private double longitud;
    private String fotoUsuario;

    public Publicacion() {
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    @Override
    public String toString() {
        return "Publicacion{" +
                "key=" + key +
                ", usuario='" + usuario + '\'' +
                ", foto='" + foto + '\'' +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", categoria='" + categoria + '\'' +
                ", stock=" + stock +
                ", precio=" + precio +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                '}';
    }

    public View getView(Context mContext, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.row_item, parent, false);
        final TextView txtUsuario = (TextView) view.findViewById(R.id.txtUsuario),
                txtHora = (TextView) view.findViewById(R.id.txtHora),
                txtDescripcion = (TextView) view.findViewById(R.id.txtDescProducto),
                txtNombProducto = (TextView) view.findViewById(R.id.txtNombProducto),
                txtPrecioProducto = (TextView) view.findViewById(R.id.txtPrecioProducto),
                txtTipoProd = (TextView) view.findViewById(R.id.tipoProducto),
                txtStock = (TextView) view.findViewById(R.id.txtStock),
                txtComentarios = (TextView) view.findViewById(R.id.txtCantComentarios);
        ImageView imgUsuario = (ImageView) view.findViewById(R.id.imgUsuario),
                imgProducto = (ImageView) view.findViewById(R.id.imgProducto);

        txtUsuario.setText(getUsuario());
        long dif = new Date().getTime() - key;
        int time = (int) (dif / (1000 * 60 * 60) % 24);
        if (time >= 1) {
            txtHora.setText(time + "h");
        } else {
            time = (int) (dif / (1000 * 60) % 60);
            txtHora.setText(time + "m");
        }
        txtStock.setText(String.valueOf(getStock())+ " en stock");
        txtDescripcion.setText(descripcion);
        txtNombProducto.setText(titulo);
        txtPrecioProducto.setText("S/" + String.valueOf(getPrecio()));
        txtTipoProd.setText(categoria);
        txtComentarios.setText("0 comentarios");

        Picasso.with(mContext).load(fotoUsuario).placeholder(R.drawable.ic_user_default_pick).centerCrop().resize(150,150).transform(new CircleTransform()).into(imgUsuario);
        Picasso.with(mContext).load(foto).placeholder(R.drawable.logo).centerCrop().resize(800, 500).into(imgProducto);
        return view;
    }
    private class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}

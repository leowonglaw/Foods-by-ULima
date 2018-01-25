package com.wong.ef_wong.Controllers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.ImageView;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Leo on 03/12/2016.
 */

public class PhotoController {

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private String mCurrentPhotoPath;
    private ImageView iviFoto;
    private String fotoURL;
    private Activity activity;

    public PhotoController getFotoControler() {
        return this;
    }

    public String getFotoURL(){
        return fotoURL;
    }

    public PhotoController(Activity activity, ImageView iviFoto) {
        this.activity = activity;
        this.iviFoto = iviFoto;
    }

    public void uploadPhoto(){
        Map config = new HashMap<String, String>();
        config.put("cloud_name","efwong");
        config.put("api_key", "362296822861378");
        config.put("api_secret", "6ESIl1_xHx60OOsfXzIQasfJnxM");
        Cloudinary cloudinary = new Cloudinary(config);
        File photoFile = new File(mCurrentPhotoPath);
        try {
            FileInputStream is = new FileInputStream(photoFile);
            String timestamp = String.valueOf(new Date().getTime());
            try {
                cloudinary.uploader().upload(is, ObjectUtils.asMap("public_id", timestamp));
                fotoURL = cloudinary.url().generate(timestamp);
                System.out.println("PhotoController fotoURL: " + fotoURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void resizePhoto() {
        // Obtener las dimensiones del ImageView
        int targetW = iviFoto.getWidth();
        int targetH = iviFoto.getHeight();
        // Obtener las dimensiones del bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // Determinamos cuanto debemos escalar la imagen
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        // Decodificamos el archivo imagen en un bitmap que pueda caber en el ImageView
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true; // <= KITKAT
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        iviFoto.setImageBitmap(bitmap);
    }

    public void getPhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            // SI hay app que reacciona a intent
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                System.out.println("Error al crear archivo");
            }
            if (photoFile != null){
                Uri uri = FileProvider.getUriForFile(
                        activity,
                        "com.wong.fileprovider",
                        photoFile
                );
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                activity.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreArchivo = "JPEG_" + timestamp + "_";
        File directorio = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(nombreArchivo, ".jpg", directorio);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


}

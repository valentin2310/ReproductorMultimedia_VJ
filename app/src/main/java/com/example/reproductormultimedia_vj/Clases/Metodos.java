package com.example.reproductormultimedia_vj.Clases;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class Metodos {

    public static final String FILTRO_USUARIO = "^[A-Za-zÑñ_0-9]{3,23}$";
    public static final String FILTRO_NOMBRE = "^[A-Za-zÑñ]{3}[A-Za-zÑñ ]{0,20}$";
    public static final String FILTRO_FECHA = "^[\\d]{2}/[\\d]{2}/[\\d]{4}$";

    public static Bitmap convertByteArrayToBitmap(byte[] byteArrayToBeCOnvertedIntoBitMap) {
        Bitmap bitMapImage = BitmapFactory.decodeByteArray(
                byteArrayToBeCOnvertedIntoBitMap, 0,
                byteArrayToBeCOnvertedIntoBitMap.length);
        return bitMapImage;
    }
    public static byte[] convertBitmapToByteArray(ImageView imgV){
        Bitmap bit = ((BitmapDrawable) imgV.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
    public static byte[] convertBitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
    public static boolean validarCampo(String filtro, String text){
        return Pattern.matches(filtro, text);
    }
    public static Date obtenerDate(String fecha){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try{
            Date date = format.parse(fecha);
            if(new Date().before(date)) return null;
            return date;
        } catch (ParseException e) {
            return null;
        }
    }
}

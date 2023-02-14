package com.example.reproductormultimedia_vj.Clases;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.reproductormultimedia_vj.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.regex.Pattern;

public class Metodos {

    public static final String FILTRO_USUARIO = "^[A-Za-zÑñ_0-9\\-]{8,23}$";
    public static final String FILTRO_NOMBRE = "^[A-Za-zÑñ]{3}[A-Za-zÑñ ]{0,20}$";
    public static final String FILTRO_FECHA = "^[\\d]{2}/[\\d]{2}/[\\d]{4}$";

    public static final int ANIMACION_LIKE = R.raw.heart_like;
    public static final int ANIMACION_ADD = R.raw.add_new;

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
            if(new Date().getYear()-16 <= date.getYear()) return null;
            return date;
        } catch (ParseException e) {
            return null;
        }
    }

    public static int getDominantColor(Bitmap bitmap) {
        if (null == bitmap) return Color.TRANSPARENT;

        int redBucket    = 0;
        int greenBucket  = 0;
        int blueBucket   = 0;
        int alphaBucket  = 0;

        boolean hasAlpha = bitmap.hasAlpha();
        int pixelCount   = bitmap.getWidth() * bitmap.getHeight();
        int[] pixels     = new int[pixelCount];

        bitmap.getPixels(
                pixels,
                0,
                bitmap.getWidth(),
                0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight()
        );

        for (int y = 0, h = bitmap.getHeight(); y < h; y++){
            for (int x = 0, w = bitmap.getWidth(); x < w; x++){
                int color   =  pixels[x + y * w];            // x + y * width
                redBucket   += (color >> 16) & 0xFF;         // Color.red
                greenBucket += (color >> 8) & 0xFF;          // Color.greed
                blueBucket  += (color & 0xFF);               // Color.blue
                if (hasAlpha) alphaBucket += (color >>> 24); // Color.alpha
            }
        }

        return Color.argb(
                (hasAlpha) ? (alphaBucket / pixelCount) : 255,
                redBucket / pixelCount,
                greenBucket / pixelCount,
                blueBucket / pixelCount
        );
    }

    public static void setAnimation(LottieAnimationView lottie, int animacion){
        lottie.setAnimation(animacion);
    }

    public static void darLike (boolean like, LottieAnimationView lottie){
        lottie.setProgress(0.0f);
        if(!like){
            lottie.setMaxProgress(0.5f);
            lottie.playAnimation();

        }else{
            lottie.setMaxProgress(1f);
            lottie.playAnimation();
            lottie.setProgress(0.7f);
        }
    }

    public static int darker (int color, float factor) {
        int a = Color.alpha( color );
        int r = Color.red( color );
        int g = Color.green( color );
        int b = Color.blue( color );

        return Color.argb( a,
                Math.max( (int)(r * factor), 0 ),
                Math.max( (int)(g * factor), 0 ),
                Math.max( (int)(b * factor), 0 ) );
    }
}

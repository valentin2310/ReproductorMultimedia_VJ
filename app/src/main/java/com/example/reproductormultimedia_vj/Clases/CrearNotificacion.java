package com.example.reproductormultimedia_vj.Clases;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.ImageView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.reproductormultimedia_vj.R;
import com.example.reproductormultimedia_vj.Servicios.NotificationActionService;

import java.io.IOException;

public class CrearNotificacion {
    public static final String CHANNEL_ID = "channel1";

    public static final String ACTION_PREVIUOS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static Notification notification;

    public static void createNotification (Context context, Cancion cancion, int playbutton, int pos, int size) throws IOException {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);


            MediaSessionCompat mediaSessionCompat= new MediaSessionCompat( context, "tag");

            PendingIntent pendingIntentPrevious;
            int drw_previous;
            if (pos == 0) {
                pendingIntentPrevious = null;
                drw_previous = 0;
            } else  {
                Intent intentPrevious = new Intent (context, NotificationActionService.class)
                        .setAction(ACTION_PREVIUOS);
                pendingIntentPrevious
                        = PendingIntent.getBroadcast(context, 0,
                        intentPrevious, PendingIntent.FLAG_IMMUTABLE);
                drw_previous = R.drawable.ic_baseline_skip_previous_24;
        }

            Intent intentPlay= new Intent (context, NotificationActionService.class)
                    .setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay= PendingIntent.getBroadcast (context,  0,
                    intentPlay, PendingIntent.FLAG_IMMUTABLE);

            PendingIntent pendingIntentNext;
            int drw_next;
            if (pos == size) {
                pendingIntentNext = null;
                drw_next = 0;
            } else {
                    Intent intentNext = new Intent (context, NotificationActionService.class).setAction (ACTION_NEXT);
            pendingIntentNext = PendingIntent.getBroadcast (context,  0,
                    intentNext, PendingIntent.FLAG_IMMUTABLE);
            drw_next = R.drawable.ic_baseline_skip_next_24;
        }
            Bitmap icon = null;

            if (cancion.getRuta().startsWith("audio/"))
            icon = Metodos.convertByteArrayToBitmap(cancion.getPortada());
            else{
                if (!new String(cancion.getPortada()).equals("")) {
                    Uri imageUri = Uri.parse(new String(cancion.getPortada()));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        icon = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.getContentResolver(), imageUri));
                    } else {
                        icon = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                    }
                }
            }
            //create notification
            notification = new NotificationCompat.Builder (context, CHANNEL_ID)
                    .setSmallIcon (R.drawable.ic_baseline_repeat_50)
                    .setContentTitle (cancion.getTitulo())
                    .setContentText (cancion.getNombreArtista())
                    .setLargeIcon (icon)
                    .setOnlyAlertOnce (true) //show notification for only first time
                    .setShowWhen (false)
                    .addAction (drw_previous,  "Previous", pendingIntentPrevious)
                    .addAction (playbutton, "play", pendingIntentPlay)
                    .addAction (drw_next,  "Next", pendingIntentNext)
                    .setStyle (new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession (mediaSessionCompat.getSessionToken()))
                    .setPriority (NotificationCompat. PRIORITY_LOW)
                    .build();
            notificationManagerCompat.notify(1, notification);
        }
    }
}

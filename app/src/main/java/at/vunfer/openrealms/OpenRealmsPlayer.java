/* Licensed under GNU GPL v3.0 (C) 2023 */
package at.vunfer.openrealms;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;

public class OpenRealmsPlayer extends Service {
    private MediaPlayer player;
    private SharedPreferences prefs;
    private static final String PREF_NAME = "OpenRealmsPlayerPrefs";
    private static final String KEY_INGAME_POSITION = "ingame_position";
    private static final String KEY_MENU_POSITION = "menu_position";
    private static final String MENU_MUSIC_EXTRA = "menu";
    private static final String INGAME_MUSIC_EXTRA = "ingame";
    private String trackPlaying;
    private static final String TAG = OpenRealmsPlayer.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    }

    /*Royalty for the used music:
    Menu Music:
    Embers in the Wind | Copyright Free Fantasy Tavern Music
    By PGN Music
    YouTube-Link: https://youtu.be/4KGxNjARTYQ

    Ingame Music:
    Royalty Free Celtic Fantasy Music - "The Lone Wolf"
    By Royalty Free Music - Alexander Nakarada
    YouTube-Link: https://youtu.be/RN1THCKeaNsa*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        if (extras == null || (trackPlaying = extras.getString("track")) == null) {
            Log.e(TAG, "No track was selected!");
            return Service.START_STICKY;
        }
        int track;
        int position;
        if (trackPlaying.equals(MENU_MUSIC_EXTRA)) {
            track = R.raw.menu_music;
            position = prefs.getInt(KEY_MENU_POSITION, 0);
        } else {
            track = R.raw.background_music;
            position = prefs.getInt(KEY_INGAME_POSITION, 0);
        }
        player = MediaPlayer.create(this, track);
        player.setLooping(true);
        player.seekTo(position);
        player.start();

        Log.i(TAG, "Playing " + trackPlaying);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Save the current position.
        SharedPreferences.Editor editor = prefs.edit();
        if (trackPlaying.equals(MENU_MUSIC_EXTRA)) {
            editor.putInt(KEY_MENU_POSITION, player.getCurrentPosition());
        } else {
            editor.putInt(KEY_INGAME_POSITION, player.getCurrentPosition());
        }
        editor.apply();

        player.stop();
        player.release();
        player = null;
        Log.i(TAG, "Stopping " + trackPlaying);
    }

    @Nullable @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

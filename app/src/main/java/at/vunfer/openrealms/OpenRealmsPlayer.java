package at.vunfer.openrealms;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;

public class OpenRealmsPlayer extends Service {
    private MediaPlayer player;
    private SharedPreferences prefs;
    private static final String PREF_NAME = "OpenRealmsPlayerPrefs";
    private static final String KEY_POSITION = "position";

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        player = MediaPlayer.create(this, R.raw.backgound_music);
        player.setLooping(true);

        // Retrieve the saved position, if any.
        int position = prefs.getInt(KEY_POSITION, 0);
        player.seekTo(position);
        player.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (player != null) {
            // Save the current position.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_POSITION, player.getCurrentPosition());
            editor.apply();

            player.stop();
            player.release();
            player = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

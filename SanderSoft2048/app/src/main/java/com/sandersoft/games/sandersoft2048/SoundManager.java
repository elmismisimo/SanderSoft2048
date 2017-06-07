package com.sandersoft.games.sandersoft2048;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

import com.sandersoft.games.sandersoft2048.utils.Globals;

/**
 * Created by Sander on 22/05/2017.
 */

public class SoundManager {

    public static final float VOLUME = 1; 
    static MediaPlayer mp_menu_in;
    static MediaPlayer mp_menu_out;
    static MediaPlayer mp_success;
    static MediaPlayer mp_error;
    static MediaPlayer mp_open;
    static MediaPlayer mp_click;
    static MediaPlayer mp_click2;

    public static void prepareMedia(Context context){
        try {
            if (mp_menu_in == null)
                mp_menu_in = MediaPlayer.create(context, R.raw.menu_in);
            if (mp_menu_out == null)
                mp_menu_out = MediaPlayer.create(context, R.raw.menu_out);
            if (mp_success == null)
                mp_success = MediaPlayer.create(context, R.raw.success);
            if (mp_error == null)
                mp_error = MediaPlayer.create(context, R.raw.error);
            if (mp_open == null)
                mp_open = MediaPlayer.create(context, R.raw.open);
            if (mp_click == null)
                mp_click = MediaPlayer.create(context, R.raw.click);
            if (mp_click2 == null)
                mp_click2 = MediaPlayer.create(context, R.raw.click2);

        } catch (Exception ex){}
    }
    public static void playMenuIn(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean(Globals.SOUND, true)) {
            mp_menu_in.seekTo(0);
            mp_menu_in.start();
        }
    }
    public static void playMenuOut(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean(Globals.SOUND, true) && mp_menu_out != null) {
            mp_menu_out.seekTo(0);
            mp_menu_out.start();
        }
    }
    public static void playSuccess(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean(Globals.SOUND, true) && mp_success != null) {
            mp_success.seekTo(0);
            mp_success.start();
        }
    }
    public static void playError(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean(Globals.SOUND, true) && mp_error != null) {
            mp_error.seekTo(0);
            mp_error.start();
        }
    }
    public static void playOpen(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean(Globals.SOUND, true) && mp_open != null) {
            mp_open.seekTo(0);
            mp_open.start();
        }
    }
    public static void playClick(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean(Globals.SOUND, true) && mp_click != null) {
            mp_click.seekTo(0);
            mp_click.start();
        }
    }
    public static void playClick2(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean(Globals.SOUND, true) && mp_click2 != null) {
            mp_click2.seekTo(0);
            mp_click2.start();
        }
    }
}

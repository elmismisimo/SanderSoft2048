package com.sandersoft.games.sandersoft2048.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.sandersoft.games.sandersoft2048.R;
import com.sandersoft.games.sandersoft2048.SoundManager;
import com.sandersoft.games.sandersoft2048.controllers.BoardController;
import com.sandersoft.games.sandersoft2048.utils.Globals;

/**
 * Created by Sander on 22/05/2017.
 */

public class FragmentMain extends Fragment {

    View rootview;
    LinearLayout lay_main_layout;
    ImageView btn_main_google;
    ViewFlipper sld_main;
    TextView lbl_title;
    TextView lbl_subtitle;
    Button btn_play;
    Button btn_settings;
    ImageView img_sandersoft;

    LinearLayout lay_settings;
    TextView lbl_title_settings;
    Button btn_sound;
    Button btn_google;
    Button btn_theme;

    LinearLayout lay_about;
    ImageView img_logo_about;
    TextView lbl_sandersoft;
    TextView lbl_created;
    TextView lbl_alexander;
    TextView lbl_version_title;
    TextView lbl_version;
    Button btn_support;
    Button btn_rate;

    LinearLayout lay_google;
    TextView lbl_title_google;
    TextView lbl_subtitle_google;
    Button btn_google_conn;
    Button btn_leaderboards;
    Button btn_achievements;

    int pos = 0;
    boolean light;

    public FragmentMain(){}
    public static FragmentMain getInstance(int pos){
        FragmentMain frag = new FragmentMain();
        frag.pos = pos;
        //if (!ApplicationMineSweeper.getGameManager().isConnectedGooglePlay())
        //    ApplicationMineSweeper.getGameManager().connectGP();
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_main, container, false);

        if (null != savedInstanceState){
            pos = savedInstanceState.getInt(Globals.MAIN_POS);
        }

        //instantiates the views of the layout
        defineElements();

        return rootview;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //get objects backedup so we can reload the activity with the same infromation
        outState.putInt(Globals.MAIN_POS, pos);
    }

    public void defineElements(){
        lay_main_layout = (LinearLayout) rootview.findViewById(R.id.lay_main_layout);
        btn_main_google = (ImageView) rootview.findViewById(R.id.btn_main_google);
        sld_main = (ViewFlipper) rootview.findViewById(R.id.sld_main);
        lbl_title = (TextView) rootview.findViewById(R.id.lbl_title);
        lbl_subtitle = (TextView) rootview.findViewById(R.id.lbl_subtitle);
        btn_play = (Button) rootview.findViewById(R.id.btn_play);
        btn_settings = (Button) rootview.findViewById(R.id.btn_settings);
        img_sandersoft = (ImageView) rootview.findViewById(R.id.img_sandersoft);

        lay_settings = (LinearLayout) rootview.findViewById(R.id.lay_settings);
        lbl_title_settings = (TextView) rootview.findViewById(R.id.lbl_title_settings);
        btn_sound = (Button) rootview.findViewById(R.id.btn_sound);
        btn_google = (Button) rootview.findViewById(R.id.btn_google);
        btn_theme = (Button) rootview.findViewById(R.id.btn_theme);

        lay_about = (LinearLayout) rootview.findViewById(R.id.lay_about);
        img_logo_about = (ImageView) rootview.findViewById(R.id.img_logo_about);
        lbl_sandersoft = (TextView) rootview.findViewById(R.id.lbl_sandersoft);
        lbl_created = (TextView) rootview.findViewById(R.id.lbl_created);
        lbl_alexander = (TextView) rootview.findViewById(R.id.lbl_alexander);
        lbl_version_title = (TextView) rootview.findViewById(R.id.lbl_version_title);
        lbl_version = (TextView) rootview.findViewById(R.id.lbl_version);
        btn_support = (Button) rootview.findViewById(R.id.btn_support);
        btn_rate = (Button) rootview.findViewById(R.id.btn_rate);

        lay_google = (LinearLayout) rootview.findViewById(R.id.lay_google);
        lbl_title_google = (TextView) rootview.findViewById(R.id.lbl_title_google);
        lbl_subtitle_google = (TextView) rootview.findViewById(R.id.lbl_subtitle_google);
        btn_google_conn = (Button) rootview.findViewById(R.id.btn_google_conn);
        btn_leaderboards = (Button) rootview.findViewById(R.id.btn_leaderboards);
        btn_achievements = (Button) rootview.findViewById(R.id.btn_achievements);

        updateSoundButton();
        updateThemeButton();
        updateGoogleButtons();
        lbl_version.setText(getVersion());
        sld_main.setDisplayedChild(pos);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //verify if there is light theme
        setTheme();

        //set listeners
        btn_main_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToGoogle();
                SoundManager.playMenuIn(getActivity());
            }
        });
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play();
            }
        });
        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToSettings();
                SoundManager.playMenuIn(getActivity());
            }
        });
        btn_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSettingSound();
                updateSoundButton();
                SoundManager.playMenuIn(getActivity());
            }
        });
        btn_theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSettingTheme();
                updateThemeButton();
                SoundManager.playMenuIn(getActivity());
                ((ActivityMain)getActivity()).getGameManager().unlockAchievement(R.string.achievement_oh_my_eyes);
            }
        });
        img_sandersoft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToAbout();
                SoundManager.playMenuIn(getActivity());
                ((ActivityMain)getActivity()).getGameManager().unlockAchievement(R.string.achievement_who_is_that_guy);
            }
        });
        btn_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogSupport();
            }
        });
        btn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRate();
            }
        });
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToGoogle();
                SoundManager.playMenuIn(getActivity());
            }
        });
        btn_google_conn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.edit().putBoolean(Globals.GAMES_CONNECT, !preferences.getBoolean(Globals.GAMES_CONNECT, true)).apply();
                updateGoogleButtons();
                if (preferences.getBoolean(Globals.GAMES_CONNECT, true))
                    ((ActivityMain)getActivity()).getGameManager().connectGP();
                else
                    ((ActivityMain)getActivity()).getGameManager().disconnectGP();
                SoundManager.playMenuIn(getActivity());
            }
        });
        btn_leaderboards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActivityMain)getActivity()).getGameManager().seeScoreBoard(getActivity());
            }
        });
        btn_achievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActivityMain)getActivity()).getGameManager().seeAchievements(getActivity());
            }
        });
    }
    public void setTheme(){
        light = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(Globals.THEME_LIGHT, false);
        //layout_main_menu
        lay_main_layout.setBackgroundResource(light ? R.color.color_LBackground : R.color.color_Background);
        btn_main_google.setBackgroundResource(light ? R.drawable.btn_l : R.drawable.btn);
        updateGoogleButtons();
        lbl_title.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        lbl_subtitle.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_play.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_play.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_settings.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_settings.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        img_sandersoft.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        img_sandersoft.setImageResource(light ? R.mipmap.logo_sander_soft_blk : R.mipmap.logo_sander_soft);
        //layout_settings
        lay_settings.setBackgroundResource(light ? R.color.color_LBackground : R.color.color_Background);
        lbl_title_settings.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_sound.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_sound.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_google.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_google.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_theme.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_theme.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        //layout_about
        lay_about.setBackgroundResource(light ? R.color.color_LBackground : R.color.color_Background);
        img_logo_about.setImageResource(light ? R.mipmap.logo_sander_soft_blk : R.mipmap.logo_sander_soft);
        lbl_sandersoft.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        lbl_created.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        lbl_alexander.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        lbl_version_title.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        lbl_version.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_support.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_support.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_rate.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_rate.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        //layout_google
        lay_google.setBackgroundResource(light ? R.color.color_LBackground : R.color.color_Background);
        lbl_title_google.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        lbl_subtitle_google.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_google_conn.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_google_conn.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_leaderboards.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_leaderboards.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_achievements.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_achievements.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
    }
    public void updateSoundButton(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sound = getString(R.string.sound) + " " + getString(preferences.getBoolean(Globals.SOUND, true) ? R.string.on : R.string.off);
        btn_sound.setText(sound);
    }
    public void updateThemeButton(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        btn_theme.setText(preferences.getBoolean(Globals.THEME_LIGHT, false) ? R.string.light : R.string.dark);
    }
    public void updateGoogleButtons(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean google_on = preferences.getBoolean(Globals.GAMES_CONNECT, true);
        String google_state = getString(R.string.google_play) + " " +
                getString(google_on
                        ? (((ActivityMain)getActivity()).getGameManager().isConnectedGooglePlay() ? R.string.connected : R.string.on)
                        : R.string.disconnected);
        btn_google_conn.setText(google_state);
        btn_main_google.setImageResource(light ?
                (google_on ? R.mipmap.google : R.mipmap.google_d) :
                (google_on ? R.mipmap.google_w : R.mipmap.google_w_d));
        btn_leaderboards.setEnabled(google_on && ((ActivityMain)getActivity()).getGameManager().isConnectedGooglePlay());
        btn_achievements.setEnabled(google_on && ((ActivityMain)getActivity()).getGameManager().isConnectedGooglePlay());
    }

    public void changeSettingSound(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.edit().putBoolean(Globals.SOUND, !preferences.getBoolean(Globals.SOUND, true)).apply();
    }
    public void changeSettingTheme(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.edit().putBoolean(Globals.THEME_LIGHT, !preferences.getBoolean(Globals.THEME_LIGHT, false)).apply();
        setTheme();
        //getActivity().recreate();
    }

    public void moveToMain(){
        if (pos == 0) return;
        if (pos > 0) {
            sld_main.setInAnimation(getActivity(), R.anim.animation_in_right);
            sld_main.setOutAnimation(getActivity(), R.anim.animation_out_right);
        } else {
            sld_main.setInAnimation(getActivity(), R.anim.animation_in_left);
            sld_main.setOutAnimation(getActivity(), R.anim.animation_out_left);
        }
        pos = 0;
        sld_main.setDisplayedChild(pos);
    }
    public void moveToSettings(){
        if (pos == 1) return;
        if (pos > 1) {
            sld_main.setInAnimation(getActivity(), R.anim.animation_in_right);
            sld_main.setOutAnimation(getActivity(), R.anim.animation_out_right);
        } else {
            sld_main.setInAnimation(getActivity(), R.anim.animation_in_left);
            sld_main.setOutAnimation(getActivity(), R.anim.animation_out_left);
        }
        pos = 1;
        sld_main.setDisplayedChild(pos);
    }
    public void moveToAbout(){
        if (pos == 2) return;
        if (pos > 2) {
            sld_main.setInAnimation(getActivity(), R.anim.animation_in_right);
            sld_main.setOutAnimation(getActivity(), R.anim.animation_out_right);
        } else {
            sld_main.setInAnimation(getActivity(), R.anim.animation_in_left);
            sld_main.setOutAnimation(getActivity(), R.anim.animation_out_left);
        }
        pos = 2;
        sld_main.setDisplayedChild(pos);
    }
    public void moveToGoogle(){
        if (pos == 3) return;
        if (pos > 3) {
            sld_main.setInAnimation(getActivity(), R.anim.animation_in_right);
            sld_main.setOutAnimation(getActivity(), R.anim.animation_out_right);
        } else {
            sld_main.setInAnimation(getActivity(), R.anim.animation_in_left);
            sld_main.setOutAnimation(getActivity(), R.anim.animation_out_left);
        }
        pos = 3;
        sld_main.setDisplayedChild(pos);
    }

    public boolean onBackPressed(){
        SoundManager.playMenuOut(getActivity());
        if (pos == 0)
            return false;
        else if (pos == 1 || pos == 2 || pos == 3)
            moveToMain();
        return true;
    }

    public void play(){
        ((ActivityMain)getActivity()).changeToGame(false);

        SoundManager.playOpen(getActivity());
    }

    public String getVersion() {
        try
        {
            return getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        }
        catch(Exception ex)
        {
            return "1.0.0";
        }
    }

    public void openRate(){
        final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
        try {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (android.content.ActivityNotFoundException anfe) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }

    //publicidad
    public InterstitialAd interstitialAd;

    /**
     * Abrimos el dialogo de apoyo
     */
    public void openDialogSupport(){
        //publicidad
        setNewInterstitialRequest();

        AlertDialog.Builder supportDialog = new AlertDialog.Builder(getActivity());
        supportDialog.setTitle(R.string.menu_support);
        supportDialog.setMessage(R.string.support_desc);
        supportDialog.setPositiveButton("...", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                //publicidad
                displayInterstitial();
            }
        });
        supportDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        final AlertDialog dialog = supportDialog.create();
        dialog.show();

        ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setText(R.string.support_btn);
            }
        });

    }
    /**
     * Carga un nuevo anuncio para el itersticial
     */
    public void setNewInterstitialRequest(){
        getActivity().runOnUiThread(new Runnable() {
            public void run()
            {
                try {
                    interstitialAd = new InterstitialAd(getActivity());
                    interstitialAd.setAdUnitId("ca-app-pub-5066758545463777/4245721848");

                    // Create ad request.
                    AdRequest adRequest = new AdRequest.Builder()
                            .addTestDevice("1B3FD01EDB6D8258FA8A938E6C654D33")
                            .build();

                    // Begin loading your interstitial.
                    interstitialAd.loadAd(adRequest);
                }
                catch (Exception ex){
                    String ext = ex.toString();
                }
            }
        });
    }
    /**
     *  Invoke displayInterstitial() when you are ready to display an interstitial.
     */
    public void displayInterstitial() {
        getActivity().runOnUiThread(new Runnable() {
            public void run()
            {
                try {
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    }
                    //else
                    //	showToast("Not loaded");
                }
                catch (Exception ex){}

            }
        });
    }




}

package com.sandersoft.games.sandersoft2048.views;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sandersoft.games.sandersoft2048.GPManager;
import com.sandersoft.games.sandersoft2048.R;
import com.sandersoft.games.sandersoft2048.controllers.BoardController;
import com.sandersoft.games.sandersoft2048.utils.Globals;

public class ActivityMain extends AppCompatActivity {

    FragmentMain fragmentMain;
    FragmentGame fragmentGame;
    public static ActivityMain This;
    boolean inGame = false;

    public GPManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameManager = new GPManager(this);

        This = this;

        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(Globals.THEME_LIGHT, false)) {
            setTheme(R.style.AppTheme_Light_NoActionBar);
            /*if (Build.VERSION.SDK_INT >= 21) {
                int background = getResources().getColor(R.color.color_LBackground);
                getWindow().setNavigationBarColor(background);
                getWindow().setStatusBarColor(background);
            }*/
        }

        if (null == savedInstanceState) {
            //verify if a game was held pending
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String savedBoard = preferences.getString(Globals.GAME_BOARD, "");
            if (savedBoard.equals("")) {
                //place the main fragment in the container
                changeToMenu(0);
            } else {
                //place the game fragment in the container
                changeToGame(true);
            }
        } else {
            //recover the values after a destroy
            inGame = savedInstanceState.getBoolean("inGame");
            if (!inGame)
                fragmentMain = (FragmentMain) getFragmentManager().findFragmentByTag(Globals.MAIN_FRAGMENT);
            else
                fragmentGame = (FragmentGame) getFragmentManager().findFragmentByTag(Globals.GAME_FRAGMENT);
        }

        /*if (savedInstanceState == null){
            fragmentGame = FragmentGame.getInstance();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_fragment, fragmentGame, Globals.GAME_FRAGMENT);
            ft.commit();
        } else {
            fragmentGame = (FragmentGame) getFragmentManager().findFragmentByTag(Globals.GAME_FRAGMENT);
        }*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("inGame", inGame);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Globals.GAMES_CONNECT, true))
            gameManager.connectGP();
    }

    @Override
    public void onDestroy() {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Globals.GAMES_CONNECT, true)
                && gameManager.isConnectedGooglePlay())
            gameManager.disconnectGP();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!inGame && !fragmentMain.onBackPressed())
            super.onBackPressed();
        else if (inGame)
            fragmentGame.onBackPressed();
    }

    public void changeToGame(boolean continue_game){
        inGame = true;
        if (!continue_game){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            preferences.edit().putBoolean(Globals.GAME_COMPLETED, false).apply();
            preferences.edit().putBoolean(Globals.GAME_UNDO, false).apply();
        }
        fragmentGame = FragmentGame.getInstance(!continue_game);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft.replace(R.id.main_fragment, fragmentGame, Globals.GAME_FRAGMENT);
        ft.commit();
    }
    public void changeToMenu(int pos){
        inGame = false;
        fragmentMain = FragmentMain.getInstance(pos);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        ft.replace(R.id.main_fragment, fragmentMain, Globals.MAIN_FRAGMENT);
        ft.commit();
    }

    public GPManager getGameManager(){
        return gameManager;
    }

    public void onGPConnected(){
        if (!inGame)
            fragmentMain.updateGoogleButtons();
        else
            fragmentGame.updateGoogleButtons();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gameManager.onActivityResult(requestCode, resultCode, data);
    }
}

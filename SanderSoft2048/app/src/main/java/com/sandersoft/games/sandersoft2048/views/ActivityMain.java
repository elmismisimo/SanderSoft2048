package com.sandersoft.games.sandersoft2048.views;

import android.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sandersoft.games.sandersoft2048.R;
import com.sandersoft.games.sandersoft2048.utils.Globals;

public class ActivityMain extends AppCompatActivity {

    FragmentGame fragmentGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null){
            fragmentGame = FragmentGame.getInstance();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_fragment, fragmentGame, Globals.GAME_FRAGMENT);
            ft.commit();
        } else {
            fragmentGame = (FragmentGame) getFragmentManager().findFragmentByTag(Globals.GAME_FRAGMENT);
        }
    }
}

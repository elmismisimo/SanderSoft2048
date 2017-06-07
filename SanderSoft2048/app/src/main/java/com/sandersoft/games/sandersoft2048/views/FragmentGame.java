package com.sandersoft.games.sandersoft2048.views;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.sandersoft.games.sandersoft2048.R;
import com.sandersoft.games.sandersoft2048.SoundManager;
import com.sandersoft.games.sandersoft2048.controllers.BoardController;
import com.sandersoft.games.sandersoft2048.models.Cell;
import com.sandersoft.games.sandersoft2048.utils.Globals;

/**
 * Created by meixi on 27/05/2017.
 */

public class FragmentGame extends Fragment {

    BoardController controller;

    View rootview;
    RelativeLayout lay_game;
    RelativeLayout lay_header;
    TextView lbl_score_title;
    TextView lbl_score;
    ImageView btn_game_google;
    ImageView btn_restart_game;
    ImageView btn_undo;
    RecyclerView board;
    public CellsAdapter cellsAdapter;

    GestureDetector mDetector;

    LinearLayout lay_menu;
    ViewFlipper sld_menu;

    LinearLayout lay_game_menu;
    Button btn_restart;
    Button btn_quit;
    Button btn_settings;
    Button btn_tutorial;

    LinearLayout lay_settings;
    TextView lbl_title_settings;
    Button btn_sound;
    Button btn_google;
    Button btn_theme;

    LinearLayout lay_google;
    TextView lbl_title_google;
    TextView lbl_subtitle_google;
    Button btn_google_conn;
    Button btn_leaderboards;
    Button btn_achievements;

    LinearLayout lay_game_completed;
    TextView lbl_completed_title;
    TextView lbl_completed;
    Button btn_new_game;
    Button btn_continue;

    LinearLayout lay_game_over;
    TextView lbl_over_title;
    Button btn_restart_gameover;
    Button btn_undo_gameover;

    boolean light;
    boolean avoidLoading;
    int pos = 0;

    int height;
    int width;

    public FragmentGame(){

    }
    public static FragmentGame getInstance(boolean avoidLoading){
        FragmentGame frag = new FragmentGame();
        frag.controller = new BoardController(frag);
        frag.avoidLoading = avoidLoading;
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_game, container, false);

        if (null != savedInstanceState){
            controller = savedInstanceState.getParcelable(Globals.GAME_CONTROLLER);
            controller.setView(this);
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;

        //instantiates the views of the layout
        defineElements();

        //verify if its the first time opening the app, so show tutorial
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(Globals.GAME_TUTORIAL, true)){
            changeSettingTutorial();
            openTutorialDialog1();
        }

        return rootview;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //get objects backedup so we can reload the activity with the same infromation
        outState.putParcelable(Globals.GAME_CONTROLLER, controller);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveGame();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!avoidLoading) {
            loadGame();
        }
        avoidLoading = false;
    }

    public void defineElements() {
        lay_game = (RelativeLayout) rootview.findViewById(R.id.lay_game);
        lay_header = (RelativeLayout) rootview.findViewById(R.id.lay_header);
        lbl_score_title = (TextView) rootview.findViewById(R.id.lbl_score_title);
        lbl_score = (TextView) rootview.findViewById(R.id.lbl_score);
        btn_game_google = (ImageView) rootview.findViewById(R.id.btn_game_google);
        btn_restart_game = (ImageView) rootview.findViewById(R.id.btn_restart_game);
        btn_undo = (ImageView) rootview.findViewById(R.id.btn_undo);
        board = (RecyclerView) rootview.findViewById(R.id.board);
        cellsAdapter = new CellsAdapter();
        GridLayoutManager gridLayout = new GridLayoutManager(getActivity(), 4);
        board.setLayoutManager(gridLayout);
        board.setAdapter(cellsAdapter);

        lay_menu = (LinearLayout) rootview.findViewById(R.id.lay_menu);
        sld_menu = (ViewFlipper) rootview.findViewById(R.id.sld_menu);

        lay_game_menu = (LinearLayout) rootview.findViewById(R.id.lay_game_menu);
        btn_restart = (Button) rootview.findViewById(R.id.btn_restart);
        btn_quit = (Button) rootview.findViewById(R.id.btn_quit);
        btn_settings = (Button) rootview.findViewById(R.id.btn_settings);
        btn_tutorial = (Button) rootview.findViewById(R.id.btn_tutorial);

        lay_settings = (LinearLayout) rootview.findViewById(R.id.lay_settings);
        lbl_title_settings = (TextView) rootview.findViewById(R.id.lbl_title_settings);
        btn_sound = (Button) rootview.findViewById(R.id.btn_sound);
        btn_google = (Button) rootview.findViewById(R.id.btn_google);
        btn_theme = (Button) rootview.findViewById(R.id.btn_theme);

        lay_google = (LinearLayout) rootview.findViewById(R.id.lay_google);
        lbl_title_google = (TextView) rootview.findViewById(R.id.lbl_title_google);
        lbl_subtitle_google = (TextView) rootview.findViewById(R.id.lbl_subtitle_google);
        btn_google_conn = (Button) rootview.findViewById(R.id.btn_google_conn);
        btn_leaderboards = (Button) rootview.findViewById(R.id.btn_leaderboards);
        btn_achievements = (Button) rootview.findViewById(R.id.btn_achievements);

        lay_game_completed = (LinearLayout) rootview.findViewById(R.id.lay_game_completed);
        lbl_completed_title = (TextView) rootview.findViewById(R.id.lbl_completed_title);
        lbl_completed = (TextView) rootview.findViewById(R.id.lbl_completed);
        btn_new_game = (Button) rootview.findViewById(R.id.btn_new_game);
        btn_continue = (Button) rootview.findViewById(R.id.btn_continue);

        lay_game_over = (LinearLayout) rootview.findViewById(R.id.lay_game_over);
        lbl_over_title = (TextView) rootview.findViewById(R.id.lbl_over_title);
        btn_restart_gameover = (Button) rootview.findViewById(R.id.btn_restart_gameover);
        btn_undo_gameover = (Button) rootview.findViewById(R.id.btn_undo_gameover);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //verify if there is light theme
        setTheme();

        updateScore();
        updateUndoButton();
        updateGoogleButtons();

        mDetector = new GestureDetector(getActivity(), new MyGestureDetector());
        board.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDetector.onTouchEvent(event);
                return true;
            }
        });
        btn_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.executeUndo();
            }
        });
        btn_restart_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });

        lay_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeMenu(true);
            }
        });
        btn_game_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenu(2);
            }
        });
        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
                closeMenu(true);
            }
        });
        btn_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitGame();
            }
        });
        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToSettings();
            }
        });
        btn_tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTutorialDialog1();
                closeMenu(true);
            }
        });
        btn_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSettingSound();
                updateSoundButton();
                SoundManager.playMenuIn(getActivity());
            }
        });
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToGoogle();
            }
        });
        btn_theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSettingTheme();
                updateThemeButton();
                SoundManager.playMenuIn(getActivity());
                ((ActivityMain)getActivity()).getGameManager().unlockAchievement(R.string.achievement_oh_my_eyes);
            }
        });
        btn_google_conn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                SoundManager.playMenuIn(getActivity());
            }
        });
        btn_achievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActivityMain)getActivity()).getGameManager().seeAchievements(getActivity());
                SoundManager.playMenuIn(getActivity());
            }
        });
        btn_new_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
                SoundManager.playOpen(getActivity());
                closeMenu(false);
            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundManager.playOpen(getActivity());
                closeMenu(false);
            }
        });
        btn_restart_gameover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
                SoundManager.playOpen(getActivity());
                closeMenu(false);
            }
        });
        btn_undo_gameover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.executeUndo();
                closeMenu(true);
            }
        });
    }
    public void setTheme(){
        light = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(Globals.THEME_LIGHT, false);
        //layout_main_menu
        lay_game.setBackgroundResource(light ? R.color.color_LBackground : R.color.color_Background);
        lbl_score_title.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        lbl_score.setTextColor(getResources().getColor(light ? R.color.color_LAccent : R.color.color_Accent));
        btn_game_google.setBackgroundResource(light ? R.drawable.btn_l : R.drawable.btn);
        updateGoogleButtons();
        btn_restart_game.setBackgroundResource(light ? R.drawable.btn_l : R.drawable.btn);
        btn_restart_game.setImageResource(light ? R.mipmap.restart : R.mipmap.restart_w);
        btn_undo.setBackgroundResource(light ? R.drawable.btn_l : R.drawable.btn);
        updateUndoButton();
        //layout_game_menu
        lay_game_menu.setBackgroundResource(light ? R.color.color_LBackground : R.color.color_Background);
        btn_restart.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_restart.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_quit.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_quit.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_settings.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_settings.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_tutorial.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_tutorial.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        //layout_settings
        lay_settings.setBackgroundResource(light ? R.color.color_LBackground : R.color.color_Background);
        lbl_title_settings.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_sound.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_sound.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_google.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_google.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_theme.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_theme.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
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
        //layout_game_completed
        lay_game_completed.setBackgroundResource(light ? R.color.color_LBackground : R.color.color_Background);
        lbl_completed_title.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        lbl_completed.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_new_game.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_new_game.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_continue.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_continue.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        //layout_game_completed
        lay_game_over.setBackgroundResource(light ? R.color.color_LBackground : R.color.color_Background);
        lbl_over_title.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_restart_gameover.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_restart_gameover.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        btn_undo_gameover.setBackgroundResource(light ? R.drawable.btn_menu_l : R.drawable.btn_menu);
        btn_undo_gameover.setTextColor(getResources().getColor(light ? R.color.color_LText : R.color.color_Text));
        //actualizamos el board
        cellsAdapter.notifyData();
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
    public void updateGoogleButtons() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean google_on = preferences.getBoolean(Globals.GAMES_CONNECT, true);
        String google_state = getString(R.string.google_play) + " " +
                getString(google_on
                        ? (((ActivityMain)getActivity()).getGameManager().isConnectedGooglePlay() ? R.string.connected : R.string.on)
                        : R.string.disconnected);
        btn_google_conn.setText(google_state);
        btn_game_google.setImageResource(light ?
                (google_on ? R.mipmap.google : R.mipmap.google_d) :
                (google_on ? R.mipmap.google_w : R.mipmap.google_w_d));
        btn_leaderboards.setEnabled(google_on && ((ActivityMain)getActivity()).getGameManager().isConnectedGooglePlay());
        btn_achievements.setEnabled(google_on && ((ActivityMain)getActivity()).getGameManager().isConnectedGooglePlay());
    }

    public class CellsAdapter extends RecyclerView.Adapter {
        private final int VIEW_TYPE_CELL = 0;

        public CellsAdapter(){
            setHasStableIds(true);
        }


        public class ItemCellHolder extends RecyclerView.ViewHolder {
            public CardView lay_item;
            public TextView lbl_number;

            public ItemCellHolder(View view) {
                super(view);
                lay_item = (CardView) view.findViewById(R.id.lay_item);
                lbl_number = (TextView) view.findViewById(R.id.lbl_number);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return VIEW_TYPE_CELL; //cell element
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_CELL){
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_cell, parent, false);
                return new ItemCellHolder(itemView);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            //verify the element type
            if (holder instanceof ItemCellHolder){
                //reference the cell
                final Cell c = controller.getCells().get(position);
                //cast the item holder
                final ItemCellHolder ih = (ItemCellHolder) holder;
                //define size
                int size = Math.min(width, height) / 4;
                ih.lbl_number.setMinimumHeight(size);
                ih.lbl_number.setMinimumWidth(size);
                ih.lay_item.setMinimumHeight(size);
                ih.lay_item.setMinimumWidth(size);
                //place values
                ih.lbl_number.setText(c.getNumber() > 0 ? String.valueOf(c.getNumber()) : "");
                ih.lbl_number.setBackgroundColor(getColor(c.getExponent())); //.getNumber()));
            }
        }

        @Override
        public int getItemCount() {
            return controller.getCells().size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void notifyData() {
            notifyDataSetChanged();
        }
    }

    public void updateUI(){
        updateScore();
        updateUndoButton();
        cellsAdapter.notifyData();
    }
    public void updateScore(){
        lbl_score.setText(String.valueOf(controller.getScore()));
    }
    public void updateUndoButton(){
        btn_undo.setEnabled(controller.isAllowUndo());
        btn_undo.setImageResource(light ?
                (controller.isAllowUndo() ? R.mipmap.undo : R.mipmap.undo_d) :
                (controller.isAllowUndo() ? R.mipmap.undo_w : R.mipmap.undo_w_d));
    }

    public void restartGame(){
        controller.restart();
        ((ActivityMain)getActivity()).getGameManager().unlockIncrementalAchievement(R.string.achievement_persistent, 1);
        updateUI();
    }
    public void quitGame(){
        //check achievements
        if (!controller.isGameOver()) {
            ((ActivityMain)getActivity()).getGameManager().unlockIncrementalAchievement(R.string.achievement_quitter, 1);
        }
        //clear undo, set gameover, and delete saved game
        controller.clearUndo();
        controller.setGameOver(true);
        saveGame("");
        //call quitGame on activity
        ((ActivityMain)getActivity()).changeToMenu(0);
        //sound going out
        SoundManager.playMenuOut(getActivity());
    }

    public void saveGame() {
        saveGame(controller.boardToString());
        //saveGame("0:10,9,8,7,3,4,5,6,2,1,0,0,0,0,0,0|");
    }
    public void saveGame(String boardState){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.edit().putString(Globals.GAME_BOARD, boardState).apply();
    }
    public void loadGame(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String savedBoard = preferences.getString(Globals.GAME_BOARD, "");
        controller.stringToBoard(savedBoard);
    }

    Toast mToast;
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener{
        private int MAJOR_MOVE = 60;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int dx = (int) (e2.getX() - e1.getX());
            int dy = (int) (e2.getY() - e1.getY());
            //verify movement in x
            if (Math.abs(dx) > Math.abs(dy)){
                //movement left-right
                if (Math.abs(dx) > MAJOR_MOVE){
                    //mToast = Toast.makeText(getActivity(), dx > 0 ? "Right" : "Left", Toast.LENGTH_SHORT);
                    //mToast.show();
                    if (dx > 0)
                        controller.mergeRight();
                    else
                        controller.mergeLeft();
                }
            } else {
                //movement up-down
                if (Math.abs(dy) > MAJOR_MOVE){
                    //mToast = Toast.makeText(getActivity(), dy > 0 ? "Down" : "Up", Toast.LENGTH_SHORT);
                    //mToast.show();
                    if (dy > 0)
                        controller.mergeDown();
                    else
                        controller.mergeUp();
                }
            }
            return false;
        }
    }

    public int getColor(int exponent){
        return getResources().getColor(getResources().getIdentifier("color" + exponent + (exponent == 0 && light ? "l" : ""), "color", getActivity().getPackageName()));
    }

    public void gameCompleted(){
        /*long currentTime = controller.getCurrentTime();
        //upload score
        if (controller.getType() != BoardController.Type.Custom)
            ((ActivityMain)getActivity()).getGameManager().defineScore(controller.getType(), currentTime);
        //check achievements
        if (controller.getType() != BoardController.Type.Custom){
            ((ActivityMain)getActivity()).getGameManager().unlockIncrementalAchievement(R.string.achievement_winner, 1);
            ((ActivityMain)getActivity()).getGameManager().unlockIncrementalAchievement(R.string.achievement_master, 1);
            ((ActivityMain)getActivity()).getGameManager().unlockIncrementalAchievement(R.string.achievement_lord, 1);
        }
        if (controller.getType() == BoardController.Type.Easy){
            //enable medium mode
            changeSettingMedium();
            ((ActivityMain)getActivity()).getGameManager().unlockAchievement(R.string.achievement_easy_mode);
            if (currentTime <= 15 * 1000) ((ActivityMain)getActivity()).getGameManager().unlockAchievement(R.string.achievement_easy_fast);
            if (currentTime <= 10 * 1000) ((ActivityMain)getActivity()).getGameManager().unlockAchievement(R.string.achievement_easy_faster);
            if (currentTime <= 6 * 1000) ((ActivityMain)getActivity()).getGameManager().unlockAchievement(R.string.achievement_easy_impossible);
            ((ActivityMain)getActivity()).getGameManager().unlockIncrementalAchievement(R.string.achievement_easy_winner, 1);
            ((ActivityMain)getActivity()).getGameManager().unlockIncrementalAchievement(R.string.achievement_easy_master, 1);
            ((ActivityMain)getActivity()).getGameManager().unlockIncrementalAchievement(R.string.achievement_easy_lord, 1);
        }
        if (controller.getType() == BoardController.Type.Medium){
            //enable hard mode
            changeSettingHard();
            ((ActivityMain)getActivity()).getGameManager().unlockAchievement(R.string.achievement_medium_mode);
            if (currentTime <= 75 * 1000) ((ActivityMain)getActivity()).getGameManager().unlockAchievement(R.string.achievement_medium_fast);
            if (currentTime <= 60 * 1000) ((ActivityMain)getActivity()).getGameManager().unlockAchievement(R.string.achievement_medium_faster);
            if (currentTime <= 45 * 1000) ((ActivityMain)getActivity()).getGameManager().unlockAchievement(R.string.achievement_medium_impossible);
            ((ActivityMain)getActivity()).getGameManager().unlockIncrementalAchievement(R.string.achievement_medium_winner, 1);
            ((ActivityMain)getActivity()).getGameManager().unlockIncrementalAchievement(R.string.achievement_medium_master, 1);
            ((ActivityMain)getActivity()).getGameManager().unlockIncrementalAchievement(R.string.achievement_medium_lord, 1);
        }
        if (controller.getType() == BoardController.Type.Hard){
            ((ActivityMain)getActivity()).getGameManager().unlockAchievement(R.string.achievement_hard_mode);
            if (currentTime <= 240 * 1000) ((ActivityMain)getActivity()).getGameManager().unlockAchievement(R.string.achievement_hard_fast);
            if (currentTime <= 210 * 1000) ((ActivityMain)getActivity()).getGameManager().unlockAchievement(R.string.achievement_hard_faster);
            if (currentTime <= 180 * 1000) ((ActivityMain)getActivity()).getGameManager().unlockAchievement(R.string.achievement_hard_impossible);
            ((ActivityMain)getActivity()).getGameManager().unlockIncrementalAchievement(R.string.achievement_hard_winner, 1);
            ((ActivityMain)getActivity()).getGameManager().unlockIncrementalAchievement(R.string.achievement_hard_master, 1);
            ((ActivityMain)getActivity()).getGameManager().unlockIncrementalAchievement(R.string.achievement_hard_lord, 1);
        }
        if (controller.getType() == BoardController.Type.Custom)
            ((ActivityMain)getActivity()).getGameManager().unlockIncrementalAchievement(R.string.achievement_challenger, 1);*/
        //open the completed dialog
        openGameCompleted();
    }
    public void gameOver(){
        //update leaderboard
        ((ActivityMain)getActivity()).getGameManager().defineScore(controller.getScore());
        openGameOver();
    }

    public void onBackPressed(){
        if (lay_menu.getVisibility() == View.GONE)
            openMenu();
        else {
            if (pos == 0 || pos >= 2)
                closeMenu(true);
            else if (pos == 1)
                moveToMenu();
            //else if (pos == 2)
            //    moveToSettings();
        }
    }

    public void closeMenu(boolean playOut){
        if (playOut)
            SoundManager.playMenuOut(getActivity());
        //display the menu
        lay_menu.setVisibility(View.GONE);
    }
    public void openMenu() {
        openMenu(0);
    }
    public void openMenu(int pos) {
        //play menu in sound
        SoundManager.playMenuIn(getActivity());
        //display the menu
        lay_menu.setVisibility(View.VISIBLE);
        //position to main menu
        this.pos = pos;
        sld_menu.setInAnimation(null);
        sld_menu.setOutAnimation(null);
        sld_menu.setDisplayedChild(pos);
    }
    public void moveToMenu(){
        if (pos == 0) return;
        if (pos > 0) {
            //play menu in sound
            SoundManager.playMenuOut(getActivity());
            sld_menu.setInAnimation(getActivity(), R.anim.animation_in_right);
            sld_menu.setOutAnimation(getActivity(), R.anim.animation_out_right);
        }
        pos = 0;
        sld_menu.setDisplayedChild(pos);
    }
    public void moveToSettings(){
        if (pos == 1) return;
        if (pos > 1) {
            //play menu in sound
            SoundManager.playMenuOut(getActivity());
            sld_menu.setInAnimation(getActivity(), R.anim.animation_in_right);
            sld_menu.setOutAnimation(getActivity(), R.anim.animation_out_right);
        } else {
            //play menu in sound
            SoundManager.playMenuIn(getActivity());
            sld_menu.setInAnimation(getActivity(), R.anim.animation_in_left);
            sld_menu.setOutAnimation(getActivity(), R.anim.animation_out_left);
        }
        pos = 1;
        sld_menu.setDisplayedChild(pos);
        updateSoundButton();
        updateThemeButton();
    }
    public void moveToGoogle(){
        if (pos == 2) return;
        if (pos > 2) {
            //play menu in sound
            SoundManager.playMenuOut(getActivity());
            sld_menu.setInAnimation(getActivity(), R.anim.animation_in_right);
            sld_menu.setOutAnimation(getActivity(), R.anim.animation_out_right);
        } else {
            //play menu in sound
            SoundManager.playMenuIn(getActivity());
            sld_menu.setInAnimation(getActivity(), R.anim.animation_in_left);
            sld_menu.setOutAnimation(getActivity(), R.anim.animation_out_left);
        }
        pos = 2;
        sld_menu.setDisplayedChild(pos);
        updateGoogleButtons();
    }
    public void openGameCompleted() {
        //play menu in sound
        SoundManager.playSuccess(getActivity());
        //display the menu
        lay_menu.setVisibility(View.VISIBLE);
        //position to main menu
        pos = 3;
        sld_menu.setInAnimation(null);
        sld_menu.setOutAnimation(null);
        sld_menu.setDisplayedChild(pos);
    }
    public void openGameOver() {
        //play menu in sound
        SoundManager.playError(getActivity());
        //display the menu
        lay_menu.setVisibility(View.VISIBLE);
        //position to main menu
        pos = 4;
        sld_menu.setInAnimation(null);
        sld_menu.setOutAnimation(null);
        sld_menu.setDisplayedChild(pos);
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
    public void changeSettingTutorial(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.edit().putBoolean(Globals.GAME_TUTORIAL, false).apply();
    }


    public void openTutorialDialog1() {
        AlertDialog.Builder supportDialog = new AlertDialog.Builder(getActivity());
        supportDialog.setTitle(R.string.tutorial_t);
        supportDialog.setMessage(R.string.tutorial_1);
        supportDialog.setPositiveButton(R.string.tutorial_next, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                openTutorialDialog2();
                dialog.dismiss();
            }
        });
        supportDialog.setNegativeButton(R.string.tutorial_skip, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = supportDialog.create();
        dialog.show();
    }
    public void openTutorialDialog2() {
        AlertDialog.Builder supportDialog = new AlertDialog.Builder(getActivity());
        supportDialog.setTitle(R.string.tutorial_2_t);
        supportDialog.setMessage(R.string.tutorial_2);
        supportDialog.setPositiveButton(R.string.tutorial_next, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                openTutorialDialog3();
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = supportDialog.create();
        dialog.show();
    }
    public void openTutorialDialog3() {
        AlertDialog.Builder supportDialog = new AlertDialog.Builder(getActivity());
        supportDialog.setTitle(R.string.tutorial_3_t);
        supportDialog.setMessage(R.string.tutorial_3);
        supportDialog.setPositiveButton(R.string.tutorial_next, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                openTutorialDialog4();
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = supportDialog.create();
        dialog.show();
    }
    public void openTutorialDialog4() {
        AlertDialog.Builder supportDialog = new AlertDialog.Builder(getActivity());
        supportDialog.setTitle(R.string.tutorial_4_t);
        supportDialog.setMessage(R.string.tutorial_4);
        supportDialog.setPositiveButton(R.string.tutorial_play, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = supportDialog.create();
        dialog.show();
    }
}

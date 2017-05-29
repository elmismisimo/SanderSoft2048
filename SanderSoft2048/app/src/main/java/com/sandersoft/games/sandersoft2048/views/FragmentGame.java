package com.sandersoft.games.sandersoft2048.views;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.sandersoft.games.sandersoft2048.R;
import com.sandersoft.games.sandersoft2048.controllers.BoardController;
import com.sandersoft.games.sandersoft2048.models.Cell;
import com.sandersoft.games.sandersoft2048.utils.Globals;

/**
 * Created by meixi on 27/05/2017.
 */

public class FragmentGame extends Fragment {

    BoardController controller;

    View rootview;
    TextView lbl_score_title;
    TextView lbl_score;
    Button btn_undo;
    Button btn_restart;
    RecyclerView board;
    public CellsAdapter cellsAdapter;

    GestureDetector mDetector;

    int height;
    int width;

    public FragmentGame(){

    }
    public static FragmentGame getInstance(){
        FragmentGame frag = new FragmentGame();
        frag.controller = new BoardController(frag);
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.edit().putString(Globals.GAME_BOARD, controller.boardToString()).apply();
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String savedBoard = preferences.getString(Globals.GAME_BOARD, "");
        controller.stringToBoard(savedBoard);
    }

    public void defineElements() {
        lbl_score_title = (TextView) rootview.findViewById(R.id.lbl_score_title);
        lbl_score = (TextView) rootview.findViewById(R.id.lbl_score);
        btn_undo = (Button) rootview.findViewById(R.id.btn_undo);
        btn_restart = (Button) rootview.findViewById(R.id.btn_restart);
        board = (RecyclerView) rootview.findViewById(R.id.board);
        cellsAdapter = new CellsAdapter();
        GridLayoutManager gridLayout = new GridLayoutManager(getActivity(), 4);
        board.setLayoutManager(gridLayout);
        board.setAdapter(cellsAdapter);

        updateScore();
        updateUndoButton();

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
        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartGame();
            }
        });
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
                ih.lbl_number.setBackgroundColor(getColor(c.getNumber()));
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
    }

    public void restartGame(){
        controller.initiateBoard();
        updateUI();
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

    public int getColor(long number){
        return getResources().getColor(getResources().getIdentifier("color" + number, "color", getActivity().getPackageName()));
    }
}

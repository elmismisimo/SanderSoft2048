package com.sandersoft.games.sandersoft2048.controllers;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

import com.sandersoft.games.sandersoft2048.R;
import com.sandersoft.games.sandersoft2048.models.Cell;
import com.sandersoft.games.sandersoft2048.utils.Globals;
import com.sandersoft.games.sandersoft2048.views.ActivityMain;
import com.sandersoft.games.sandersoft2048.views.FragmentGame;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by meixi on 27/05/2017.
 */

public class BoardController implements Parcelable {

    FragmentGame view;
    ArrayList<Cell> cells = new ArrayList<>();
    ArrayList<Cell> cellsPre = new ArrayList<>();
    boolean gameOver = false;
    long score = 0;
    long scorePre = 0;
    boolean allowUndo = false;
    boolean waiting = false;

    public BoardController(FragmentGame fragmentGame){
        setView(fragmentGame);
        initiateBoard();
    }
    public void setView(FragmentGame fragmentGame){
        this.view = fragmentGame;
    }

    public void initiateBoard(){
        gameOver = false;
        score = 0;
        scorePre = 0;
        cells.clear();
        for(int i = 0; i < 16; i++){
            cells.add(new Cell());
        }
        //place 2 cells with value
        addNewNumber();
        addNewNumber();
    }
    public void restart(){
        initiateBoard();
        clearUndo();
        view.saveGame("");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(view.getActivity());
        preferences.edit().putBoolean(Globals.GAME_COMPLETED, false).apply();
        preferences.edit().putBoolean(Globals.GAME_UNDO, false).apply();
    }

    public ArrayList<Cell> getCells(){
        return cells;
    }
    public long getScore(){
        return score;
    }
    public boolean isAllowUndo() {
        return allowUndo;
    }
    public boolean isGameOver() {
        return gameOver;
    }
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    private boolean addNewNumber(){
        //get all the free cells
        ArrayList<Integer> free = new ArrayList<>();
        for (int i = 0; i < 16; i++){
            if (cells.get(i).getNumber() == 0)
                free.add(i);
        }
        if (free.size() <= 0) return false;
        Random rand = new Random();
        int p = rand.nextInt(free.size());
        cells.get(free.get(p)).setNewNumber(rand.nextInt(4) == 0 ? 2 : 1);
        //verify if board is full and nothing can be summed
        return free.size() > 1 || verifyGameOver();
    }

    private void unlockAllCells(){
        for (Cell c : cells){
            c.setLocked(false);
        }
    }
    public boolean mergeRight(){
        if (gameOver || waiting) return gameOver;
        boolean moved = false;
        //unlock all cells
        unlockAllCells();
        for (int i = 0; i < 4; i++){
            for (int j = i*4 + 3; j >= i*4; j--){
                if (cells.get(j).getNumber() == 0) continue;
                for (int k = j; k < i*4 + 3; k++){
                    if (cells.get(k+1).getNumber() == 0){
                        if (!moved)
                            setPreForUndo();
                        cells.get(k+1).setNewNumber(cells.get(k).getExponent());
                        cells.get(k).clear();
                        /*cells.get(k+1).setNumber(cells.get(k).getNumber());
                        cells.get(k).setNumber(0);*/
                        moved = true;
                    } else {
                        if (cells.get(k+1).getNumber() == cells.get(k).getNumber() &&
                                !cells.get(k+1).isLocked()){
                            if (!moved)
                                setPreForUndo();
                            cells.get(k+1).increment();
                            cells.get(k).clear();
                            long number = cells.get(k+1).getNumber();
                            verify2048Achievements(number);
                            score += number;

                            /*score += cells.get(k).getNumber()*2;
                            cells.get(k+1).setNumber(cells.get(k).getNumber()*2);
                            cells.get(k).setNumber(0);
                            cells.get(k+1).setLocked(true); //avoid adding another on this*/
                            moved = true;
                        }
                        break;
                    }
                }
            }
        }
        finishMerge();
        if (moved) {
            verifyGameCompleted();
            new PostMerge().execute();
        }
        return gameOver;
    }
    public boolean mergeLeft(){
        if (gameOver || waiting) return gameOver;
        boolean moved = false;
        //unlock all cells
        unlockAllCells();
        for (int i = 0; i < 4; i++){
            for (int j = i*4; j <= i*4 + 3; j++){
                if (cells.get(j).getNumber() == 0) continue;
                for (int k = j; k > i*4; k--){
                    if (cells.get(k-1).getNumber() == 0){
                        if (!moved)
                            setPreForUndo();
                        cells.get(k-1).setNewNumber(cells.get(k).getExponent());
                        cells.get(k).clear();
                        /*cells.get(k-1).setNumber(cells.get(k).getNumber());
                        cells.get(k).setNumber(0);*/
                        moved = true;
                    } else {
                        if (cells.get(k-1).getNumber() == cells.get(k).getNumber() &&
                                !cells.get(k-1).isLocked()){
                            if (!moved)
                                setPreForUndo();
                            cells.get(k-1).increment();
                            cells.get(k).clear();
                            long number = cells.get(k-1).getNumber();
                            verify2048Achievements(number);
                            score += number;

                            /*score += cells.get(k).getNumber()*2;
                            cells.get(k-1).setNumber(cells.get(k).getNumber()*2);
                            cells.get(k).setNumber(0);
                            cells.get(k-1).setLocked(true); //avoid adding another on this*/
                            moved = true;
                        }
                        break;
                    }
                }
            }
        }
        finishMerge();
        if (moved) {
            verifyGameCompleted();
            new PostMerge().execute();
        }
        return gameOver;
    }
    public boolean mergeDown(){
        if (gameOver || waiting) return gameOver;
        boolean moved = false;
        //unlock all cells
        unlockAllCells();
        for (int i = 0; i < 4; i++){
            for (int j = i + 12; j >= i; j-=4){
                if (cells.get(j).getNumber() == 0) continue;
                for (int k = j; k < i + 12; k += 4){
                    if (cells.get(k+4).getNumber() == 0){
                        if (!moved)
                            setPreForUndo();
                        cells.get(k+4).setNewNumber(cells.get(k).getExponent());
                        cells.get(k).clear();
                        /*cells.get(k+4).setNumber(cells.get(k).getNumber());
                        cells.get(k).setNumber(0);*/
                        moved = true;
                    } else {
                        if (cells.get(k+4).getNumber() == cells.get(k).getNumber() &&
                                !cells.get(k+4).isLocked()){
                            if (!moved)
                                setPreForUndo();
                            cells.get(k+4).increment();
                            cells.get(k).clear();
                            long number = cells.get(k+4).getNumber();
                            verify2048Achievements(number);
                            score += number;

                            /*score += cells.get(k).getNumber()*2;
                            cells.get(k+4).setNumber(cells.get(k).getNumber()*2);
                            cells.get(k).setNumber(0);
                            cells.get(k+4).setLocked(true); //avoid adding another on this*/
                            moved = true;
                        }
                        break;
                    }
                }
            }
        }
        finishMerge();
        if (moved) {
            verifyGameCompleted();
            new PostMerge().execute();
        }
        return gameOver;
    }
    public boolean mergeUp(){
        if (gameOver || waiting) return gameOver;
        boolean moved = false;
        //unlock all cells
        unlockAllCells();
        for (int i = 0; i < 4; i++){
            for (int j = i; j <= i + 12; j += 4){
                if (cells.get(j).getNumber() == 0) continue;
                for (int k = j; k > i; k -= 4){
                    if (cells.get(k-4).getNumber() == 0){
                        if (!moved)
                            setPreForUndo();
                        cells.get(k-4).setNewNumber(cells.get(k).getExponent());
                        cells.get(k).clear();
                        /*cells.get(k-4).setNumber(cells.get(k).getNumber());
                        cells.get(k).setNumber(0);*/
                        moved = true;
                    } else {
                        if (cells.get(k-4).getNumber() == cells.get(k).getNumber() &&
                                !cells.get(k-4).isLocked()){
                            if (!moved)
                                setPreForUndo();
                            cells.get(k-4).increment();
                            cells.get(k).clear();
                            long number = cells.get(k-4).getNumber();
                            verify2048Achievements(number);
                            score += number;

                            /*score += cells.get(k).getNumber()*2;
                            cells.get(k-4).setNumber(cells.get(k).getNumber()*2);
                            cells.get(k).setNumber(0);
                            cells.get(k-4).setLocked(true); //avoid adding another on this*/
                            moved = true;
                        }
                        break;
                    }
                }
            }
        }
        finishMerge();
        if (moved) {
            verifyGameCompleted();
            new PostMerge().execute();
        }
        return gameOver;
    }
    public void finishMerge(){
        view.updateUI();
    }

    private class PostMerge extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            waiting = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                Thread.sleep(50);
            }catch (Exception ex){}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            waiting = false;
            gameOver = !addNewNumber();
            view.updateUI();
            if (gameOver)
                view.gameOver();
        }
    }

    public void verifyGameCompleted(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(view.getActivity());
        if (!preferences.getBoolean(Globals.GAME_COMPLETED, false)){
            boolean is2048 = false;
            for (Cell c : cells)
                if (c.getNumber() == 2048) {
                    preferences.edit().putBoolean(Globals.GAME_COMPLETED, true).apply();
                    view.gameCompleted();
                    break;
                }
        }
    }
    public boolean verifyGameOver(){
        for (int i = 0; i < 16; i++){
            int row = i / 4;
            //check to he right
            if (i+1 < 16 && row == (i+1)/4 && cells.get(i).getNumber() == cells.get(i+1).getNumber()) return true;
            //check to he left
            if (i-1 >= 0 && row == (i-1)/4 && cells.get(i).getNumber() == cells.get(i-1).getNumber()) return true;
            //check up
            if (i-4 >= 0 && cells.get(i).getNumber() == cells.get(i-4).getNumber()) return true;
            //check down
            if (i+4 < 16 && cells.get(i).getNumber() == cells.get(i+4).getNumber()) return true;
        }
        return false;    }

    public void verify2048Achievements(long number){
        if (number == 2048){
            //2048 achievement
            ((ActivityMain)view.getActivity()).getGameManager().unlockAchievement(R.string.achievement_2048);
            //winner achievement
            ((ActivityMain)view.getActivity()).getGameManager().unlockIncrementalAchievement(R.string.achievement_winner, 1);
            //master achievement
            ((ActivityMain)view.getActivity()).getGameManager().unlockIncrementalAchievement(R.string.achievement_master, 1);
            //lord achievement
            ((ActivityMain)view.getActivity()).getGameManager().unlockIncrementalAchievement(R.string.achievement_lord, 1);
            //no undos achievement
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(view.getActivity());
            if (!preferences.getBoolean(Globals.GAME_UNDO, false)){
                ((ActivityMain)view.getActivity()).getGameManager().unlockAchievement(R.string.achievement_no_undos);
            }
        }
        //awesome achievement
        else if (number == 4096) ((ActivityMain)view.getActivity()).getGameManager().unlockAchievement(R.string.achievement_awesome);
        //yeah achievement
        else if (number == 8192) ((ActivityMain)view.getActivity()).getGameManager().unlockAchievement(R.string.achievement_yeah);
        //keep going achievement
        else if (number == 16384) ((ActivityMain)view.getActivity()).getGameManager().unlockAchievement(R.string.achievement_keep_going);
        //brutal achievement
        else if (number == 32768) ((ActivityMain)view.getActivity()).getGameManager().unlockAchievement(R.string.achievement_brutal);
        //ascended achievement
        else if (number == 65536) ((ActivityMain)view.getActivity()).getGameManager().unlockAchievement(R.string.achievement_ascended);
        //impossible achievement
        else if (number == 131072) ((ActivityMain)view.getActivity()).getGameManager().unlockAchievement(R.string.achievement_impossible);
    }

    private void setPreForUndo(){
        scorePre = score;
        cellsPre.clear();
        for (Cell c : cells)
            cellsPre.add(c.clone());
        allowUndo = true;
    }
    public void executeUndo(){
        if (!allowUndo) return;
        gameOver = false;
        allowUndo = false;
        cells.clear();
        for (Cell c : cellsPre)
            cells.add(c.clone());
        score = scorePre;
        clearUndo();
        view.updateUI();
        //mark that we already made an undo in this game
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(view.getActivity());
        preferences.edit().putBoolean(Globals.GAME_UNDO, true).apply();
    }
    public void clearUndo(){
        cellsPre.clear();
        scorePre = 0;
        allowUndo = false;
    }

    public String boardToString(){
        String b = "";
        String bp = "";
        if (!gameOver) {
            for (Cell c : cells)
                b += "," + (c.getNumber() <= 0 ? "" : c.getExponent()); //(int)(Math.log(c.getNumber()) / Math.log(2)));
            b = score + ":" + b.substring(1) + " |";
            if (allowUndo) {
                for (Cell c : cellsPre)
                    bp += "," + (c.getNumber() <= 0 ? "" : c.getExponent()); //(int)(Math.log(c.getNumber()) / Math.log(2)));
                bp = scorePre + ":" + bp.substring(1) + " ";
            }
        }
        return b + bp;
    }
    public void stringToBoard(String brds){
        try {
            if (brds.length() <= 0) return;
            String[] bs = brds.split("\\|");
            String[] bs0 = bs[0].split(":");
            score = Long.valueOf(bs0[0]);
            String[] b = bs0[1].split(",");
            cells.clear();
            for (String c : b) {
                if (c.trim().equals("")) cells.add(new Cell());
                else cells.add(new Cell(Integer.valueOf(c.trim()))); //(long) Math.pow(2, Integer.valueOf(c.trim())), false));
            }
            if (bs.length < 2 || bs[1].length() < 0) {
                allowUndo = false;
            } else {
                allowUndo = true;
                String[] bs1 = bs[1].split(":");
                scorePre = Long.valueOf(bs1[0]);
                b = bs1[1].split(",");
                cellsPre.clear();
                for (String c : b) {
                    if (c.trim().equals("")) cellsPre.add(new Cell());
                    else cellsPre.add(new Cell(Integer.valueOf(c.trim()))); //(long) Math.pow(2, Integer.valueOf(c.trim())), false));
                }
            }
            //verify integrity
            if (cells.size() != 16 && cellsPre.size() % 16 != 0)
                throw new Exception("invalid data, boards are invalid");
        } catch (Exception ex){
            initiateBoard();
        }
        view.updateUI();
    }

    // Parcelling part
    public BoardController(Parcel in){
        cells = in.readArrayList(Cell.class.getClassLoader());
        cellsPre = in.readArrayList(Cell.class.getClassLoader());
        gameOver = in.readInt() == 1;
        score = in.readLong();
        scorePre = in.readLong();
        allowUndo = in.readInt() == 1;
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(cells);
        dest.writeList(cellsPre);
        dest.writeInt(gameOver ? 1 : 0);
        dest.writeLong(score);
        dest.writeLong(scorePre);
        dest.writeInt(allowUndo ? 1 : 0);
    }
    public static final Creator CREATOR = new Creator() {
        public BoardController createFromParcel(Parcel in) {
            return new BoardController(in);
        }

        public BoardController[] newArray(int size) {
            return new BoardController[size];
        }
    };

}

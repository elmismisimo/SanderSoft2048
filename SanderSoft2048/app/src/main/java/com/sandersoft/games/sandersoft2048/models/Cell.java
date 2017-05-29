package com.sandersoft.games.sandersoft2048.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by meixi on 27/05/2017.
 */

public class Cell implements Parcelable {
    long number;
    int exponent;
    boolean locked;

    public Cell() {
        number = 0;
        exponent = 0;
        locked = false;
    }
    public Cell(long number, int exponent, boolean locked) {
        this.number = number;
        this.exponent = exponent;
        this.locked = locked;
    }
    public Cell(int exponent){
        setNewNumber(exponent);
        locked = false;
    }

    public Cell clone(){
        return new Cell(this.number, this.exponent, this.locked);
    }

    public void increment(){
        this.number *= 2;
        this.exponent++;
        this.locked = true;
    }
    public void clear(){
        this.number = 0;
        this.exponent = 0;
    }
    public void setNewNumber(int exponent) {
        this.number = (long)Math.pow(2, exponent);
        this.exponent = exponent;
    }


    public long getNumber() {
        return number;
    }
    public void setNumber(long number) {
        this.number = number;
    }
    public boolean isLocked() {
        return locked;
    }
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    public int getExponent() {
        return exponent;
    }
    public void setExponent(int exponent) {
        this.exponent = exponent;
    }

    // Parcelling part
    public Cell(Parcel in){
        number = in.readLong();
        locked = in.readInt() == 1;
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(number);
        dest.writeInt(locked ? 1 : 0);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Cell createFromParcel(Parcel in) {
            return new Cell(in);
        }

        public Cell[] newArray(int size) {
            return new Cell[size];
        }
    };

}

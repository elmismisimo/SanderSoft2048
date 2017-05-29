package com.sandersoft.games.sandersoft2048.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by meixi on 27/05/2017.
 */

public class Cell implements Parcelable {
    long number;
    boolean locked;

    public Cell() {
        number = 0;
        locked = false;
    }
    public Cell(long number, boolean locked) {
        this.number = number;
        this.locked = locked;
    }

    public Cell clone(){
        return new Cell(this.number, this.locked);
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

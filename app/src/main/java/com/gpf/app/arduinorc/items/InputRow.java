package com.gpf.app.arduinorc.items;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Zalo on 19/07/2015.
 */
public class InputRow implements Parcelable {

    String title, value;

    public InputRow(String title, String value) {
        this.title = title;
        this.value = value;
    }
    public InputRow(Parcel in){
        title = in.readString();
        value = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getTitle());
        dest.writeString(getValue());
    }

    public static final Parcelable.Creator<InputRow> CREATOR = new Parcelable.Creator<InputRow>(){

        @Override
        public InputRow createFromParcel(Parcel source) {
            return new InputRow(source);
        }

        @Override
        public InputRow[] newArray(int size) {
            return new InputRow[size];
        }
    };
}

package com.gpf.app.arduinorc.items;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Zalo on 19/07/2015.
 */
public class InputRow implements Parcelable {

    String id, title, value;

    public InputRow(String id, String title, String value) {
        this.id = id;
        this.title = title;
        this.value = value;
        processStrings();
    }
    public InputRow(Parcel in){
        id = in.readString();
        title = in.readString();
        value = in.readString();
        processStrings();
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
        this.id = id.replace("/", "_");
        this.id = id.replace("-", "_");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.title = title.replace("/", "_");
        this.title = title.replace("-", "_");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private void processStrings(){
        this.id = id.replace("/", "_");
        this.id = id.replace("-", "_");
        this.title = title.replace("/", "_");
        this.title = title.replace("-", "_");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getID());
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

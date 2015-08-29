package com.gpf.app.arduinorc.items;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Zalo on 29/08/2015.
 */
public class ConsoleMessage implements Parcelable{
    String text;
    int arrowDirection;

    public ConsoleMessage(String text, int arrowDirection) {
        this.text = text;
        this.arrowDirection = arrowDirection;
    }
    public ConsoleMessage(Parcel in){
        text = in.readString();
        arrowDirection = in.readInt();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getArrowDirection(){
        return arrowDirection;
    }

    public void setArrowDirection(int arrowDirection){
        this.arrowDirection = arrowDirection;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getText());
        dest.writeInt(getArrowDirection());
    }

    public static final Parcelable.Creator<ConsoleMessage> CREATOR = new Parcelable.Creator<ConsoleMessage>(){

        @Override
        public ConsoleMessage createFromParcel(Parcel source) {
            return new ConsoleMessage(source);
        }

        @Override
        public ConsoleMessage[] newArray(int size) {
            return new ConsoleMessage[size];
        }
    };
}

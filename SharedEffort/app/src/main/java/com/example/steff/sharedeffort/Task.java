package com.example.steff.sharedeffort;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Task implements Parcelable{
    public int taskId;
    public String taskName;
    public int points;
    public int pointsForTransfert;
    public Date taskDate;
    public boolean isDone;
    public int personId;

    public Task(int taskId, String taskName, int points, int pointsForTransfert, String taskDate, int isDone, int personId){
        this.taskId = taskId;
        this.taskName = taskName;
        this.points = points;
        this.pointsForTransfert = pointsForTransfert;
        try {
            this.taskDate = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z").parse(taskDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.isDone = isDone == 1 ? true : false;
        this.personId = personId;

    }

    protected Task(Parcel in) {
        taskId = in.readInt();
        taskName = in.readString();
        points = in.readInt();
        pointsForTransfert = in.readInt();
        taskDate = (Date)in.readSerializable();
        isDone = in.readByte() != 0;
        personId = in.readInt();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(taskId);
        dest.writeString(taskName);
        dest.writeInt(points);
        dest.writeInt(pointsForTransfert);
        dest.writeSerializable(taskDate);
        dest.writeByte((byte)(isDone?1:0));
        dest.writeInt(personId);
    }
}

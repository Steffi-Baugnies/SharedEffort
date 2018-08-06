package com.example.steff.sharedeffort;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Task implements Parcelable{
    public int taskId;
    public String taskName;
    public int points;
    public int pointsForTransfer;
    public LocalDate taskDate;
    public int isDone;
    public int personId;
    public boolean isRecu;

    public Task(int taskId, String taskName, int points, int pointsForTransfer, String taskDate, int isDone, int personId, int isRecu){
        this.taskId = taskId;
        this.taskName = taskName;
        this.points = points;
        this.pointsForTransfer = pointsForTransfer;

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");
        this.taskDate = LocalDate.parse(taskDate, dateTimeFormatter);

        this.isDone = isDone;
        this.personId = personId;
        this.isRecu = isRecu == 1 ? true : false;

    }

    protected Task(Parcel in) {
        taskId = in.readInt();
        taskName = in.readString();
        points = in.readInt();
        pointsForTransfer = in.readInt();
        taskDate = (LocalDate) in.readSerializable();
        isDone = in.readInt();
        personId = in.readInt();
        isRecu = in.readByte() != 0;
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
        dest.writeInt(pointsForTransfer);
        dest.writeSerializable(taskDate);
        dest.writeInt(isDone);
        dest.writeInt(personId);
        dest.writeByte((byte)(isRecu?1:0));
    }
}

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
    public boolean isRecu;
    public int personId;
    public LocalDate taskDate;
    public int status;

    public Task(int taskId, String taskName, int points, int pointsForTransfer, int isRecu, int personId, String taskDate, int status){
        this.taskId = taskId;
        this.taskName = taskName;
        this.points = points;
        this.pointsForTransfer = pointsForTransfer;
        this.isRecu = isRecu == 1 ? true : false;
        this.personId = personId;

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");
        this.taskDate = LocalDate.parse(taskDate, dateTimeFormatter);

        this.status = status;


    }

    protected Task(Parcel in) {
        taskId = in.readInt();
        taskName = in.readString();
        points = in.readInt();
        pointsForTransfer = in.readInt();
        taskDate = (LocalDate) in.readSerializable();
        personId = in.readInt();
        isRecu = in.readByte() != 0;
        status = in.readInt();
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
        dest.writeInt(personId);
        dest.writeByte((byte)(isRecu?1:0));
        dest.writeInt(status);
    }
}

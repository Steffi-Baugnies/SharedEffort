package com.example.steff.sharedeffort;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class Event implements Parcelable{
    public int eventId;
    public String eventName;
    public String eventDescription;
    public int personId;
    public Boolean isRecu;
    public LocalDateTime eventDate;

    public Event(int eventId, String eventName, String eventDescription, int personId, int isRecu, String eventDate){
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.personId = personId;
        this.isRecu = isRecu == 1 ? true : false;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss z");
        this.eventDate = LocalDateTime.parse(eventDate, dateTimeFormatter);
    }

    protected Event(Parcel in) {
        eventId = in.readInt();
        eventName = in.readString();
        eventDescription = in.readString();
        personId = in.readInt();
        isRecu = in.readByte() != 0;
        eventDate = (LocalDateTime) in.readSerializable();
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(eventId);
        dest.writeString(eventName);
        dest.writeString(eventDescription);
        dest.writeInt(personId);
        dest.writeByte((byte)(isRecu?1:0));
        dest.writeSerializable(eventDate);
    }
}

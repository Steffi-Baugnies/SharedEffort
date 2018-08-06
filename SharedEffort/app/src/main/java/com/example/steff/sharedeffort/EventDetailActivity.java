package com.example.steff.sharedeffort;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class EventDetailActivity extends AppCompatActivity {

    private TextView mEventName;
    private TextView mEventDescription;
    private TextView mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Event event = getIntent().getParcelableExtra("event");
        mEventName = findViewById(R.id.activity_eventDetail_eventName);
        mEventDescription = findViewById(R.id.activity_eventDetail_eventDescription);
        mDate = findViewById(R.id.activity_eventDetail_eventDate);

        mEventName.setText(event.eventName);
        mEventDescription.setText("" + event.eventDescription);
        mDate.setText("" + event.eventDate);
    }



}

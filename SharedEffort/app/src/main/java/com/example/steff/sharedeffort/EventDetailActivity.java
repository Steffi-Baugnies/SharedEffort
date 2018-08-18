package com.example.steff.sharedeffort;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.time.format.DateTimeFormatter;

public class EventDetailActivity extends AppCompatActivity {

    private TextView mEventName;
    private TextView mEventDescription;
    private TextView mDate;
    private TextView mPerson;
    private Button mReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Event event = getIntent().getParcelableExtra("event");
        mEventName = findViewById(R.id.activity_eventDetail_eventName);
        mEventDescription = findViewById(R.id.activity_eventDetail_eventDescription);
        mDate = findViewById(R.id.activity_eventDetail_eventDate);
        mPerson = findViewById(R.id.activity_eventDetail_person);

        mEventName.setText(event.eventName);
        mEventDescription.setText("" + event.eventDescription);
        mDate.setText(event.eventDate.toString().replace("T", " "));

        mReturn = findViewById(R.id.activity_eventDetail_return_btn);
        System.out.println("steak : " + event.eventDate);
        if(event.personId == -1){
            mPerson.setText("Tout le monde");
        }
        else{
            for(int i = 0; i < ConnectedUserInfo.getInstance().getFamilyMembers().size(); i++){
                if(ConnectedUserInfo.getInstance().getFamilyMembers().get(i).getId() == event.personId){
                    mPerson.setText(ConnectedUserInfo.getInstance().getFamilyMembers().get(i).getFname());
                }
            }

        }

        initReturnBtn();

    }

    private void initReturnBtn(){
        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent familyBoardActivity = new Intent(EventDetailActivity.this, FamilyBoardActivity.class);
                startActivity(familyBoardActivity);
            }
        });

    }

}

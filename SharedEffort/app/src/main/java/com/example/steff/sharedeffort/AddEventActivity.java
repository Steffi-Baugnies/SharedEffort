package com.example.steff.sharedeffort;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {

    private EditText mEventName;
    private EditText mEventDescription;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private Spinner mUserSpinner;
    private AppCompatCheckBox mRecurrence;

    private Map<String, Integer> userInfo;

    private Button mValidateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        mEventName = findViewById(R.id.activity_addEvent_eventName);
        mEventDescription = findViewById(R.id.activity_addEvent_eventDesc);
        mDatePicker = findViewById(R.id.activity_addEvent_datePicker);
        mTimePicker = findViewById(R.id.activity_addEvent_timePicker);
        mUserSpinner = findViewById(R.id.activity_addEvent_userSpinner);
        mRecurrence = findViewById(R.id.activity_addEvent_recurrence);

        mValidateBtn = findViewById(R.id.activity_addEvent_validate_btn);

        initValidateBtn();
        getFamilyInfo();
    }

    public void getFamilyInfo(){
        JSONObject familyId = new JSONObject();
        try {
            familyId.put("familyId", ConnectedUserInfo.getInstance().getFamilyId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // to modify
        IEventNotifier eventNotifier = new IEventNotifier() {
            @Override
            public void RequestComplete(JSONObject jsonObject) {
                JSONArray familyMembers = null;
                try {
                    familyMembers = jsonObject.getJSONArray("familyMembers");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                userInfo = new HashMap<>();
                final List<String> familyMemberNames = new ArrayList<>();
                familyMemberNames.add("Tout le monde");
                userInfo.put("Tout le monde", -1);
                for(int i = 0; i < familyMembers.length(); i++){
                    try {
                        JSONObject familyMember = (JSONObject) familyMembers.get(i);
                        familyMemberNames.add(familyMember.getString("fname"));
                        userInfo.put(familyMember.getString("fname"), familyMember.getInt("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                AddEventActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        createSpinner(familyMemberNames);
                    }
                });

            }
        };
        new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "board/familyInfo", familyId, eventNotifier)).start();
    }

    public void createSpinner(List<String> familyMembersName){
        mUserSpinner = findViewById(R.id.activity_addEvent_userSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, familyMembersName);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mUserSpinner.setAdapter(adapter);
    }

    public void initValidateBtn(){
        mValidateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEventName.getText().length() == 0){
                    Toaster toast = new Toaster("Veuillez donner un nom à l'événement", AddEventActivity.this);
                    toast.showToast();
                }
                else {
                    addEvent();
                }
            }
        });
    }

    public void addEvent(){
        JSONObject event = new JSONObject();
        String eventDate = mDatePicker.getYear() + "-" + (mDatePicker.getMonth() + 1) + "-" + mDatePicker.getDayOfMonth() + " " + mTimePicker.getHour() + ":" + mTimePicker.getMinute();
        String desc = (mEventDescription.getText().length() == 0 ? "" : mEventDescription.getText().toString());

        try {
            event.put("eventName", mEventName.getText());
            event.put("eventDescription", desc);
            event.put("eventDate", eventDate);
            event.put("famId", ConnectedUserInfo.getInstance().getFamilyId());
            event.put("persId", userInfo.get(mUserSpinner.getSelectedItem()));
            event.put("recu", mRecurrence.isChecked());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // to modify
        IEventNotifier eventNotifier = new IEventNotifier() {
            @Override
            public void RequestComplete(JSONObject jsonObject) {
                System.out.println(jsonObject.toString());
                int state = 0;
                String message = "";
                try {
                    state = jsonObject.getInt("state");
                    message = jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(state == 0){
                    Toaster toast = new Toaster(message, AddEventActivity.this);
                }
                else{
                    PreviousToast.getInstance().setMessage(message);
                    finish();
                }
            }
        };
        new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "board/addEvent", event, eventNotifier)).start();

    }
}

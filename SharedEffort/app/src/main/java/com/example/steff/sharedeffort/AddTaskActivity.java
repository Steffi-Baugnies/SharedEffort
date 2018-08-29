package com.example.steff.sharedeffort;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {

    private Spinner mUserSpinner;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;

    private EditText mTaskName;
    private EditText mPoints;
    private EditText mPointsForTransfer;
    private AppCompatCheckBox mRecurrence;

    private Button mValidateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        mTaskName = findViewById(R.id.activity_addTask_taskName);
        mPoints = findViewById(R.id.activity_addTask_points);
        mPointsForTransfer = findViewById(R.id.activity_addTask_pointsForTransfer);
        mDatePicker = findViewById(R.id.activity_addTask_datePicker);
        mTimePicker = findViewById(R.id.activity_addTask_timePicker);
        mUserSpinner = findViewById(R.id.activity_addTask_userSpinner);
        mRecurrence = findViewById(R.id.activity_addTask_recurrence);
        mValidateBtn = findViewById(R.id.activity_addTask_validateBtn);
        setTimePickerMode();
        initValidateBtn();
        createSpinner();
    }

    public void setTimePickerMode(){
        mTimePicker.setIs24HourView(true);
    }


    // Calls the API method to add a task and gives it the user entered paramaters
    public void addTask(){
        JSONObject task = new JSONObject();
        String taskDate = mDatePicker.getYear() + "-" + (mDatePicker.getMonth() + 1) + "-" + mDatePicker.getDayOfMonth() + " " + mTimePicker.getHour() + ":" + mTimePicker.getMinute();
        String points = (mPoints.getText().length() == 0 ? "0" : mPoints.getText().toString());
        String pointsForTransfer = (mPointsForTransfer.getText().length() == 0 ? "0" : mPointsForTransfer.getText().toString());
        List<FamilyMember> familyMembers = ConnectedUserInfo.getInstance().getFamilyMembers();
        int selectedItemPosition = mUserSpinner.getSelectedItemPosition();
        int persIdIndex = (selectedItemPosition > 0 ) ? familyMembers.get(selectedItemPosition - 1).getId() : -1;
        try {
            task.put("taskName", mTaskName.getText());
            task.put("points", points);
            task.put("tpoints", pointsForTransfer);
            task.put("isRecu", mRecurrence.isChecked());
            task.put("persId", persIdIndex);
            task.put("famId", ConnectedUserInfo.getInstance().getFamilyId());
            task.put("taskDate", taskDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        IEventNotifier eventNotifier = new IEventNotifier() {
            @Override
            public void RequestComplete(JSONObject jsonObject) {
                System.out.println(jsonObject.toString());
                int state = 0;
                String message = "";
                try {
                    state = jsonObject.getInt("status");
                    message = jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(state == 0){
                    Toaster toast = new Toaster(message, AddTaskActivity.this);
                    toast.showToast();
                }
                else{
                    PreviousToast.getInstance().setMessage(message);
                    finish();
                }
            }
        };
        new Thread(new ApiRequestHandler("board/addTask", task, eventNotifier)).start();

    }


    // Creates a spinner with the family members' names
    public void createSpinner() {
        List<String> familyMembersName = new ArrayList<>();
        List<FamilyMember> familyMembers = ConnectedUserInfo.getInstance().getFamilyMembers();
        familyMembersName.add("Libre");
        for (int i = 0; i < familyMembers.size(); i++) {
            familyMembersName.add(familyMembers.get(i).getFname());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, familyMembersName);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mUserSpinner.setAdapter(adapter);
    }

    // Sets an on click listener on the add task button
    public void initValidateBtn(){
        mValidateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTaskName.getText().length() == 0){
                    Toaster toast = new Toaster("Veuillez donner un nom à la tâche", AddTaskActivity.this);
                    toast.showToast();
                }
                else {
                    // If the fields are correctly filled out, calls the addTask() method
                    addTask();
                }
            }
        });
    }
}

package com.example.steff.sharedeffort;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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

public class AddTaskActivity extends AppCompatActivity {

    private Spinner mUserSpinner;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;

    private EditText mTaskName;
    private EditText mPoints;
    private EditText mPointsForTransfer;
    private AppCompatCheckBox mRecurrence;

    private Button mValidateBtn;

    private String mAdminPswd = "";

    private Map<String, Integer> userInfo;

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
        getFamilyInfo();
        setTimePickerMode();
        initValidateBtn();
    }

    public void setTimePickerMode(){
        mTimePicker.setIs24HourView(true);
    }

    public void addTask(){
        JSONObject task = new JSONObject();
        String taskDate = mDatePicker.getYear() + "-" + (mDatePicker.getMonth() + 1) + "-" + mDatePicker.getDayOfMonth() + " " + mTimePicker.getHour() + ":" + mTimePicker.getMinute();
        try {
            task.put("connectedUser", ConnectedUserInfo.getInstance().getConnectedUser());
            task.put("pswd", mAdminPswd);
            task.put("taskName", mTaskName.getText());
            task.put("points", mPoints.getText());
            task.put("pointsForTransfer", mPointsForTransfer.getText());
            task.put("taskDate", taskDate);
            task.put("persId", userInfo.get(mUserSpinner.getSelectedItem()));
            task.put("recurrent", mRecurrence.isChecked());
            task.put("famId", ConnectedUserInfo.getInstance().getFamilyId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // to modify
        IEventNotifier eventNotifier = new IEventNotifier() {
            @Override
            public void RequestComplete(JSONObject jsonObject) {
                System.out.println(jsonObject.toString());

            }
        };
        new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "board/addTask", task, eventNotifier)).start();

    }


    public void createSpinner(List<String> familyMembersName){
        mUserSpinner = findViewById(R.id.activity_addTask_userSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, familyMembersName);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mUserSpinner.setAdapter(adapter);
    }

    public void initValidateBtn(){
        mValidateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAdminPasswordPopup();
            }
        });
    }

    public void openAdminPasswordPopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_admin_password, null);
        builder.setView(dialogView).setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText = dialogView.findViewById(R.id.dialog_admin_password_input);
                System.out.println(editText.getText().toString());
                mAdminPswd = editText.getText().toString();

                addTask();

            }
        }).setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
                familyMemberNames.add("Libre");
                userInfo.put("Libre", -1);
                for(int i = 0; i < familyMembers.length(); i++){
                    try {
                        JSONObject familyMember = (JSONObject) familyMembers.get(i);
                        familyMemberNames.add(familyMember.getString("fname"));
                        userInfo.put(familyMember.getString("fname"), familyMember.getInt("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                AddTaskActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        createSpinner(familyMemberNames);
                    }
                });

            }
        };
        new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "board/familyInfo", familyId, eventNotifier)).start();
    }
}

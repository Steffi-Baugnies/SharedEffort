package com.example.steff.sharedeffort;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FamilyBoardActivity extends AppCompatActivity {

    private Spinner mUserSpinner;

    private List<Task> taskList;

    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_board);
        String previousMessage = PreviousToast.getInstance().getMessage();
        if(previousMessage != null){
            Toaster toaster = new Toaster(previousMessage, FamilyBoardActivity.this);
            toaster.showToast();
        }
        getFamilyInfo();

        linearLayout = findViewById(R.id.activity_familyBoard_debug);
        DateHandler week = new DateHandler();
        week.getWeekDates();

    }

    public void createSpinner(List<String> familyMembersName){
        mUserSpinner = findViewById(R.id.activity_familyBoard_userSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, familyMembersName);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mUserSpinner.setAdapter(adapter);
        mUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getPersonTasks();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getPersonTasks();
    }

    public void getPersonTasks(){

        JSONObject tasksRequest = new JSONObject();
        try {
            tasksRequest.put("familyId", ConnectedUserInfo.getInstance().getFamilyId());
            tasksRequest.put("fName",mUserSpinner.getSelectedItem());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IEventNotifier eventNotifier = new IEventNotifier() {
            @Override
            public void RequestComplete(JSONObject jsonObject) {
                JSONArray tasks = null;
                try {
                    tasks = jsonObject.getJSONArray("personTasks");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(tasks != null){
                    taskList = new ArrayList<>();
                    for(int i = 0 ; i < tasks.length(); i++){
                        try {
                            JSONObject taskObject = (JSONObject) tasks.get(i);
                            Task task = new Task(
                                    taskObject.getInt("id"),
                                    taskObject.getString("nomTache"),
                                    taskObject.getInt("nbPointsTache"),
                                    taskObject.getInt("nbPointsTransfert"),
                                    taskObject.getString("dateTache"),
                                    taskObject.getInt("estFaite"),
                                    taskObject.getInt("idPersonne")
                            );
                            taskList.add(task);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    FamilyBoardActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createClickableTasks(taskList);
                        }
                    });

                }

            }
        };
        new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "board/personTasks", tasksRequest, eventNotifier)).start();

    }

    public void createClickableTasks(final List<Task> tasks){
        linearLayout.removeAllViews();
        for(int i = 0; i < tasks.size(); i++){
            Button btn = new Button(this);
            final Task task = tasks.get(i);
            btn.setText(task.taskName);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent taskDetailActivity = new Intent(FamilyBoardActivity.this, TaskDetailActivity.class);
                    taskDetailActivity.putExtra("task", task);
                    startActivity(taskDetailActivity);
                }
            });
            linearLayout.addView(btn);

        }
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
                final List<String> familyMemberNames = new ArrayList<>();
                for(int i = 0; i < familyMembers.length(); i++){
                    try {
                        JSONObject familyMember = (JSONObject) familyMembers.get(i); 
                        familyMemberNames.add(familyMember.getString("fname"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                FamilyBoardActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        createSpinner(familyMemberNames);
                    }
                });

            }
        };
        new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "board/familyInfo", familyId, eventNotifier)).start();
    }
}

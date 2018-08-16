package com.example.steff.sharedeffort;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView mTaskName;
    private TextView mPoints;
    private TextView mPointsForTransfer;
    private TextView mDate;
    private TextView mPerson;
    private String mFName;

    private Button mReturn;
    private Button mClaimTaskBtn;
    private Button mTransferTaskBtn;
    private Button mDeleteTaskBtn;
    private Button mRequestValidationBtn;
    private Button mValidateBtn;
    private Task mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        mTask = getIntent().getParcelableExtra("task");
        mTaskName = findViewById(R.id.activity_taskDetail_taskName);
        mPoints = findViewById(R.id.activity_taskDetail_points);
        mPointsForTransfer = findViewById(R.id.activity_taskDetail_pointsForTransfer);
        mDate = findViewById(R.id.activity_taskDetail_Date);
        mPerson = findViewById(R.id.activity_taskDetail_person);

        mReturn = findViewById(R.id.activity_taskDetail_return_btn);
        mClaimTaskBtn = findViewById(R.id.activity_taskDetail_claimTask_btn);
        mTransferTaskBtn = findViewById(R.id.activity_taskDetail_transferTask_btn);
        mDeleteTaskBtn = findViewById(R.id.activity_taskDetail_deleteTask_Btn);
        mRequestValidationBtn = findViewById(R.id.activity_taskDetail_request_validation_btn);
        mValidateBtn = findViewById(R.id.activity_taskDetail_validate_btn);

        System.out.println(mTask.points);

        mTaskName.setText(mTask.taskName);
        mPoints.setText("" + mTask.points);
        mPointsForTransfer.setText("" + mTask.pointsForTransfer);
        mDate.setText(mTask.taskDate.toString());

        getPersonName();
        mPerson.setText(mFName);
        initReturnBtn();
        initClaimTaskBtn(mTask);
        initTransferTaskBtn(mTask);
        initDeleteTaskBtn();
        initRequestValidationBtn();
        initValidateBtn();
    }

    public void getPersonName(){
        for(int i = 0; i < ConnectedUserInfo.getInstance().getFamilyMembers().size(); i++){
            if(ConnectedUserInfo.getInstance().getFamilyMembers().get(i).getId() == mTask.personId){
                mFName = ConnectedUserInfo.getInstance().getFamilyMembers().get(i).getFname();
            }
        }
    }

    private void initReturnBtn(){
        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent familyBoardActivity = new Intent(TaskDetailActivity.this, FamilyBoardActivity.class);
                startActivity(familyBoardActivity);
            }
        });
    }
    private void initClaimTaskBtn(final Task task){
        if(task.personId == -1){
            mClaimTaskBtn.setVisibility(View.VISIBLE);
        }

        mClaimTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject userTaskInfo = new JSONObject();
                try {
                    userTaskInfo.put("persId", ConnectedUserInfo.getInstance().getConnectedMember());
                    userTaskInfo.put("taskId", task.taskId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                IEventNotifier eventNotifier = new IEventNotifier() {
                    @Override
                    public void RequestComplete(JSONObject jsonObject) {
                        int state = 0;
                        String message = "";
                        try {
                            state = (int) jsonObject.get("state");
                            message = jsonObject.get("message").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final int x = state;
                        final String y = message;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(x == 0){
                                    Toaster toast = new Toaster(y, TaskDetailActivity.this);
                                    toast.showToast();
                                }
                                else {
                                    Toaster toast = new Toaster(y, TaskDetailActivity.this);
                                    toast.showToast();
                                }
                            }
                        });

                    }
                };
                new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "board/claimTask", userTaskInfo, eventNotifier)).start();
            }
        });

    }

    private void initTransferTaskBtn(Task task) {
        if(task.personId == ConnectedUserInfo.getInstance().getConnectedMember().getId()){
            if(task.pointsForTransfer <= ConnectedUserInfo.getInstance().getConnectedMember().getPoints()){
                mTransferTaskBtn.setVisibility(View.VISIBLE);
            }
        }
        mTransferTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserSelectionPopUp();
            }
        });
    }

    public void openUserSelectionPopUp(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.user_selection_popup, null);

        final Spinner spinner = dialogView.findViewById(R.id.user_selection_popup_spinner);
        HashMap<Integer, String> members = getNonAdminMembers();
        final List<Integer> memberIds = new ArrayList<>(members.keySet());
        List<String> memberNames = new ArrayList<>(members.values());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(TaskDetailActivity.this, R.layout.support_simple_spinner_dropdown_item, memberNames);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        builder.setView(dialogView).setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

             int id = memberIds.get(spinner.getSelectedItemPosition());
             transferTask(id);

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

    private HashMap<Integer, String> getNonAdminMembers(){
        List<FamilyMember> famList = ConnectedUserInfo.getInstance().getFamilyMembers();
        HashMap<Integer, String> members = new HashMap<>();
        for(int i = 0; i < famList.size(); i++){
            if(!famList.get(i).getAdmin() && famList.get(i).getId() != ConnectedUserInfo.getInstance().getConnectedMember().getId()){
                members.put(famList.get(i).getId(), famList.get(i).getFname());
            }
        }
        return members;
    }

    private void transferTask(int id){
        JSONObject transfer = new JSONObject();
        try {
            transfer.put("taskId", mTask.taskId);
            transfer.put("transferorId", mTask.personId);
            transfer.put("transfereeId", id);
            transfer.put("substractPoints", mTask.pointsForTransfer);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        IEventNotifier eventNotifier = new IEventNotifier() {
            @Override
            public void RequestComplete(JSONObject jsonObject) {
                int state = 0;
                String message = "";

                try {
                    state = jsonObject.getInt("state");
                    message = jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(state == 1){
                    PreviousToast.getInstance().setMessage(message);
                    finish();
                }else{
                    final String finalMessage = message;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toaster toast = new Toaster(finalMessage, TaskDetailActivity.this);
                            toast.showToast();
                        }
                    });
                }
            }
        };
        new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "board/transferTask", transfer, eventNotifier)).start();
    }

    private void initDeleteTaskBtn(){
        if(ConnectedUserInfo.getInstance().getConnectedMember().getAdmin()){
            mDeleteTaskBtn.setVisibility(View.VISIBLE);
        }

        mDeleteTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteConfirmationPopup();
            }
        });
    }

    private void openDeleteConfirmationPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.delete_confirmation_popup, null);
        builder.setView(dialogView).setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTask();

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

    private void deleteTask(){
        JSONObject delete = new JSONObject();
        try {
            delete.put("taskId", mTask.taskId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        IEventNotifier eventNotifier = new IEventNotifier() {
            @Override
            public void RequestComplete(JSONObject jsonObject) {
                int state = 0;
                String message = "";
                try {
                    state = jsonObject.getInt("state");
                    message = jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(state == 1) {
                    PreviousToast.getInstance().setMessage(message);
                    ConnectedUserInfo.getInstance().getConnectedMember().setPoints(ConnectedUserInfo.getInstance().getConnectedMember().getPoints() - mTask.pointsForTransfer);
                    finish();
                }
                else {
                    final String finalMessage = message;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toaster toast = new Toaster(finalMessage, TaskDetailActivity.this);
                            toast.showToast();
                        }
                    });
                }
            }
        };
        new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "board/deleteTask", delete, eventNotifier)).start();
    }

    private void initRequestValidationBtn(){
        if(mTask.status == 0 && ConnectedUserInfo.getInstance().getConnectedMember().getId() == mTask.personId){
            mRequestValidationBtn.setVisibility(View.VISIBLE);
        }
        mRequestValidationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestValidation();
            }
        });
    }

    private void requestValidation(){
        JSONObject requestValidation = new JSONObject();
        try {
            requestValidation.put("taskId", mTask.taskId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        IEventNotifier eventNotifier = new IEventNotifier() {
            @Override
            public void RequestComplete(JSONObject jsonObject) {
                int state = 0;
                String message = "";
                try {
                    state = jsonObject.getInt("state");
                    message = jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(state == 1){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRequestValidationBtn.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                final String finalMessage = message;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toaster toast = new Toaster(finalMessage, TaskDetailActivity.this);
                        toast.showToast();
                    }
                });
            }
        };
        new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "board/requestValidation", requestValidation, eventNotifier)).start();
    }

    private void initValidateBtn(){
        if(ConnectedUserInfo.getInstance().getConnectedMember().getAdmin() && mTask.status == 1){
            mValidateBtn.setVisibility(View.VISIBLE);
        }

        mValidateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateTask();
            }
        });
    }

    private void validateTask(){
        JSONObject validate = new JSONObject();
        try {
            validate.put("taskId", mTask.taskId);
            validate.put("points", mTask.points);
            validate.put("persId", mTask.personId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        IEventNotifier eventNotifier = new IEventNotifier() {
            @Override
            public void RequestComplete(JSONObject jsonObject) {
                int state = 0;
                String message = "";
                try {
                    state = jsonObject.getInt("state");
                    message = jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(state == 1){
                    for(int i = 0; i < ConnectedUserInfo.getInstance().getFamilyMembers().size(); i++){
                        if(mTask.personId == ConnectedUserInfo.getInstance().getFamilyMembers().get(i).getId()){
                            int points = ConnectedUserInfo.getInstance().getFamilyMembers().get(i).getPoints();
                            ConnectedUserInfo.getInstance().getFamilyMembers().get(i).setPoints(points + mTask.points);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mValidateBtn.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                final String finalMessage = message;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toaster toast = new Toaster(finalMessage, TaskDetailActivity.this);
                        toast.showToast();
                    }
                });
            }
        };
        new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "board/validateTask", validate, eventNotifier)).start();
    }
}


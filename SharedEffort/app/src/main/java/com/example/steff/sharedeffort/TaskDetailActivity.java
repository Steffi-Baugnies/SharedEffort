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
        mDate.setText(mTask.taskDate.toString().replace("T", " "));

        getPersonName();
        mPerson.setText(mFName);
        initReturnBtn();
        initClaimTaskBtn(mTask);
        initTransferTaskBtn(mTask);
        initDeleteTaskBtn();
        initRequestValidationBtn();
        initValidateBtn();
    }

    // Finds the first name of the member associated to the task and shows it
    public void getPersonName(){
        for(int i = 0; i < ConnectedUserInfo.getInstance().getFamilyMembers().size(); i++){
            if(ConnectedUserInfo.getInstance().getFamilyMembers().get(i).getId() == mTask.personId){
                mFName = ConnectedUserInfo.getInstance().getFamilyMembers().get(i).getFname();
            }
        }
    }

    // Sets onClickListener
    // If clicked, sends user back to calendar
    private void initReturnBtn(){
        mReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent familyBoardActivity = new Intent(TaskDetailActivity.this, FamilyBoardActivity.class);
                startActivity(familyBoardActivity);
            }
        });
    }

    // If the current task is unassigned, sets the button's visibility to visible
    // Sets onClickListener
    // If clicked, calls API's claimTaskMethod which assigns the task to the connected member
    private void initClaimTaskBtn(final Task task){
        if(task.personId == -1){
            mClaimTaskBtn.setVisibility(View.VISIBLE);
        }

        mClaimTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject userTaskInfo = new JSONObject();
                try {
                    userTaskInfo.put("persId", ConnectedUserInfo.getInstance().getConnectedMember().getId());
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
                new Thread(new ApiRequestHandler("board/claimTask", userTaskInfo, eventNotifier)).start();
            }
        });

    }

    // If the connected member has more (or =) points than the current task, set the button visibility to visible
    // Sets onClickListener
    // If clicked, calls method "openUserSelectionPopUp()"
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

    // Opens a pop up containing a spinner of non admin family members
    // When validate button is clicked, gets spinner position to determine chosen member
    // Calls transferTask() method
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

    // Creates a hashmap containing id and first name of all non admin members of the family
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


    // Calls API's transferTask method which sets chosen member's id as the member assigned to the task and
    // substracts the correct amount of points from the connctedMember
    // Sends user back to calendar
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
                    ConnectedUserInfo.getInstance().getConnectedMember().setPoints(ConnectedUserInfo.getInstance().getConnectedMember().getPoints() - mTask.pointsForTransfer);
                    PreviousToast.getInstance().setMessage(message);
                    Intent familyBoardActivity = new Intent(TaskDetailActivity.this, FamilyBoardActivity.class);
                    finish();
                    startActivity(familyBoardActivity);

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
        new Thread(new ApiRequestHandler( "board/transferTask", transfer, eventNotifier)).start();
    }

    // If the connected member is an admin, sets the button's visibility to visible
    // Sets onClickListener
    // If clicked, calls method "openDeleteConfirmationsPopup()"
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

    // Opens a popup with a delete and cancel button
    // delete button calls method "deleteTask"
    // Cancel button closes pop up
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


    // Calls API's deleteTask method which deletes the task
    // Sends user back to calendar
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
        new Thread(new ApiRequestHandler("board/deleteTask", delete, eventNotifier)).start();
    }


    // If the connected user is the member assigned to the task and the task's status is 0, sets the button's visibility to visible
    // Sets OnclickListener
    // If clicked, calls method "requestValidation"
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

    // Calls API's requestValidation method which sets the tast status to 1
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
        new Thread(new ApiRequestHandler( "board/requestValidation", requestValidation, eventNotifier)).start();
    }


    // If the connected member is admin and the task status is 1, sets the button's visibility to visible
    // Sets onclickListener
    // If clicked, calls method 'validateTask'
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

    // Calls API's validateTask method which sets the task status to 2 (done) and grants the points to
    // the member assigned to the task
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
        new Thread(new ApiRequestHandler("board/validateTask", validate, eventNotifier)).start();
    }
}


package com.example.steff.sharedeffort;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView mTaskName;
    private TextView mPoints;
    private TextView mPointsForTransfer;
    private TextView mDate;
    private Button mClaimTaskBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Task task = getIntent().getParcelableExtra("task");
        mTaskName = findViewById(R.id.activity_taskDetail_taskName);
        mPoints = findViewById(R.id.activity_taskDetail_points);
        mPointsForTransfer = findViewById(R.id.activity_taskDetail_pointsForTransfer);
        mDate = findViewById(R.id.activity_taskDetail_Date);
        mClaimTaskBtn = findViewById(R.id.activity_taskDetail_claimTask_btn);

        System.out.println(task.points);

        mTaskName.setText(task.taskName);
        mPoints.setText("" + task.points);
        mPointsForTransfer.setText("" + task.pointsForTransfer);
        mDate.setText(task.taskDate.toString());
        initClaimTaskBtn(task);
    }

    public void initClaimTaskBtn(final Task task){
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
}

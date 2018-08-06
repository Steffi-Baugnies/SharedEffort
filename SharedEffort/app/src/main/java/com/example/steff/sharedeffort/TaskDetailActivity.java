package com.example.steff.sharedeffort;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView mTaskName;
    private TextView mPoints;
    private TextView mPointsForTransfer;
    private TextView mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Task task = getIntent().getParcelableExtra("task");
        mTaskName = findViewById(R.id.activity_taskDetail_taskName);
        mPoints = findViewById(R.id.activity_taskDetail_points);
        mPointsForTransfer = findViewById(R.id.activity_taskDetail_pointsForTransfer);
        mDate = findViewById(R.id.activity_taskDetail_Date);

        System.out.println(task.points);

        mTaskName.setText(task.taskName);
        mPoints.setText("" + task.points);
        mPointsForTransfer.setText("" + task.pointsForTransfer);
        mDate.setText(task.taskDate.toString());
    }
}

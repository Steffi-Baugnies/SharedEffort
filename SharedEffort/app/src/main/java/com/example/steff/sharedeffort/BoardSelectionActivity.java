package com.example.steff.sharedeffort;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BoardSelectionActivity extends AppCompatActivity {

    private Button mBoardCreationBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_selection);
        InitBoardCreationBtn();
    }

    private void InitBoardCreationBtn(){
        // Sends back to login page... WHY ?
        mBoardCreationBtn = findViewById(R.id.activity_boardSelection_boardCreation_btn);
        mBoardCreationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent boardCreationActivity = new Intent(getBaseContext(), BoardCreationActivity.class);
                startActivity(boardCreationActivity);
            }
        });
    }
}

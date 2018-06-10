package com.example.steff.sharedeffort;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FamilyBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_board);
        String previousMessage = PreviousToast.getInstance().getMessage();
        if(previousMessage != null){
            Toaster toaster = new Toaster(previousMessage, FamilyBoardActivity.this);
            toaster.showToast();
        }
    }
}

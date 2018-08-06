package com.example.steff.sharedeffort;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FamilyMemberManagementActivity extends AppCompatActivity {

    private Button mAddMemberBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member_management);

        mAddMemberBtn = findViewById(R.id.activity_familyMemberManagement_addUser_btn);

        initAddMemberBtn();

        String previousMessage = PreviousToast.getInstance().getMessage();
        if(previousMessage != null){
            Toaster toaster = new Toaster(previousMessage, FamilyMemberManagementActivity.this);
            toaster.showToast();
        }

    }

    protected void onRestart() {
        super.onRestart();
        String previousMessage = PreviousToast.getInstance().getMessage();
        if(previousMessage != null){
            Toaster toaster = new Toaster(previousMessage, FamilyMemberManagementActivity.this);
            toaster.showToast();
        }
    }

    private void initAddMemberBtn(){
        mAddMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addFamilyMemberActivity = new Intent(FamilyMemberManagementActivity.this, AddFamilyMemberActivity.class);
                startActivity(addFamilyMemberActivity);
            }
        });
    }
}

package com.example.steff.sharedeffort;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FamilyMemberManagementActivity extends AppCompatActivity {

    private Button mAddMemberBtn;
    private LinearLayout mClickableFamilyMembers;
    private String mUserPswd;
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

        getFamilyMembersInfo();

    }

    protected void onRestart() {
        super.onRestart();
        String previousMessage = PreviousToast.getInstance().getMessage();
        if(previousMessage != null){
            Toaster toaster = new Toaster(previousMessage, FamilyMemberManagementActivity.this);
            toaster.showToast();
        }
        getFamilyMembersInfo();
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

    private void createClickableFamilyMembers(){
        mClickableFamilyMembers = findViewById(R.id.activity_familyMemberManagement_memberList);
        List<FamilyMember> familyMembers = ConnectedUserInfo.getInstance().getFamilyMembers();

        mClickableFamilyMembers.removeAllViews();

        for(int i = 0; i < familyMembers.size(); i++){
            Button btn = new Button(this);
            btn.setText(familyMembers.get(i).getFname());
            mClickableFamilyMembers.addView(btn);


            final int finalI = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPasswordDialog(finalI);
                }
            });

        }
    }

    public void openPasswordDialog(final int index){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_password, null);
        builder.setView(dialogView).setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText = dialogView.findViewById(R.id.dialog_admin_password_input);
                mUserPswd = editText.getText().toString();

                connectFamilyMember(index);

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

    public void connectFamilyMember(int index){
        FamilyMember fam  = ConnectedUserInfo.getInstance().getFamilyMembers().get(index);
        if(mUserPswd.equals(fam.getPswd())) {
            ConnectedUserInfo.getInstance().setConnectedMember(fam.getId());
            Intent familyBoardActivity = new Intent(FamilyMemberManagementActivity.this, FamilyBoardActivity.class);
            startActivity(familyBoardActivity);
        }
        else{
            Toaster toast = new Toaster("Mot de passe incorrect !", this);
            toast.showToast();
        }
    }

    private void getFamilyMembersInfo(){
        JSONObject family = new JSONObject();
        int famId = ConnectedUserInfo.getInstance().getFamilyId();

        try {
            family.put("familyId", famId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IEventNotifier eventNotifier = new IEventNotifier() {
            @Override
            public void RequestComplete(JSONObject jsonObject) {
                JSONArray familyMembers = null;
                try {
                    familyMembers = jsonObject.getJSONArray("familyMembers");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                List<FamilyMember> members = new ArrayList<>();

                for(int i = 0; i < familyMembers.length(); i++){
                    try {
                        JSONObject familyMember = (JSONObject) familyMembers.get(i);
                        members.add(new FamilyMember(
                                familyMember.getInt("id"),
                                familyMember.getString("fname"),
                                familyMember.getString("lname"),
                                familyMember.getString("birthdate"),
                                familyMember.getInt("points"),
                                familyMember.getString("mdp"),
                                familyMember.getInt("isAdmin")
                        ));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ConnectedUserInfo.getInstance().setFamilyMembers(members);
                FamilyMemberManagementActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createClickableFamilyMembers();

                    }
                });
            }
        };

        new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "board/familyMembersInfo", family, eventNotifier)).start();

    }
}

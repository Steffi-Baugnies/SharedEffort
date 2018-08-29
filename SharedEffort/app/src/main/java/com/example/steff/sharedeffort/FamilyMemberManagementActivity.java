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
import android.widget.ImageButton;
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
    private ImageButton mLogoutBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member_management);

        mAddMemberBtn = findViewById(R.id.activity_familyMemberManagement_addUser_btn);
        mLogoutBtn = findViewById(R.id.activity_familyMemberManagement_logout_btn);

        initAddMemberBtn();

        String previousMessage = PreviousToast.getInstance().getMessage();
        if(previousMessage != null){
            Toaster toaster = new Toaster(previousMessage, FamilyMemberManagementActivity.this);
            toaster.showToast();
        }

        getFamilyMembersInfo();
        initLogoutBtn();

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

    // Sets onClickListener
    // When clicked, sends user to the add family member page
    private void initAddMemberBtn(){
        mAddMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addFamilyMemberActivity = new Intent(FamilyMemberManagementActivity.this, AddFamilyMemberActivity.class);
                startActivity(addFamilyMemberActivity);
            }
        });
    }

    // Sets onClickListener
    // When clicked, sends user to the log in page
    private void initLogoutBtn(){
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivity = new Intent(FamilyMemberManagementActivity.this, LoginActivity.class);
                startActivity(loginActivity);
            }
        });
    }

    // Creates a button for each family member in the connected family
    // Sets onClickListener for each
    private void createClickableFamilyMembers(){
        mClickableFamilyMembers = findViewById(R.id.activity_familyMemberManagement_memberList);
        List<FamilyMember> familyMembers = ConnectedUserInfo.getInstance().getFamilyMembers();

        mClickableFamilyMembers.removeAllViews();

        for(int i = 0; i < familyMembers.size(); i++){
            Button btn = new Button(this);
            btn.setScaleX(0.8F);
            btn.setScaleY(0.8F);
            btn.setText(familyMembers.get(i).getFname());
            btn.setWidth(800);
            btn.setBackgroundColor(getResources().getColor(R.color.logoBottom));
            btn.setTextColor(getResources().getColor(R.color.logoMiddleBottom));
            btn.setTextSize(20);
            btn.setAllCaps(false);
            btn.setPadding(20, 20, 20, 20);

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

    // Opens a dialog asking for the password of the member wishing to log in
    // When validate button is clicked, connectFamilyMember method is called
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

    // If the password entered in the dialog is correct, the member is set as the connectedMember and is sent to the calendar
    public void connectFamilyMember(int index){
        FamilyMember fam  = ConnectedUserInfo.getInstance().getFamilyMembers().get(index);
        if(mUserPswd.equals(fam.getPswd())) {
            ConnectedUserInfo.getInstance().setConnectedMember(fam);
            Intent familyBoardActivity = new Intent(FamilyMemberManagementActivity.this, FamilyBoardActivity.class);
            startActivity(familyBoardActivity);
        }
        else{
            Toaster toast = new Toaster("Mot de passe incorrect !", this);
            toast.showToast();
        }
    }

    // Calls API's getFamilyMembersInfo and set's ConnectedUserInfo's family members
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

        new Thread(new ApiRequestHandler("board/familyMembersInfo", family, eventNotifier)).start();

    }
}

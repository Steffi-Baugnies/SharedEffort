package com.example.steff.sharedeffort;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddFamilyMemberActivity extends AppCompatActivity {
    private EditText mFName;
    private EditText mLName;
    private DatePicker mDatePicker;
    private EditText mPswd;
    private EditText mPswdConf;
    private CheckBox mIsAdmin;

    private Button mValidateBtn;
    private LinearLayout mAdminLayout;
    private TextView mNewAdminText;
    private TextView mAdminText;

    private String mAdminPswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family_member);

        String previousMessage = PreviousToast.getInstance().getMessage();
        if(previousMessage != null){
            Toaster toaster = new Toaster(previousMessage, AddFamilyMemberActivity.this);
            toaster.showToast();
        }

        mFName = findViewById(R.id.activity_addFamilyMember_fname);
        mLName = findViewById(R.id.activity_addFamilyMember_lname);
        mDatePicker = findViewById(R.id.activity_addFamilyMember_birthdate);
        mPswd = findViewById(R.id.activity_addFamilyMember_pswd);
        mPswdConf = findViewById(R.id.activity_addFamilyMember_pswd_confirmation);
        mIsAdmin = findViewById(R.id.activity_addFamilyMember_isAdmin);

        mValidateBtn = findViewById(R.id.activity_addFamilyMember_validate_btn);

        mAdminLayout = findViewById(R.id.activity_addFamilyMember_isAdmin_layout);
        mNewAdminText = findViewById(R.id.activity_addFamilyMember_newAdmin_text);
        mAdminText = findViewById(R.id.activity_addFamilyMember_admin_text);

        initValidateBtn();
        checkForAdminMember();

    }

    public void checkForAdminMember(){
        if(ConnectedUserInfo.getInstance().getFamilyMembers().size() == 0){
            mAdminLayout.setVisibility(View.INVISIBLE);
            mNewAdminText.setVisibility(View.INVISIBLE);
            mAdminText.setVisibility(View.VISIBLE);
            mIsAdmin.setChecked(true);
        }
    }

    private void initValidateBtn(){
        mValidateBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mIsAdmin.isChecked()) {
                    openPasswordDialog();
                }
                else{
                    addUser();
                }
            }
        });
    }

    public void openPasswordDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.admin_password_popup, null);
        builder.setView(dialogView).setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText = dialogView.findViewById(R.id.dialog_admin_password_input);
                mAdminPswd = editText.getText().toString();
                List<String> pswds = getAdminPasswords();

                if(pswds.contains(mAdminPswd)){
                    addUser();
                }
                else{
                    Toaster toast = new Toaster("Mot de passe incorrect", AddFamilyMemberActivity.this);
                    toast.showToast();
                }

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

    private void addUser(){
        String fname = mFName.getText().toString();
        String lname = mLName.getText().toString();
        String pswd = mPswd.getText().toString();
        String pswdConf = mPswdConf.getText().toString();
        String birthdate =  mDatePicker.getYear() + "-" + (mDatePicker.getMonth() + 1) + "-" + mDatePicker.getDayOfMonth();
        int famId = ConnectedUserInfo.getInstance().getFamilyId();
        String message = "";

        if(fname.length() > 0 && lname.length() > 0){
            if(pswd.length() > 0 && pswd.equals(pswdConf)){
                JSONObject newMember = new JSONObject();
                try {
                    newMember.put("fname", fname);
                    newMember.put("lname", lname);
                    newMember.put("birthdate", birthdate);
                    newMember.put("pswd", pswd);
                    newMember.put("isAdmin", mIsAdmin.isChecked());
                    newMember.put("famId", famId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                IEventNotifier eventNotifier = new IEventNotifier() {
                    @Override
                    public void RequestComplete(JSONObject jsonObject) {
                        int state = 0;
                        try {
                            state = jsonObject.getInt("state");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(state == 1){
                            String message = "Le nouveau membre a bien été ajouté";
                            PreviousToast.getInstance().setMessage(message);
                            finish();
                        }
                    }
                };
                new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "addFamilyMember", newMember, eventNotifier)).start();
            }
            else {
                message = "Les mots de passe sont différents";
            }

        }
        else {
            message = "Les champs 'prénom' et 'nom' sont requis";
        }
        Toaster toast = new Toaster(message, AddFamilyMemberActivity.this);
        toast.showToast();
    }

    private List<String> getAdminPasswords(){
        List<FamilyMember> famList = ConnectedUserInfo.getInstance().getFamilyMembers();
        List<String> adminPswds = new ArrayList<>();
        for(int i = 0; i < famList.size(); i++){
            if(famList.get(i).getAdmin()){
                adminPswds.add(famList.get(i).getPswd());
            }
        }
        return adminPswds;
    }
}

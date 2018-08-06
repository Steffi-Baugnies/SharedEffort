package com.example.steff.sharedeffort;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class AddFamilyMemberActivity extends AppCompatActivity {
    private EditText mFName;
    private EditText mLName;
    private DatePicker mDatePicker;
    private EditText mPswd;
    private EditText mPswdConf;
    private CheckBox mIsAdmin;

    private Button mValidateBtn;

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

        initValidateBtn();

    }

    private void initValidateBtn(){
        mValidateBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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

        });

    }
}

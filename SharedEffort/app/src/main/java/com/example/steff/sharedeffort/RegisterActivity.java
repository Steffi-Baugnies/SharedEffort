package com.example.steff.sharedeffort;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class RegisterActivity extends AppCompatActivity {


    // code for date picker taken from : https://www.numetriclabz.com/android-date-picker-tutorial/
    private EditText mNewUserMailAddress;
    private EditText mNewUserPassword;
    private EditText mNewUserPasswordConfirmation;
    private EditText mNewUserLName;
    private EditText mNewUserFName;
    private DatePicker mDatePicker;
    private Button mRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mNewUserMailAddress = findViewById(R.id.activity_register_mailAddress);
        mNewUserPassword = findViewById(R.id.activity_register_password);
        mNewUserPasswordConfirmation = findViewById(R.id.activity_register_passwordConfirmation);
        mNewUserLName = findViewById(R.id.activity_register_lName);
        mNewUserFName = findViewById(R.id.activity_register_fName);
        mDatePicker = findViewById(R.id.activity_register_birthdate);
        initRegisterBtn();
    }


    private void initRegisterBtn(){
        mRegisterBtn = findViewById(R.id.activity_register_register_btn);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mailAddress = mNewUserMailAddress.getText().toString();
                String password = mNewUserPassword.getText().toString();
                String passwordConfirmation = mNewUserPasswordConfirmation.getText().toString();
                String lastName = mNewUserLName.getText().toString();
                String firstName = mNewUserFName.getText().toString();
                String birthdate = mDatePicker.getYear() + "-" + (mDatePicker.getMonth() + 1) + "-" + mDatePicker.getDayOfMonth();

                if(password.length() > 7){
                    if(password.equals(passwordConfirmation)){
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("mailAddress", mailAddress);
                            jsonObject.put("password", password);
                            jsonObject.put("lName", lastName);
                            jsonObject.put("fName", firstName);
                            jsonObject.put("birthdate", birthdate);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        IEventNotifier eventNotifier = new IEventNotifier() {
                            @Override
                            public void RequestComplete(JSONObject jsonObject) {
                                int state = 0;
                                try {
                                    state = (int) jsonObject.get("registrationStatus");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String message = null;
                                try {
                                    message = jsonObject.get("message").toString();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if(state == 1){
                                    PreviousToast.getInstance().setMessage(message);
                                    Intent loginActivity = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(loginActivity);
                                }
                                else{

                                    final String finalMessage = message;
                                    RegisterActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toaster toaster = new Toaster(finalMessage, RegisterActivity.this);
                                            toaster.showToast();
                                        }
                                    });
                                }
                            }
                        };
                        new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "register", jsonObject, eventNotifier)).start();
                    }
                    else {

                        String message = "Les mots de passe ne correspondent pas, veuillez réessayer";
                        Toaster toaster = new Toaster(message, RegisterActivity.this);
                        toaster.showToast();
                    }

                }
                else {
                    String message = "Le mot de passe entré est trop court. Veuillez choisir un mot de passe d'au moins 8 caractères";
                    Toaster toasterPswd = new Toaster(message, RegisterActivity.this);
                    toasterPswd.showToast();
                }
            }
        });


    }


}

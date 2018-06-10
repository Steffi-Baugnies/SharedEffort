package com.example.steff.sharedeffort;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static android.widget.Toast.LENGTH_SHORT;

public class LoginActivity extends AppCompatActivity {

    private Button mLoginBtn;
    private Button mRegisterBtn;
    private EditText mLoginMailAddress;
    private EditText mLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginMailAddress = findViewById(R.id.activity_login_login_mail_address);
        mLoginPassword = findViewById(R.id.activity_login_login_password);
        InitRegisterButton();
        InitLoginButton();
    }

    private void InitLoginButton(){
        mLoginBtn = findViewById(R.id.activity_login_login_btn);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(mLoginMailAddress.getText().toString()) && !TextUtils.isEmpty(mLoginPassword.getText().toString())) {
                    JSONObject credentials = new JSONObject();
                    try {
                        credentials.put("mailAddress", mLoginMailAddress.getText());
                        credentials.put("password", mLoginPassword.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    IEventNotifier eventNotifier = new IEventNotifier() {
                        @Override
                        public void RequestComplete(JSONObject jsonObject) {
                            int loginState = 0;
                            try {
                                loginState = (int) jsonObject.get("connectionStatus");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (loginState == 1) {
                                // Je pense que c'est ici :D
                                Intent boardSelectionActivity = new Intent(LoginActivity.this, BoardSelectionActivity.class);
                                startActivity(boardSelectionActivity);

                            } else {
                                try {
                                    setErrorMessage(jsonObject.get("message").toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    };
                    new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "login", credentials, eventNotifier)).start();
                }
                else{
                    setErrorMessage("Veuillez entrer vos informations de connexion");
                }

            }
        });
    }

    private void InitRegisterButton(){
        mRegisterBtn = findViewById(R.id.activity_login_register_btn);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerActivity);
            }
        });
    }

    private void setErrorMessage(final String errorMessage){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(LoginActivity.this,errorMessage, Toast.LENGTH_LONG);
                TextView view = toast.getView().findViewById(android.R.id.message);
                view.setGravity(Gravity.CENTER);
                toast.setGravity(Gravity.CENTER ,0,0);
                toast.show();
            }
        });
    }

}

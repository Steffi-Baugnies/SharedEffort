package com.example.steff.sharedeffort;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class BoardCreationActivity extends AppCompatActivity {
    private EditText mNewBoardName;
    private EditText mNewBoardPswd;
    private EditText mNewBoardPswdConf;
    private Button mBoardCreationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_creation);
        mNewBoardName = findViewById(R.id.activity_boardCreation_boardName);
        mNewBoardPswd = findViewById(R.id.activity_boardCreation_adminPassword);
        mNewBoardPswdConf = findViewById(R.id.activity_boardCreation_adminPassword_confirmation);
        initBoardCreationBtn();
    }

    private void initBoardCreationBtn(){
        mBoardCreationBtn = findViewById(R.id.activity_boardCreation_boardCreation_btn);
        mBoardCreationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String boardName = mNewBoardName.getText().toString();
                String adminPswd = mNewBoardPswd.getText().toString();
                String adminPswdConf = mNewBoardPswdConf.getText().toString();

                if(adminPswd.equals(adminPswdConf)){
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("boardName", boardName);
                        jsonObject.put("adminPswd", adminPswd);
                        jsonObject.put("persId", ConnectedUser.getInstance().getConnectedUser());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    IEventNotifier eventNotifier = new IEventNotifier() {
                        @Override
                        public void RequestComplete(JSONObject jsonObject) {
                            int state = 0;
                            try {
                                state = (int) jsonObject.get("boardCreationStatus");
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
                                Intent familyBoardActivity = new Intent(BoardCreationActivity.this, FamilyBoardActivity.class);
                                startActivity(familyBoardActivity);
                            }
                            else {
                                Toaster toaster = new Toaster(message, BoardCreationActivity.this);
                            }
                        }
                    };
                    new Thread(new ApiRequestHandler("http://10.0.2.2:5000", "createBoard", jsonObject, eventNotifier)).start();

                }
                else {
                    String errorMessage = "Les mots de passe ne correspondent pas, veuillez réessayer";
                    Toast toast = Toast.makeText(BoardCreationActivity.this,errorMessage, Toast.LENGTH_LONG);
                    TextView view = toast.getView().findViewById(android.R.id.message);
                    view.setGravity(Gravity.CENTER);
                    toast.setGravity(Gravity.CENTER ,0,0);
                    toast.show();
                }
            }
        });

    }
}

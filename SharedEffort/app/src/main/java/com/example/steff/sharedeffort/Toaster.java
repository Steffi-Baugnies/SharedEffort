package com.example.steff.sharedeffort;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class Toaster {

    private Toast toast;

    public Toaster(String message, Context context) {
        toast = Toast.makeText(context,message, Toast.LENGTH_LONG);
        TextView view = toast.getView().findViewById(android.R.id.message);
        view.setGravity(Gravity.CENTER);
        toast.setGravity(Gravity.CENTER ,0,0);
    }

    public void showToast(){
        toast.show();
    }
}

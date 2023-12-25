package edu.ubbcluj.cs.groutes.generalhelper;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class MsgHelper {

    public static void exceptionCatchMsg(Exception err, String TAG, Activity context){
        Toast.makeText(context, err.getMessage(), Toast.LENGTH_LONG).show();
        Log.e(TAG, err.getStackTrace().toString());
    }
}

package com.aspiration.bahikhata;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseRole;
import com.parse.SaveCallback;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.util.List;

import de.greenrobot.event.EventBus;


public class StartActivity extends SherlockActivity {

    EditText name,address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        khataApplication.setmContext(this);

        if(HandleData.GetIdentity().size() > 0){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        else{
            setContentView(R.layout.activity_start);
            name = (EditText) findViewById(R.id.name);
            address = (EditText)findViewById(R.id.address);

            name.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void StartBtnClick(View v) {
        if (name.getText().toString().isEmpty())
            name.setError("Required");
        else {
            HandleData.AddIdentity(name.getText().toString(), address.getText().toString());
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}
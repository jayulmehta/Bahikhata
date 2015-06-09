package com.aspiration.bahikhata;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;


public class SettingActivity extends Activity {

    EditText name, address, contact;
    TextView key, expiry;
    //, currency, language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        /*name = (EditText)findViewById(R.id.name);
        address = (EditText)findViewById(R.id.address);
        contact = (EditText)findViewById(R.id.contact);

        key = (TextView)findViewById(R.id.key);
        expiry = (TextView)findViewById(R.id.expiry);

        List<ParseObject> list = HandleData.GetIdentity();
        name.setText(list.get(0).getString("name"));
        address.setText(list.get(0).getString("address"));
        contact.setText(list.get(0).getString("contact"));*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }
}

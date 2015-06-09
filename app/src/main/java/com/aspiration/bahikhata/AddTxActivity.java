package com.aspiration.bahikhata;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import java.text.NumberFormat;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.greenrobot.event.EventBus;


public class AddTxActivity extends SherlockActivity
{
    DateTimeFormatter format = DateTimeFormat.forPattern("dd MMM");
    EditText detail,amount;
    TextView balanceView;
    AutoCompleteTextView name;
    DateTime mydateTime;
    RadioGroup type;
    NumberFormat formatter;
    String amt;
    AlertDialog alert;
    static final int PICK_CONTACT=1;
    static EditText partyname, contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtx);

        name = (AutoCompleteTextView)findViewById(R.id.username);
        detail = (EditText)findViewById(R.id.license);
        amount = (EditText)findViewById(R.id.amount);
        balanceView = (TextView)findViewById(R.id.balanceView);

        formatter = NumberFormat.getCurrencyInstance(new Locale(getResources().getString(R.string.language),
                getResources().getString(R.string.countrycode)));
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(0);

        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    name.setText("");
                    balanceView.setText("");
                } else {
                    if (!name.getText().toString().isEmpty()) {
                        //Check balance of this party and show in balance field.
                        Double balance;
                        try{
                            balance = HandleData.GetPartyBalance(name.getText().toString());
                        }
                        catch (java.lang.NullPointerException e){
                            balance = 0.0;
                        }
                        if(balance!=null)
                            balanceView.setText(formatter.format(balance));
                        else
                            balanceView.setText("");
                    }
                }
            }
        });

        amount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    amount.setText("");
                }
                else{
                    if (!amount.getText().toString().isEmpty()) {
                        amt = amount.getText().toString();
                        amount.setText(formatter.format(Long.valueOf(amt)));
                    }
                }
            }
        });
        type = (RadioGroup)findViewById(R.id.type);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.add_tx_title);

        name.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);

        mydateTime = new DateTime();

        //Autocomplete process to get Name
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item,HandleData.GetPartyNameAll());
        name.setThreshold(1);
        name.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.menu_addtx, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.animator.fadein, R.animator.slideout);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.fadein, R.animator.slideout);
    }

    public void AddAccount(View view){
        //show alert dialog with fields to fill in.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.add_account, null);
        partyname = (EditText)v.findViewById(R.id.name);
        contact = (EditText)v.findViewById(R.id.contact);

        builder.setView(v);
        builder.setPositiveButton(R.string.ok, null);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setTitle(R.string.addacc_msg);
        builder.setCancelable(true);
        alert = builder.create();
        alert.show();

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText address = (EditText) alert.findViewById(R.id.address);

                if (partyname.getText().toString().isEmpty()) {
                    partyname.setError("Required");
                } else {
                    HandleData.AddAccount(partyname.getText().toString(),
                            contact.getText().toString(),
                            address.getText().toString());
                }
            }
        });
    }

    public void AddTx(View v){
        if (amount.getText().toString().isEmpty())
            amount.setError(getResources().getString(R.string.r_msg));
        else if (amt.length()>=10)
            amount.setError(getResources().getString(R.string.e_msg));
        else {
            int selected = type.getCheckedRadioButtonId();
            HandleData.AddTransaction(name.getText().toString(), amt,
                    detail.getText().toString(), new DateTime(), selected);
        }
    }

    public void onEvent(EventType type){
        switch (type.success){
            case 0:
                onBackPressed();
                break;
            case 1:
                Toast.makeText(getApplicationContext(),"Transaction Failed. Retry!!",Toast.LENGTH_SHORT).show();
                break;
            case 2:
                partyname.setError("Account exists");
                break;
            case 3:
                name.setText(partyname.getText().toString());

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item,HandleData.GetPartyNameAll());
                name.setThreshold(1);
                name.setAdapter(adapter);

                amount.requestFocus();
                alert.dismiss();
                break;
        }
    }

    public void PickContactClick(View v){
        partyname.setError(null);

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c =  managedQuery(contactData, null, null, null, null);

                    if (c.moveToFirst()) {
                        String id =c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();
                            String cNumber = phones.getString(phones.getColumnIndex("data1"));
                            //System.out.println("number is:"+cNumber);
                            contact.setText(cNumber);
                        }
                        partyname.setText(c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                    }
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public static class EventType {
        public final int success;

        public EventType(int success) {
            this.success = success;
        }
    }

}
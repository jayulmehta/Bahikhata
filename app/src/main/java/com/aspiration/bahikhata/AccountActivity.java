package com.aspiration.bahikhata;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.shamanland.fonticon.FontIconDrawable;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;

public class AccountActivity extends SherlockActivity{
    NumberFormat formatter;
    TextView totalDisplay;
    DateTimeFormatter format = DateTimeFormat.forPattern("MMM");
    ExpandableListAdapter listAdapter;
    ExpandableListView expandableListView;
    ArrayList<ArrayList<String>> listDataHeader;
    HashMap<ArrayList<String>,List<String>> listDataChild;
    static AutoCompleteTextView partyname;
    HandleData data;
    String amt;
    AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        data = new HandleData();

        /*
        //DateTime dt = new DateTime();
        //date.setText(dt.toString(format));

        //Fill objects into listview.
        customAdapter = new CustomAdapter(this, new DateTime());
        accList.setAdapter(customAdapter);
        customAdapter.loadObjects();*/

        //Money formatter
        formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(0);

        totalDisplay = (TextView)findViewById(R.id.totalBalance);
        totalDisplay.setText(formatter.format(data.GetPartyBalanceAll()));

        //Action Bar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setTitle(getResources().getStringArray(R.array.menuItems)[1]);

        //Expandable ListView
        expandableListView = (ExpandableListView)findViewById(R.id.lvExp);
        prepareDataListing();
        listAdapter = new ExpandableListAdapter(this,listDataHeader,listDataChild);
        expandableListView.setAdapter(listAdapter);

        //Load transactions and show when clicked on a item in listview.
        /*expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {

            }
        });*/
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                data.GetTransactionsByParty(listDataHeader.get(i).get(0));
                return false;
            }
        });

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

    public void onEvent(EventType eventType) {
        if(eventType.success) {
            int size = eventType.txlist.size();
            if(size > 0){
                Log.e("val", String.valueOf(size));
                ArrayList<String> info = new ArrayList<String>();
                for(int i=0;i<size;i++){
                    info.add(String.valueOf(eventType.txlist.get(i).getDate("datetime")));
                }
                listDataChild.put(listDataHeader.get(1), info);
                expandableListView.expandGroup(3);
            }
            else{
                Toast.makeText(getApplicationContext(),"No Transactions",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class EventType {
        public final boolean success;
        public final List<ParseObject> txlist;

        public EventType(boolean success,List<ParseObject> txlist) {
            this.success = success;
            this.txlist = txlist;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.menu_accountview, menu);

        //Drawable icon = FontIconDrawable.inflate(getResources(),R.xml.icon_users);
        //menu.findItem(R.id.addAccount).setIcon(icon);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        /*Intent intent = new Intent(getApplicationContext(), TimeViewActivity.class);
        startActivity(intent);
        overridePendingTransition(R.animator.fadein, R.animator.slideoutup);*/
    }

    public void AddTxClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.add_txn, null);

        builder.setView(v);
        builder.setPositiveButton(R.string.ok, null);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setTitle(R.string.add_tx_title);
        builder.setCancelable(true);
        alert = builder.create();
        alert.show();

        partyname = (AutoCompleteTextView)((AlertDialog)alert).findViewById(R.id.name);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item,data.GetPartyNameAll());
        partyname.setThreshold(1);
        partyname.setAdapter(adapter);

        final EditText amount = (EditText)alert.findViewById(R.id.amount);
        amount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    amount.setText("");
                } else {
                    if (!amount.getText().toString().isEmpty()) {
                        amt = amount.getText().toString();
                        amount.setText(formatter.format(Long.valueOf(amt)));
                    }
                }
            }
        });

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText purpose = (EditText) ((AlertDialog) alert).findViewById(R.id.purpose);
                RadioGroup txtype = (RadioGroup) ((AlertDialog) alert).findViewById(R.id.type);

                if (partyname.getText().toString().isEmpty())
                    partyname.setError(getResources().getString(R.string.r_msg));
                else if (amount.getText().toString().isEmpty())
                    amount.setError(getResources().getString(R.string.r_msg));
                else if(amt.length()>=10)
                    amount.setError(getResources().getString(R.string.e_msg));
                else
                {
                    data.AddTransaction(partyname.getText().toString(),
                            amt,
                            purpose.getText().toString(),
                            new DateTime(), txtype.getCheckedRadioButtonId());
                }
            }
        });
    }

    public void prepareDataListing(){
        listDataHeader = new ArrayList<ArrayList<String>>();
        listDataChild = new HashMap<ArrayList<String>,List<String>>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Party");
        query.addAscendingOrder("name");
        query.whereNotEqualTo("name", "");
        query.fromLocalDatastore();
        query.selectKeys(Arrays.asList("name", "balance"));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                int size = list.size();
                for (int i = 0; i < size; i++) {
                    ArrayList<String> arrayList = new ArrayList<String>();
                    arrayList.add(0, list.get(i).getString("name"));
                    arrayList.add(1,String.valueOf(list.get(i).getDouble("balance")));
                    listDataHeader.add(arrayList);

                    //Get Transactions with partyname.
                    List<String> info = new ArrayList<String>();
                    info.add("Divyanshu Goel");

                    listDataChild.put(listDataHeader.get(i), info);
                }
            }
        });
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private ArrayList<ArrayList<String>> _listDataHeader;
        private HashMap<ArrayList<String>, List<String>> _listDataChild;

        public ExpandableListAdapter(Context context,ArrayList<ArrayList<String>> _listDataHeader,
                                     HashMap<ArrayList<String>,List<String>> _listDataChild){
            this.context = context;
            this._listDataChild = _listDataChild;
            this._listDataHeader  = _listDataHeader;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            final String childText = (String) getChild(i,i1);
            if(view == null){
                LayoutInflater infalInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = infalInflater.inflate(R.layout.list_item, null);
            }
            /*TextView txtListChild = (TextView) view
                    .findViewById(R.id.lblListItem);

            txtListChild.setText(childText);*/
            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return this._listDataChild.get(this._listDataHeader.get(i)).size();
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public Object getChild(int i, int i1) {
            return this._listDataChild.get(this._listDataHeader.get(i)).get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public Object getGroup(int i) {
            return this._listDataHeader.get(i);
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            //Log.e("Value",getGroup(i).toString());
            ArrayList<String> headerTitle = (ArrayList<String>)getGroup(i);
            if(view == null){
                LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_group,null);
            }
            TextView listListHeader = (TextView)view.findViewById(R.id.name);
            listListHeader.setText(headerTitle.get(0));

            TextView listListHeaderBalance = (TextView)view.findViewById(R.id.balance);
            listListHeaderBalance.setText(formatter.format(Double.valueOf(headerTitle.get(1))));

            return view;
        }
    }

}

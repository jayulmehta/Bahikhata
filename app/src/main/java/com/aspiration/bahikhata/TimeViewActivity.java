package com.aspiration.bahikhata;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * Created by abhi on 11/03/15.
 */
public class TimeViewActivity extends SherlockFragmentActivity {

    //Declare Variables
    ListView list_txn;
    DateTimeFormatter format = DateTimeFormat.forPattern("dd MMM");
    static DateTime dt;
    static NumberFormat formatter;
    ViewPager viewpager;
    HandleData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        data = new HandleData();

        //Setting Today Date
        if(dt == null)
            dt = new DateTime();

        CustomPagerAdapter customPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());
        viewpager = (ViewPager)findViewById(R.id.pager);
        viewpager.setAdapter(customPagerAdapter);

    }

    //Delete Transaction
    public AdapterView.OnItemLongClickListener deleteTx = new AdapterView.OnItemLongClickListener(){
        @Override
        public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position, long l) {
            AlertDialog.Builder builder = new AlertDialog.Builder(TimeViewActivity.this);

            builder.setMessage(R.string.A_msg);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    data.DeleteTransaction((ParseObject)adapterView.getItemAtPosition(position));

                    /*if(){
                        Intent intent = new Intent(getApplicationContext(),TimeViewActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.animator.fadein,R.animator.fadeout);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Error while Deleting",Toast.LENGTH_LONG).show();
                    }*/
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setCancelable(true);
            builder.show();
            return false;
        }
    };

    public boolean onCreateOptionsMenu(Menu menu) {

        getSupportMenuInflater().inflate(R.menu.menu_timeview, menu);

        //Draw a calendar with today's date & month inside it.
        Drawable drawable = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                Paint paint = new Paint();
                paint.setARGB(255, 255, 255, 255);
                paint.setTextSize(16);
                paint.setTextAlign(Paint.Align.CENTER);

                canvas.drawText(dt.toString(format).split(" ")[0],0,0,paint);
                canvas.drawText(dt.toString(format).split(" ")[1],0,16,paint);
            }

            @Override
            public void setAlpha(int i) {

            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return 0;
            }

        };

        //menu.findItem(R.id.pickDate).setIcon(FontIconDrawable.inflate(getResources(), R.xml.icon_calendar));
        menu.findItem(R.id.pickDate).setIcon(drawable);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Check if options Item Selected... if yes.... show menu...
        /*if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }*/

        switch (item.getItemId()) {
            case R.id.pickDate:
                PickDateClick(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
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

    //Setting set date to Activity
    public void onEvent(DateEvent event) {

        dt = new DateTime(event.year, event.monthOfYear + 1, event.dayOfMonth, dt.getHourOfDay(), dt.getMinuteOfHour());
        CustomAdapter adpater = new CustomAdapter(this, dt,"db");

        //list_txn.setAdapter(adpater);
        adpater.loadObjects();
    }

    public void PickDateClick(View v) {
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getFragmentManager(), "datePicker");
    }

    //Add a new transaction.
    public void AddTxClick(View v) {
        Intent i = new Intent(getApplicationContext(), AddTxActivity.class);
        startActivity(i);
        overridePendingTransition(R.animator.slidein, R.animator.fadeout);
    }

    //Hardware keyboard back button pressed.
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public static class DateEvent {
        public final int year;
        public final int monthOfYear;
        public final int dayOfMonth;

        public DateEvent(int dayOfMonth, int monthOfYear, int year) {
            this.dayOfMonth = dayOfMonth;
            this.monthOfYear = monthOfYear;
            this.year = year;
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            DateTime dt = new DateTime();
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, dt.getYear(), dt.getMonthOfYear() + 1, dt.getDayOfMonth());
            dialog.getDatePicker().setMaxDate(new Date().getTime());
            return dialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            EventBus.getDefault().post(new DateEvent(dayOfMonth, monthOfYear, year));
        }
    }

    public static class CustomAdapter extends ParseQueryAdapter<ParseObject> {

        public CustomAdapter(Context context, final DateTime Date,final String type) {
            super(context, new QueryFactory<ParseObject>() {
                public ParseQuery create() {

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Transaction");
                    query.fromLocalDatastore();
                    //use when multiple data featch
                    query.include("parent");
                    query.whereGreaterThanOrEqualTo("datetime", new DateTime(Date.getYear(), Date.getMonthOfYear(), Date.getDayOfMonth(), 0, 0).toDate());
                    query.whereLessThanOrEqualTo("datetime", new DateTime(Date.getYear(), Date.getMonthOfYear(), Date.getDayOfMonth(), 23, 59).toDate());
                    query.orderByDescending("datetime");
                    query.whereEqualTo("type",type);
                    return query;
                }
            });
        }

        @Override
        public View getItemView(ParseObject object, View v, ViewGroup parent) {
            if (v == null) {
                v = View.inflate(getContext(), R.layout.list_entry, null);
            }
            super.getItemView(object, v, parent);


            //Money formatter
            formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            formatter.setMinimumFractionDigits(0);
            formatter.setMaximumFractionDigits(0);

            TextView amount = (TextView) v.findViewById(R.id.amount);
            TextView user = (TextView) v.findViewById(R.id.username);
            TextView detail = (TextView) v.findViewById(R.id.license);

            //multiple quares
            ParseObject parseObject = object.getParseObject("parent");
            user.setText(parseObject.getString("name"));
            detail.setText(object.getString("detail"));
            Double balance = object.getNumber("amount").doubleValue();
            amount.setText(formatter.format(balance));

            return v;
        }
    }

    public class CustomPagerAdapter extends FragmentStatePagerAdapter{
        public CustomPagerAdapter(android.support.v4.app.FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment;
            if(i==0)
                fragment = new DebitFragment();
            else
                fragment = new CreditFragment();
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position == 0) return " DEBIT ";
            else return " CREDIT ";
        }
    }
    public static class DebitFragment extends Fragment{

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_txns, container, false);

            //Show list of transactions
            ListView list_txn = (ListView)rootView.findViewById(R.id.list);
            CustomAdapter customAdapter = new CustomAdapter(khataApplication.getmContext(),TimeViewActivity.dt,"db");

            list_txn.setAdapter(customAdapter);
            customAdapter.loadObjects();
            //list_txn.setOnItemLongClickListener(deleteTx);

            return rootView;
        }
    }
    public static class CreditFragment extends Fragment{

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_txns,container,false);

            ListView list_txn = (ListView)rootView.findViewById(R.id.list);
            CustomAdapter adapter = new CustomAdapter(khataApplication.getmContext(),TimeViewActivity.dt,"cr");

            list_txn.setAdapter(adapter);
            adapter.loadObjects();

            return rootView;
        }
    }

}
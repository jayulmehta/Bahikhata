package com.aspiration.bahikhata;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.helpshift.Helpshift;
import com.parse.ParseObject;
import com.shamanland.fonticon.FontIconDrawable;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;


public class MainActivity extends SherlockFragmentActivity{

    DrawerLayout drawerLayout;
    ListView drawerList;
    ActionBarDrawerToggle drawerToggle;
    ActionBar actionBar;
    static DateTime dt;
    Menu menu;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting ActionBar
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);

        dt = new DateTime();

        drawerList = (ListView)findViewById(R.id.listview_drawer);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item,
                //menuItems));
                getResources().getStringArray(R.array.menuItems)));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.drawable.ic_drawer,
                R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                actionBar.setTitle("Select");
                //invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        //getSupportMenuInflater().inflate(R.menu.menu_main, menu);

        this.menu = menu;
        getSupportMenuInflater().inflate(R.menu.menu_main, menu);

        //Draw a calendar with today's date & month inside it.
        Drawable drawable = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                DateTimeFormatter format = DateTimeFormat.forPattern("dd MMM");
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

        menu.findItem(R.id.pickDate).setIcon(drawable);
        menu.findItem(R.id.addAccount).setIcon(FontIconDrawable.inflate(getResources(), R.xml.icon_users));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(drawerLayout.isDrawerOpen(drawerList)){
                    drawerLayout.closeDrawer(drawerList);
                }
                else{
                    drawerLayout.openDrawer(drawerList);
                }
                return true;
            case R.id.pickDate:
                PickDateClick(null);
                return true;
            case R.id.addAccount:
                AddTxClick(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(com.actionbarsherlock.view.Menu menu) {

        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerList);
        menu.findItem(R.id.pickDate).setVisible(!drawerOpen);
        menu.findItem(R.id.addAccount).setVisible(!drawerOpen);

        return super.onPrepareOptionsMenu(menu);
    }

    public void showOverflowMenu(int showMenu){
        if(menu == null)
            return;
        switch (showMenu) {
            case 0:
                menu.setGroupVisible(R.id.rojmelmenu,true);
                menu.setGroupVisible(R.id.khatamenu, false);
                break;
            case 1:
                menu.setGroupVisible(R.id.rojmelmenu,false);
                menu.setGroupVisible(R.id.khatamenu, true);
                break;
        }
    }

    public void selectItem(int position){
        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        switch (position){
            case 0:
                ft.replace(R.id.content_frame, new TimeFragment());
                actionBar.setTitle(getResources().getStringArray(R.array.menuItems)[0]);
                showOverflowMenu(0);
                break;
            case 1:
                ft.replace(R.id.content_frame,new AccountFragment());
                actionBar.setTitle(getResources().getStringArray(R.array.menuItems)[1]);
                showOverflowMenu(1);
                break;
            case 2:
                Helpshift.showFAQs(this);
                overridePendingTransition(R.animator.slidein, R.animator.fadeout);

                //ft.replace(R.id.content_frame,new HelpFragment());
                //actionBar.setTitle(getResources().getStringArray(R.array.menuItems)[2]);
                //showOverflowMenu(2);
                break;
            case 3:
                ft.replace(R.id.content_frame,new SettingFragment());
                actionBar.setTitle(getResources().getStringArray(R.array.menuItems)[1]);
                showOverflowMenu(2);

                /*Intent i = new Intent(this, SettingActivity.class);
                startActivity(i);
                overridePendingTransition(R.animator.slidein, R.animator.fadeout);*/

                break;
        }
        ft.commit();

        drawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerList);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            selectItem(i);
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //Add a new transaction.
    public void AddTxClick(View v) {
        Intent i = new Intent(this, AddTxActivity.class);
        startActivity(i);
        overridePendingTransition(R.animator.slidein, R.animator.fadeout);
    }

    public void OnEditClick(View v){
        View rootview = (View)v.getParent();
        if(v.getId() == R.id.nameEdit){
            final EditText name = (EditText)rootview.findViewById(R.id.name);
            name.setEnabled(true);
            name.requestFocus();
            name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {

                    } else {
                        name.setEnabled(false);
                        //Save the changed identity.
                        HandleData.AddIdentity(name.getText().toString(),null);
                    }
                }
            });
        }
        else if(v.getId() == R.id.addressEdit){
            final EditText address = (EditText)rootview.findViewById(R.id.address);
            address.setEnabled(true);
            address.requestFocus();
            address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {

                    } else {
                        address.setEnabled(false);
                        //Save the changed identity.
                        HandleData.AddIdentity(null,address.getText().toString());
                    }
                }
            });
        }
        else if(v.getId() == R.id.contactEdit){
            final EditText contact = (EditText)rootview.findViewById(R.id.contact);
            contact.setEnabled(true);
            contact.requestFocus();
            contact.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {

                    } else {
                        contact.setEnabled(false);
                        //Save the changed identity.
                        HandleData.AddIdentity(null,null);
                    }
                }
            });
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

    public static class EventType {
        public final boolean success;

        public EventType(boolean success) {
            this.success = success;
        }
    }

    //Setting set date to Activity
    public void onEvent(DateEvent event) {
        dt = new DateTime(event.year, event.monthOfYear + 1, event.dayOfMonth, dt.getHourOfDay(), dt.getMinuteOfHour());
        selectItem(0);
        invalidateOptionsMenu();
    }

    public void PickDateClick(View v) {
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getFragmentManager(), "datePicker");
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
}

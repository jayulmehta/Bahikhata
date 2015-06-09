package com.aspiration.bahikhata;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import org.joda.time.DateTime;

import java.text.NumberFormat;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimeFragment extends SherlockFragment {

    static NumberFormat formatter;

    ViewPager viewpager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_time, container, false);

        CustomPagerAdapter customPagerAdapter = new CustomPagerAdapter(getFragmentManager());
        viewpager = (ViewPager)rootView.findViewById(R.id.pager);
        viewpager.setAdapter(customPagerAdapter);

        formatter = NumberFormat.getCurrencyInstance(new Locale(getResources().getString(R.string.language),
                getResources().getString(R.string.countrycode)));
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(0);

        return rootView;
    }

    public static class CustomAdapter extends ParseQueryAdapter<ParseObject> {

        public CustomAdapter(Context context, final DateTime Date,final String type) {
            super(context, new QueryFactory<ParseObject>() {
                public ParseQuery create() {

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Transaction");
                    query.fromLocalDatastore();
                    //use when multiple data fetch
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

            ParseObject parseObject = object.getParseObject("parent");
            user.setText(parseObject.getString("name"));
            detail.setText(object.getString("detail"));
            Double balance = object.getNumber("amount").doubleValue();
            amount.setText(formatter.format(balance));

            return v;
        }
    }

    public class CustomPagerAdapter extends FragmentStatePagerAdapter {
        public CustomPagerAdapter(android.support.v4.app.FragmentManager fm){
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int i) {
            android.support.v4.app.Fragment fragment;
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
            String currency;
            if(position == 0){
                currency = formatter.format(HandleData.GetDateWiseBalance(MainActivity.dt, "db"));
                return "DEBIT ( " + currency + " )";
            }
            else{
                currency = formatter.format(HandleData.GetDateWiseBalance(MainActivity.dt, "cr"));
                return "CREDIT ( " + currency + " )";
            }
        }
    }

    public static class DebitFragment extends android.support.v4.app.Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_txns, container, false);

            //Show list of transactions
            ListView list_txn = (ListView)rootView.findViewById(R.id.list);
            CustomAdapter customAdapter = new CustomAdapter(khataApplication.getmContext(),MainActivity.dt,"db");

            list_txn.setAdapter(customAdapter);
            customAdapter.loadObjects();
            //list_txn.setOnItemLongClickListener(deleteTx);

            return rootView;
        }
    }

    public static class CreditFragment extends android.support.v4.app.Fragment {

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_txns,container,false);

            ListView list_txn = (ListView)rootView.findViewById(R.id.list);
            CustomAdapter adapter = new CustomAdapter(khataApplication.getmContext(),MainActivity.dt,"cr");

            list_txn.setAdapter(adapter);
            adapter.loadObjects();

            return rootView;
        }
    }

    //Delete Transaction
    public AdapterView.OnItemLongClickListener deleteTx = new AdapterView.OnItemLongClickListener(){
        @Override
        public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position, long l) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());

            builder.setMessage(R.string.A_msg);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    HandleData.DeleteTransaction((ParseObject)adapterView.getItemAtPosition(position));

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
}
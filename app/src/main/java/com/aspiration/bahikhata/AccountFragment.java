package com.aspiration.bahikhata;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.parse.ParseObject;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AccountFragment extends SherlockFragment{
    NumberFormat formatter;
    TextView totalDisplay;
    ExpandableListAdapter listAdapter;
    ExpandableListView expandableListView;
    ArrayList<ArrayList<String>> listDataHeader;
    HashMap<ArrayList<String>,ArrayList<ArrayList<String>>> listDataChild;
    int previousGroup = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_account, container, false);

        formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(0);

        totalDisplay = (TextView)rootView.findViewById(R.id.totalBalance);
        totalDisplay.setText(formatter.format(HandleData.GetPartyBalanceAll()));

        //Expandable ListView
        expandableListView = (ExpandableListView)rootView.findViewById(R.id.lvExp);

        prepareDataListing();
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {
                if(i != previousGroup){
                    expandableListView.collapseGroup(previousGroup);
                    previousGroup = i;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public static class EventType {
        public final boolean success;
        public final List<ParseObject> txlist;

        public EventType(boolean success,List<ParseObject> txlist) {
            this.success = success;
            this.txlist = txlist;
        }
    }

    public void prepareDataListing() {
        listDataHeader = new ArrayList<ArrayList<String>>();
        listDataChild = new HashMap<ArrayList<String>, ArrayList<ArrayList<String>>>();

        List<ParseObject> list = HandleData.GetPartyNameBalanceAll();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(0, list.get(i).getString("name"));
            arrayList.add(1, String.valueOf(list.get(i).getDouble("balance")));
            listDataHeader.add(arrayList);

            //Get Transactions with partyname.
            List<ParseObject> txs = HandleData.GetTransactionsByParty(list.get(i).getString("name"));
            ArrayList<ArrayList<String>> info = new ArrayList<ArrayList<String>>();
            for (int j = 0; j < txs.size(); j++) {
                ArrayList<String> arrayList1 = new ArrayList<String>();
                arrayList1.add(0,String.valueOf(txs.get(j).getDate("datetime")));
                arrayList1.add(1,txs.get(j).getString("detail"));
                arrayList1.add(2,String.valueOf(txs.get(j).getDouble("amount")));
                info.add(arrayList1);
            }
            listDataChild.put(listDataHeader.get(i), info);
        }

        listAdapter = new ExpandableListAdapter(getActivity().getApplicationContext(),listDataHeader,listDataChild);
        expandableListView.setAdapter(listAdapter);
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private ArrayList<ArrayList<String>> _listDataHeader;
        private HashMap<ArrayList<String>,ArrayList<ArrayList<String>>> _listDataChild;

        public ExpandableListAdapter(Context context,ArrayList<ArrayList<String>> _listDataHeader,
                                     HashMap<ArrayList<String>,ArrayList<ArrayList<String>>> _listDataChild){
            this.context = context;
            this._listDataChild = _listDataChild;
            this._listDataHeader  = _listDataHeader;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            ArrayList<String> childText = (ArrayList<String>)getChild(i, i1);
            if(view == null){
                LayoutInflater infalInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = infalInflater.inflate(R.layout.list_item, null);
            }
            TextView amount = (TextView) view.findViewById(R.id.amount);
            TextView detail = (TextView) view.findViewById(R.id.license);
            TextView date = (TextView) view.findViewById(R.id.date);

            DateTimeFormatter format = DateTimeFormat.forPattern("dd MMM");
            DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("EEE MMM dd");

            amount.setText(formatter.format(Double.valueOf(childText.get(2))));
            detail.setText(childText.get(1));

            //Invalid format: "Fri May 29 13:45:36 GMT+05:30 2015" is malformed at "+05:30 2015"
            //What if the format changes... in between?
            String[] dateTexts = childText.get(0).split(" ");

            date.setText(dateTimeFormatter.parseDateTime(dateTexts[0]+ " " + dateTexts[1]+ " " + dateTexts[2]).toString(format));

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

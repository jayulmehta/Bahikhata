package com.aspiration.bahikhata;

import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;

public class HandleData {

    public HandleData(){
    }

    public static void AddIdentity(String name,String address){

        //If exists then update.. else add new.
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Identity");
        query.fromLocalDatastore();
        List<ParseObject> list;
        try {
            list = query.find();
        }
        catch (ParseException e){
            list = null;
        }
        if(list == null || list.isEmpty()){
            ParseObject identity = new ParseObject("Identity");
            identity.put("name", name);
            identity.put("address", address);
            identity.pinInBackground();
        }
        else{
            ParseObject identity = (ParseObject)list.get(0);
            if(name != null)    identity.put("name", name);
            if(address != null) identity.put("address", address);
            identity.pinInBackground();
        }
    }

    public static void AddAccount(final String name,final String contact,final String address){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Party");
        query.fromLocalDatastore();
        query.selectKeys(Arrays.asList("name"));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                int i = 0, size = ((list == null) ? 0 : list.size());
                for (i = 0; i < size; i++) {
                    if (list.get(i).getString("name").equals(name)) {
                        EventBus.getDefault().post(new AddTxActivity.EventType(2));
                        return;
                    }
                }

                ParseObject parseObject = new ParseObject("Party");
                parseObject.put("name", name);
                parseObject.put("contact", contact);
                parseObject.put("address", address);
                parseObject.put("balance", 0);
                parseObject.pinInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        EventBus.getDefault().post(new AddTxActivity.EventType(3));
                    }
                });
            }
        });
    }

    public static void AddTransaction(final String name,final String  amount,final String detail, DateTime dateTime,int selected){
        final ParseObject transaction = new ParseObject("Transaction");
        transaction.put("detail", detail);
        transaction.put("datetime", dateTime.toDate());

        //Remove white spaces in the name.
        final String t_name=name.trim();

        final String type;
        if (selected == R.id.credit) {
            type = "cr";
            transaction.put("type", "cr");
            transaction.put("amount", -Double.valueOf(amount));
        } else {
            type = "db";
            transaction.put("type", "db");
            transaction.put("amount", Double.valueOf(amount));
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Party");
        query.fromLocalDatastore();
        query.whereEqualTo("name", t_name);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, com.parse.ParseException e) {
                if (parseObject == null) {
                    ParseObject party = new ParseObject("Party");
                    party.put("name", t_name);
                    if (type.equals("cr"))
                        party.put("balance", -Double.valueOf(amount));
                    else
                        party.put("balance", Double.valueOf(amount));
                    party.pinInBackground();
                    transaction.put("parent", party);
                    transaction.pinInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            if(e == null)
                                EventBus.getDefault().post(new AddTxActivity.EventType(0));
                            else
                                EventBus.getDefault().post(new AddTxActivity.EventType(1));
                        }
                    });
                } else {
                    if (type.equals("cr"))
                        parseObject.increment("balance", -Double.valueOf(amount));
                    else
                        parseObject.increment("balance", Double.valueOf(amount));

                    parseObject.pinInBackground();
                    transaction.put("parent", parseObject);
                    transaction.pinInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            if(e == null)
                                EventBus.getDefault().post(new AddTxActivity.EventType(0));
                            else
                                EventBus.getDefault().post(new AddTxActivity.EventType(1));
                        }
                    });
                }
            }
        });
    }

    public static List<ParseObject> GetIdentity(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Identity");
        query.fromLocalDatastore();
        List<ParseObject> list;
        try {
            list = query.find();
        }
        catch (ParseException e){
            list = null;
        }
        return list;
    }

    public void GetPartyAll(){

    }

    public static Double GetPartyBalance(String name){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Party");
        query.whereEqualTo("name", name);
        query.fromLocalDatastore();
        query.selectKeys(Arrays.asList("balance"));
        List<ParseObject> results;
        try
        {
            results = query.find();
        }
        catch (com.parse.ParseException e){
            results = Collections.emptyList();
            return null;
        }
        if(results.size() == 0) return null;
        return results.get(0).getNumber("balance").doubleValue();
    }

    public static Double GetPartyBalanceAll(){
        Double total = 0.0;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Party");
        query.fromLocalDatastore();
        query.selectKeys(Arrays.asList("balance"));
        List<ParseObject> results;
        try {
            results = query.find();
        } catch (com.parse.ParseException e) {
            results = Collections.emptyList();
            e.printStackTrace();
        }
        for (int i = 0; i < results.size(); i++) {
            total += results.get(i).getNumber("balance").doubleValue();
        }
        return total;
    }

    public static Double GetDateWiseBalance(DateTime dt,String type){
        Double total = 0.0;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Transaction");
        query.fromLocalDatastore();
        //use when multiple data fetch
        DateTime Date = MainActivity.dt;
        query.whereGreaterThanOrEqualTo("datetime", new DateTime(Date.getYear(), Date.getMonthOfYear(), Date.getDayOfMonth(), 0, 0).toDate());
        query.whereLessThanOrEqualTo("datetime", new DateTime(Date.getYear(), Date.getMonthOfYear(), Date.getDayOfMonth(), 23, 59).toDate());
        query.whereEqualTo("type", type);
        query.selectKeys(Arrays.asList("amount"));
        List<ParseObject> results;
        try{
            results = query.find();
        }
        catch (ParseException e){
            results = Collections.emptyList();
            e.printStackTrace();
        }
        int size = results.size();
        for (int i = 0; i < size; i++) {
            total += results.get(i).getNumber("amount").doubleValue();
        }
        return total;
    }

    public static String[] GetPartyNameAll(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Party");
        query.fromLocalDatastore();
        query.selectKeys(Arrays.asList("name"));
        List<ParseObject> results;
        try
        {
            results = query.find();
        }
        catch (com.parse.ParseException e){
            results = Collections.emptyList();
            e.printStackTrace();
        }

        int size=results.size();
        String[] names = new String[size];
        for(int i=0;i<size;i++){
            names[i] = results.get(i).getString("name");
        }
        return names;
    }

    public static List<ParseObject> GetPartyNameBalanceAll(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Party");
        query.addAscendingOrder("name");
        query.whereNotEqualTo("name", "");
        query.fromLocalDatastore();
        query.selectKeys(Arrays.asList("name", "balance"));
        List<ParseObject> results;
        try
        {
            results = query.find();
        }
        catch (com.parse.ParseException e){
            results = Collections.emptyList();
            e.printStackTrace();
        }

        return results;
    }

    public static List<ParseObject> GetTransactionsByParty(String name){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Party");
        query.fromLocalDatastore();
        query.whereEqualTo("name", name);

        ParseQuery<ParseObject> txquery = ParseQuery.getQuery("Transaction");
        txquery.fromLocalDatastore();
        txquery.whereMatchesQuery("parent", query);
        txquery.orderByDescending("datetime");
        List<ParseObject> results;
        try {
            results = txquery.find();
        } catch (com.parse.ParseException e) {
            results = Collections.emptyList();
            e.printStackTrace();
        }

        return results;
    }

    public static void DeleteTransaction(final ParseObject obj){
        final Double amount = obj.getDouble("amount");
        final String type =  obj.getString("type");
        final ParseObject parent= obj.getParseObject("parent");

        //Delete from Party
        if (type.equals("cr"))
            parent.increment("balance", -amount);
        else
            parent.increment("balance", -amount);
        parent.pinInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                obj.unpinInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        //Reload list of transaction

                    }
                });
            }
        });
    }
}

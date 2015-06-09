package com.aspiration.bahikhata;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.parse.Parse;
import com.parse.ParseObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SettingFragment extends SherlockFragment {

    EditText name, address, contact;
    TextView key, expiry;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_setting, container, false);

        name = (EditText)rootView.findViewById(R.id.name);
        address = (EditText)rootView.findViewById(R.id.address);
        contact = (EditText)rootView.findViewById(R.id.contact);

        key = (TextView)rootView.findViewById(R.id.key);
        expiry = (TextView)rootView.findViewById(R.id.expiry);

        List<ParseObject> list = HandleData.GetIdentity();
        ParseObject parseObject = (ParseObject)list.get(0);

        name.setText(parseObject.getString("name"));
        address.setText(parseObject.getString("address"));
        contact.setText("+91-8487959825");

        return rootView;
    }

}

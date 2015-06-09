package com.aspiration.bahikhata;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.helpshift.Helpshift;

public class HelpFragment extends SherlockFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.fragment_help, container, false);



        /*

        Button helpBtn = (Button)rootView.findViewById(R.id.helpBtn);
        Button issueBtn = (Button)rootView.findViewById(R.id.IssueBtn);

        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helpshift.showFAQs(getActivity());
            }
        });

        issueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helpshift.showConversation(getActivity());
            }
        });*/

        return null;
    }


}

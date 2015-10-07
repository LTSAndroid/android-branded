package com.pixelmags.android.pixelmagsapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pixelmags.android.pixelmagsapp.R;

/**
 * Created by Annie on 29/09/15.
 */
public class UserAccountFragment extends Fragment {
    private TextView mUserid;
    private TextView mEmailView;
    private TextView mName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // set the Logout Listener
        View rootView = inflater.inflate(R.layout.fragment_useraccount, container, false);
        Button button = (Button) rootView.findViewById(R.id.LOGOUT);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //logout current user

                startActivity(new Intent(getActivity(), LoginActivity.class));
                do_Logout();

            }
        });


        mEmailView = (TextView) rootView.findViewById(R.id.accountEmailid);
        mName = (TextView) rootView.findViewById(R.id.accountName);
        mUserid = (TextView) rootView.findViewById(R.id.accountuserid);
        return rootView;
    }


    public void do_Logout() {



    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }
}


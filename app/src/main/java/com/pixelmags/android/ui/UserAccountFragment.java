package com.pixelmags.android.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.UserPrefs;
import com.pixelmags.android.util.Util;

/**
 * Created by Annie on 29/09/15.
 */
public class UserAccountFragment extends Fragment {

    private TextView dataUserId;
    private TextView dataEmail;
    private TextView dataFullName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // set the Logout Listener
        View rootView = inflater.inflate(R.layout.fragment_useraccount, container, false);

        dataUserId = (TextView) rootView.findViewById(R.id.dataUserId);
        dataUserId.setText(UserPrefs.getUserPixelmagsId());

        dataEmail = (TextView) rootView.findViewById(R.id.dataUserEmail);
        dataEmail.setText(UserPrefs.getUserEmail());

        dataFullName = (TextView) rootView.findViewById(R.id.dataUserFullName);
        String fullName = UserPrefs.getUserFirstName() +" "+UserPrefs.getUserLastName();
        dataFullName.setText(fullName);


        Button button = (Button) rootView.findViewById(R.id.accountLogout);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //logout current user

                //startActivity(new Intent(getActivity(), LoginActivity.class));
                do_Logout();

            }
        });

        // populate with user account info

        return rootView;
    }


    public void do_Logout() {

        Util.doAllLogoutSteps();

        Fragment fragmentLogin = new LoginFragment();
        FragmentManager loginFragmentManager = getFragmentManager();
        loginFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, fragmentLogin)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();


    }


}


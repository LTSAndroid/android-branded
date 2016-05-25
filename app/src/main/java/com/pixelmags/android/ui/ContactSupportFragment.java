package com.pixelmags.android.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.pixelmagsapp.R;

public class ContactSupportFragment extends Fragment {

    public ContactSupportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Contact PixelMags");
        intent.putExtra(Intent.EXTRA_TEXT, "Title: "+ Config.Magazine_Title +", Bundle ID: "+ Config.Bundle_ID +", App Version: "+Config.Version);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Config.Support_EMail});

        Intent mailer = Intent.createChooser(intent, null);
        getActivity().startActivity(mailer);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_support, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


}

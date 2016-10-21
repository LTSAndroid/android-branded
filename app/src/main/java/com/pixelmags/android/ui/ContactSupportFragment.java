package com.pixelmags.android.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.UserPrefs;

public class ContactSupportFragment extends Fragment {

    private String TAG = "ContactSupport";

    public ContactSupportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Contact PixelMags");
            intent.putExtra(Intent.EXTRA_TEXT, "Title : "+ Config.Magazine_Title +"\nBundle ID : "+ Config.Bundle_ID +"\nApp Version : "
                    +Config.Version +"\nDevice Id : "+ UserPrefs.getDeviceID()+"\nMagazine ID : "+Config.Magazine_Number+
                    "\nSystem Version : "+ Build.VERSION.SDK_INT);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Config.Support_EMail});

            Intent mailer = Intent.createChooser(intent, null);
            startActivity(mailer);
        }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_support, container, false);

//        if(savedInstanceState == null){
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("message/rfc822");
//            intent.putExtra(Intent.EXTRA_SUBJECT, "Contact PixelMags");
//            intent.putExtra(Intent.EXTRA_TEXT, "Title : "+ Config.Magazine_Title +"\nBundle ID : "+ Config.Bundle_ID +"\nApp Version : "
//                    +Config.Version +"\nDevice Id : "+ UserPrefs.getDeviceID()+"\nMagazine ID : "+Config.Magazine_Number+
//                    "\nSystem Version : "+ Build.VERSION.SDK_INT);
//            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Config.Support_EMail});
//
//            Intent mailer = Intent.createChooser(intent, null);
//            getActivity().startActivity(mailer);
//        }


        return view;

    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //Restore the fragment's state here
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Contact PixelMags");
            intent.putExtra(Intent.EXTRA_TEXT, "Title : "+ Config.Magazine_Title +"\nBundle ID : "+ Config.Bundle_ID +"\nApp Version : "
                    +Config.Version +"\nDevice Id : "+ UserPrefs.getDeviceID()+"\nMagazine ID : "+Config.Magazine_Number+
                    "\nSystem Version : "+ Build.VERSION.SDK_INT);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Config.Support_EMail});

            Intent mailer = Intent.createChooser(intent, null);
            startActivity(mailer);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's state here
    }



//    @Override
//    public void onDetach() {
//        super.onDetach();
//    }


    @Override
    public void onResume() {

        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                    // handle back button
                    Fragment fragment = new AllIssuesFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, fragment, "All Issues")
                            .commit();

                    return true;

                }

                return false;
            }
        });

    }
}

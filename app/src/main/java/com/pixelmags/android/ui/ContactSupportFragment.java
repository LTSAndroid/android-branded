package com.pixelmags.android.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.UserPrefs;
import com.pixelmags.android.ui.uicomponents.CustomBottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class ContactSupportFragment extends Fragment {

    private String TAG = "ContactSupport";

    public ContactSupportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null){

            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+Config.Support_EMail));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Contact PixelMags");
            intent.putExtra(Intent.EXTRA_TEXT, "Title : "+ Config.Magazine_Title +"\nBundle ID : "+ Config.Bundle_ID +"\nApp Version : "
                    +Config.Version +"\nDevice Id : "+ UserPrefs.getDeviceID()+"\nMagazine ID : "+Config.Magazine_Number+
                    "\nSystem Version : "+ Build.VERSION.SDK_INT);
            startActivity(intent);

            /* Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Contact PixelMags");
            intent.putExtra(Intent.EXTRA_TEXT, "Title : "+ Config.Magazine_Title +"\nBundle ID : "+ Config.Bundle_ID +"\nApp Version : "
                    +Config.Version +"\nDevice Id : "+ UserPrefs.getDeviceID()+"\nMagazine ID : "+Config.Magazine_Number+
                    "\nSystem Version : "+ Build.VERSION.SDK_INT);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Config.Support_EMail});

            Intent mailer = Intent.createChooser(intent, null);
            startActivity(mailer);*/
           // openEmailApp();

        }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
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
           /* Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Contact PixelMags");
            intent.putExtra(Intent.EXTRA_TEXT, "Title : "+ Config.Magazine_Title +"\nBundle ID : "+ Config.Bundle_ID +"\nApp Version : "
                    +Config.Version +"\nDevice Id : "+ UserPrefs.getDeviceID()+"\nMagazine ID : "+Config.Magazine_Number+
                    "\nSystem Version : "+ Build.VERSION.SDK_INT);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Config.Support_EMail});

            Intent mailer = Intent.createChooser(intent, null);
            startActivity(mailer);*/
           // openEmailApp();
           // CustomBottomSheetDialog bottomSheetDialog = CustomBottomSheetDialog.getInstance();
            //bottomSheetDialog.show(getActivity().getSupportFragmentManager(), "Custom Bottom Sheet");
        }
    }


    /*private void openEmailApp() {
        List<Intent> emailAppLauncherIntents = new ArrayList<>();

        //Intent that only email apps can handle:
        Intent emailAppIntent = new Intent(Intent.ACTION_SEND);
        emailAppIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact PixelMags");
        emailAppIntent.putExtra(Intent.EXTRA_TEXT, "Title : "+ Config.Magazine_Title +"\nBundle ID : "+ Config.Bundle_ID +"\nApp Version : "
                +Config.Version +"\nDevice Id : "+ UserPrefs.getDeviceID()+"\nMagazine ID : "+Config.Magazine_Number+
                "\nSystem Version : "+ Build.VERSION.SDK_INT);
        emailAppIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{Config.Support_EMail});


        PackageManager packageManager = getActivity().getPackageManager();


        *//*Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Contact PixelMags");
            intent.putExtra(Intent.EXTRA_TEXT, "Title : "+ Config.Magazine_Title +"\nBundle ID : "+ Config.Bundle_ID +"\nApp Version : "
                    +Config.Version +"\nDevice Id : "+ UserPrefs.getDeviceID()+"\nMagazine ID : "+Config.Magazine_Number+
                    "\nSystem Version : "+ Build.VERSION.SDK_INT);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Config.Support_EMail});

            Intent mailer = Intent.createChooser(intent, null);
            startActivity(mailer);*//*

        //All installed apps that can handle email intent:
        List<ResolveInfo> emailApps = packageManager.queryIntentActivities(emailAppIntent, PackageManager.MATCH_ALL);

        for (ResolveInfo resolveInfo : emailApps) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
            emailAppLauncherIntents.add(launchIntent);
        }

        //Create chooser


        Intent chooserIntent = Intent.createChooser(new Intent(), "Select email app:");
        Intent mailer = Intent.createChooser(chooserIntent, null);
        startActivity(mailer);
    }*/

    private void openEmailApp() {
        List<Intent> emailAppLauncherIntents = new ArrayList<>();

        //Intent that only email apps can handle:
        Intent emailAppIntent = new Intent(Intent.ACTION_SEND);
        emailAppIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact PixelMags");
        emailAppIntent.putExtra(Intent.EXTRA_TEXT, "Title : "+ Config.Magazine_Title +"\nBundle ID : "+ Config.Bundle_ID +"\nApp Version : "
                +Config.Version +"\nDevice Id : "+ UserPrefs.getDeviceID()+"\nMagazine ID : "+Config.Magazine_Number+
                "\nSystem Version : "+ Build.VERSION.SDK_INT);
        emailAppIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{Config.Support_EMail});


        PackageManager packageManager = getActivity().getPackageManager();

        //All installed apps that can handle email intent:
        List<ResolveInfo> emailApps = packageManager.queryIntentActivities(emailAppIntent, PackageManager.MATCH_ALL);

        for (ResolveInfo resolveInfo : emailApps) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
            emailAppLauncherIntents.add(launchIntent);
        }

        //Create chooser
        Intent chooserIntent = Intent.createChooser(new Intent(), "Select email app:");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, emailAppLauncherIntents.toArray(new Parcelable[emailAppLauncherIntents.size()]));
        startActivity(chooserIntent);
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

                    Bundle bundle = new Bundle();
                    bundle.putString("Update", "Success");
                    fragment.setArguments(bundle);

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

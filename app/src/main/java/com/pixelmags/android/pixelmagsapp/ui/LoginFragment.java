package com.pixelmags.android.pixelmagsapp.ui;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pixelmags.android.pixelmagsapp.LaunchActivity;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.pixelmagsapp.test.ResultsFragment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        // Demonstration of a collection-browsing activity.
        rootView.findViewById(R.id.goToRegisterButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        navigateToRegisterButton();
                    }
                });

        // ##22 Set title bar based on fragment
        //  ((MainActivity) getActivity()) .setActionBarTitle("Login title");

        // set the Log in Listener
        Button button = (Button) rootView.findViewById(R.id.logInButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
        return rootView;
    }

    private void navigateToRegisterButton() {

       // Intent a = new Intent(getActivity().getBaseContext(), LoginActivity.class);
       //  startActivity(a);


        Fragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putInt("Register Key", 4);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
       // FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentManager.beginTransaction()
                .replace(((ViewGroup)(getView().getParent())).getId(), fragment)
                .addToBackStack(null)
                .commit();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


    /* ##22 Any Mod's positioned here */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActionBar().setTitle("Log in");
    }

    // retrieve the action bar
    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    // On click log in button
    public void doLogin(){
        new CallAPI().execute("");
    }


    private class CallAPI extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            String urlString=params[0]; // URL to call

            urlString="http://www.google.co.uk";

            String resultToDisplay = "";

            InputStream in = null;

            // HTTP Get
            try {

                URL url = new URL(urlString);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                in = new BufferedInputStream(urlConnection.getInputStream());


                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }

                resultToDisplay = total.toString();

            } catch (Exception e ) {

                System.out.println(e.getMessage());
                return e.getMessage();

            }

            return resultToDisplay;

        }

        protected void onPostExecute(String result) {

            if (result != null){
                System.out.println("API result :: " + result);
            }


            Fragment fragment = new ResultsFragment();
            Bundle args = new Bundle();
            args.putInt("Results Key", 5);
            args.putString("DISPLAY_RESULTS", result);
            fragment.setArguments(args);

            FragmentManager fragmentManager = getFragmentManager();
            // FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentManager.beginTransaction()
                    .replace(((ViewGroup)(getView().getParent())).getId(), fragment)
                    .addToBackStack(null)
                    .commit();

        }

    }

}

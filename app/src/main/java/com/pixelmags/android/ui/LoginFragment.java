package com.pixelmags.android.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.pixelmags.android.api.GetMyIssues;
import com.pixelmags.android.api.GetMySubscriptions;
import com.pixelmags.android.api.ValidateUser;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.UserPrefs;
import com.pixelmags.android.util.AccountUtil;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class  LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText mEmailView;
    private EditText mPasswordView;
    private OnFragmentInteractionListener mListener;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private ValidateUserTask mValidateUserTask = null;

    private ProgressDialog loginProgressDialog;

    /**
    * getMyIssues() if Login success
    */
    private GetMyIssuesTask mGetMyIssuesTask = null;

    /**
     * getMySubscriptions api if login is susscess
     */
    private GetMySubscriptionsTask mGetMySubscriptionsTask = null;

    public LoginFragment() {
        // Required empty public constructor
    }

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

        // ##22 Set title bar based on fragment
        //  ((MainActivity) getActivity()) .setActionBarTitle("Login title");
        mEmailView = (EditText) rootView.findViewById(R.id.loginEmail);

        mPasswordView = (EditText) rootView.findViewById(R.id.loginPassword);
        // set the Log in Listener
        Button doLoginButton = (Button) rootView.findViewById(R.id.logInButton);
        doLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        Button doRegisterButton = (Button) rootView.findViewById(R.id.registerButton);
        doRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRegisterButton();
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
    public void doLogin()
    {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Do the checks in reverse

//         Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !AccountUtil.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }


        // Check for a valid email address.
        if (!AccountUtil.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // check if password field is empty
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        //Check if email field is empty
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            loginProgressDialog = ProgressDialog.show(getActivity(), getString(R.string.log_in_progress_dialog_title),
                    getString(R.string.log_in_progress_dialog_message), true);

            // perform the user register attempt.
            mValidateUserTask = new ValidateUserTask(email, password);
            mValidateUserTask.execute((String) null);
        }
    }

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
                            .replace(R.id.main_fragment_container, fragment, "HOME")
                            .commit();

                    return true;

                }

                return false;
            }
        });
    }

    public void displayLogInFailed(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.log_in_fail_title));
        builder.setMessage(getString(R.string.log_in_fail_message));
        builder.setPositiveButton(getString(R.string.ok), null);
        builder.show();

    }

    public void loadAllIssuesPage(){

        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        Fragment fragmentAllIsuues = new AllIssuesFragment();

        // Insert the fragment by replacing any existing fragment
        FragmentManager allIssuesFragmentManager = getFragmentManager();
        allIssuesFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, fragmentAllIsuues)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

    }

    private void callGetMyIssuesAPI()
    {
        mGetMyIssuesTask = new GetMyIssuesTask();
        mGetMyIssuesTask.execute((String) null);
    }

    private void callGetMySubscriptionsAPI()
    {
        mGetMySubscriptionsTask = new GetMySubscriptionsTask();
        mGetMySubscriptionsTask.execute((String) null);
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

    private class ValidateUserTask extends AsyncTask<String, String,String> {

        private final String mEmail;
        private final String mPassword;
        ValidateUser apiValidateUser;

        ValidateUserTask(String email, String password) {
            mEmail = email;
            mPassword = password;

        }
        @Override
        protected String doInBackground(String... params) {

            String resultToDisplay = "";

            apiValidateUser = new ValidateUser(mEmail, mPassword);
            apiValidateUser.init();

            if(apiValidateUser.isSuccess())
            {
                //getMyIssues API
                callGetMyIssuesAPI();
            }
            //
            return resultToDisplay;

        }
        protected void onPostExecute(String result) {

            if(loginProgressDialog != null) {
                loginProgressDialog.dismiss();
            }

            // successful or failed login action
            if(UserPrefs.getUserLoggedIn()){

                System.out.println("LOG IN SUCCESS");

                // Navigate to issues page
                loadAllIssuesPage();



            }else{

                // Display log in fail
                displayLogInFailed();

            }
        }
    }

    private class GetMyIssuesTask extends AsyncTask<String, String,String> {

        GetMyIssues apiGetMyIssues;

        @Override
        protected String doInBackground(String... params) {

            String resultToDisplay = "";

            apiGetMyIssues = new GetMyIssues();
            apiGetMyIssues.init();

            //Irrespective of getMyissues API success we will call Getmysubscriptions as we know user is loggedIn
            callGetMySubscriptionsAPI();
            //
            return resultToDisplay;

        }
        protected void onPostExecute(String result) {
        }
    }

    private class GetMySubscriptionsTask extends AsyncTask<String, String,String> {

        GetMySubscriptions apiGetMySubscriptions;

        @Override
        protected String doInBackground(String... params) {

            String resultToDisplay = "";

            apiGetMySubscriptions = new GetMySubscriptions();
            apiGetMySubscriptions.init();
            //
            return resultToDisplay;

        }
        protected void onPostExecute(String result) {
        }
    }

}

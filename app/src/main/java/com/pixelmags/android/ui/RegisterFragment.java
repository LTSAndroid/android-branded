package com.pixelmags.android.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.pixelmags.android.api.CreateUser;
import com.pixelmags.android.api.GetMyIssues;
import com.pixelmags.android.api.GetMySubscriptions;
import com.pixelmags.android.api.ValidateUser;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.UserPrefs;

import java.util.Calendar;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mfirstnameView;
    private EditText mlastnameView;
    private TextView mDOBView;
    private EditText mCPasswordView;
    private CheckBox mtemsconditionsView;
    private View mProgressView;
    private View mRegisterFormView;
    private int yy,mm,dd;
    private TextView pickDate;
    private Calendar calendar;
    private String TAG = "RegisterFragment";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegistrationTask mRegisterTask = null;

    private OnFragmentInteractionListener mListener;
    private GetMyIssuesTask mGetMyIssuesTask = null;
    private GetMySubscriptionsTask mGetMySubscriptionsTask = null;
    private ProgressDialog progressBar;
    // Listener
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            String year1 = String.valueOf(selectedYear);
            String month1 = String.valueOf(selectedMonth + 1);
            String day1 = String.valueOf(selectedDay);

            mDOBView.setText(day1 + "-" + month1 + "-" + year1);
            mDOBView.setError(null);

        }
    };

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // set the Log in Listener
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        mEmailView = (EditText) rootView.findViewById(R.id.accountName);
        mPasswordView = (EditText) rootView.findViewById(R.id.registerPassword);
        mCPasswordView = (EditText) rootView.findViewById(R.id.registerConfirmPassword);
        mfirstnameView = (EditText) rootView.findViewById(R.id.accountuserid);
        mlastnameView = (EditText) rootView.findViewById(R.id.accountEmailid);
       // mDOBView = (TextView) rootView.findViewById(R.id.dateOfBirthregister);
        mtemsconditionsView = (CheckBox) rootView.findViewById(R.id.registerAcceptTermsConditions);
        calendar = Calendar.getInstance(TimeZone.getDefault());
        mDOBView= (TextView) rootView.findViewById(R.id.registerDateOfBirth);

        Button button = (Button) rootView.findViewById(R.id.registerDoRegister);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegister();
            }
        });

//        Button backtologinbutton = (Button) rootView.findViewById(R.id.backtologin);
//        backtologinbutton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                navigateTobacktologinbutton();
//            }
//        });

        Button datePicker = (Button) rootView.findViewById(R.id.pick_dob);
        datePicker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                        R.style.AppTheme, datePickerListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePicker.setCancelable(false);
                datePicker.setTitle("Select the date");
                datePicker.show();
            }
        });

        return rootView;
    }

    public void navigateTobacktologinbutton() {

        // Intent a = new Intent(getActivity().getBaseContext(), LoginActivity.class);
        //  startActivity(a);


        Fragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putInt("Register Key", 4);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        // FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentManager.beginTransaction()
                .replace(((ViewGroup) (getView().getParent())).getId(), fragment)
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //getActionBar().setTitle("Register");
    }

    // retrieve the action bar
    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    // On click log in button
    public void doRegister()
    {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mCPasswordView.setError(null);
        mfirstnameView.setError(null);
        mDOBView.setError(null);
        mlastnameView.setError(null);
        mtemsconditionsView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String cPassword = mCPasswordView.getText().toString();
        String firstName = mfirstnameView.getText().toString();
        String lastName = mlastnameView.getText().toString();
        String DOB = mDOBView.getText().toString();
        Log.d(TAG,"DOB is : "+DOB);
        Boolean termsConditions = mtemsconditionsView.isChecked();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }


        //for confirm password
        if (TextUtils.isEmpty(cPassword)) {
            mCPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mCPasswordView;
            cancel = true;
        }

        if(cPassword.length() != password.length()){
            mPasswordView.setError(getString(R.string.password_length_not_matching));
            mCPasswordView.setError(getString(R.string.password_length_not_matching));
            focusView = mPasswordView;
            cancel = true;
        }

        if(!password.equalsIgnoreCase(cPassword)){
            mPasswordView.setError(getString(R.string.password_length_not_matching));
            mCPasswordView.setError(getString(R.string.password_length_not_matching));
            focusView = mCPasswordView;
            cancel = true;
        }

        //first name
        if (TextUtils.isEmpty(firstName)) {
            mfirstnameView.setError(getString(R.string.error_name));
            focusView = mfirstnameView;
            cancel = true;
        }

        //second name
        if (TextUtils.isEmpty(lastName)) {
            mlastnameView.setError(getString(R.string.error_field_required));
            focusView = mlastnameView;
            cancel = true;
        }

       //DOB

        Log.d(TAG,"DOB is empty : "+ TextUtils.isEmpty(DOB));

       if (TextUtils.isEmpty(DOB)) {
            mDOBView.setError(getString(R.string.error_field_required));
            focusView = mDOBView;
            cancel = true;
        }

        //Terms n Conditions
        if (!termsConditions) {
            mtemsconditionsView.setError(getString(R.string.error_field_required));
            focusView = mtemsconditionsView;
            cancel = true;
        }
        // Check for a valid email address.

        if (TextUtils.isEmpty(email)) {
        mEmailView.setError(getString(R.string.error_email));
        focusView = mEmailView;
        cancel = true;
    }
        else if (!isEmailValid(email)) {
        mEmailView.setError(getString(R.string.error_invalid_email));
        focusView = mEmailView;
        cancel = true;
    }
        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // perform the user register attempt.
//            mRegisterTask = new UserRegistrationTask(email, password,firstName,lastName,DOB);
            mRegisterTask = new UserRegistrationTask(email, password,firstName,lastName,DOB);
            mRegisterTask.execute((String) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {

        //TODO: Replace this with your own logic
        return password.length() >= 6;
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

        Bundle bundle = new Bundle();
        bundle.putString("Update", "Success");
        fragmentAllIsuues.setArguments(bundle);

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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegistrationTask extends AsyncTask<String, String, String> {

        private final String mEmail;
        private final String mPassword;
        private final String mFirstName;
        private final String mLastName;
        private final String mDOB;
        private ValidateUserTask mValidateUserTask = null;

        UserRegistrationTask(String email, String password,String firstName , String lastName, String DOB) {
            mEmail = email;
            mPassword = password;
            mFirstName = firstName;
            mLastName = lastName;
            mDOB = DOB;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.d(TAG,"Email is : "+mEmail);
            Log.d(TAG,"Password is : "+mPassword);
            Log.d(TAG,"FirstName is : "+mFirstName);
            Log.d(TAG,"LastName is : "+mLastName);
            Log.d(TAG,"DOB is : "+mDOB);

            try{

                progressBar = new ProgressDialog(getActivity());
                if (progressBar != null) {
                    progressBar.show();
                    progressBar.setCancelable(false);
                    progressBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    progressBar.setContentView(R.layout.progress_dialog);
                }



            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            String resultToDisplay = "";

            try{
                CreateUser apiCreateUser = new CreateUser();
                apiCreateUser.init(mEmail,mPassword,mFirstName,mLastName,mDOB);

            }catch (Exception e){

            }



          /*  HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://api.pixelmags.com/createUser");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(9);
                nameValuePairs.add(new BasicNameValuePair("email", mEmail));
                nameValuePairs.add(new BasicNameValuePair("password", mPassword));
                nameValuePairs.add(new BasicNameValuePair("date_of_birth", mDOB));
                nameValuePairs.add(new BasicNameValuePair("first_name", mFirstName));
                nameValuePairs.add(new BasicNameValuePair("last_name", mLastName));
                nameValuePairs.add(new BasicNameValuePair("device_id", "testingforbanded"));
                nameValuePairs.add(new BasicNameValuePair("magazine_id", Config.Magazine_Number));
                nameValuePairs.add(new BasicNameValuePair("api_mode",Config.api_mode));
                nameValuePairs.add(new BasicNameValuePair("api_version", Config.api_version));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                InputStream is = response.getEntity().getContent();
                BufferedInputStream bis = new BufferedInputStream(is);
                ByteArrayBuffer baf = new ByteArrayBuffer(20);

                int current = 0;

                while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
                }


                resultToDisplay = new String(baf.toByteArray());
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            */

            return resultToDisplay;

        }

    /*   @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
           // Use the current date as the default date in the picker
           final Calendar c = Calendar.getInstance();
           int year = c.get(Calendar.YEAR);
           int month = c.get(Calendar.MONTH);
           int day = c.get(Calendar.DAY_OF_MONTH);

           DatePickerDialog dpd = new DatePickerDialog(this,
                   new DatePickerDialog.OnDateSetListener() {*//*

           // Create a new instance of DatePickerDialog and return it
                           return new DatePickerDialog(getActivity(), this, mYear, mMonth, day);
                       }

                       public void onDateSet(DatePicker view, int year, int month, int day, int dayOfMonth ,int monthOfYear)
                       {

                           // Display Selected date in textbox
                           mtxtDate.setText(dayOfMonth + "-"
                                   + (monthOfYear + 1) + "-" + year);

                       }
                   }, mYear, mMonth, mDay);

       }*/

            // Do something with the date chosen by the user

   /* @Override
    public void onClick(View v) {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
    }*/

        protected void onPostExecute(String result) {

            mRegisterTask = null;
            //showProgress(false);

            if (result != null) {
                System.out.println("API result :: " + result);
            }


            System.out.println("PRINT PREFS  :: " + UserPrefs.getUserEmail());
            System.out.println("PRINT PREFS  :: " + UserPrefs.getUserFirstName());
            System.out.println("PRINT PREFS  :: " + UserPrefs.getUserPixelmagsId());

            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

//            Fragment fragment = new ResultsFragment();
//            Bundle args = new Bundle();
//            args.putInt("Results Key", 5);
//            args.putString("DISPLAY_RESULTS", result);
//            fragment.setArguments(args);



            // perform the user register attempt.

                mValidateUserTask = new ValidateUserTask(mEmail, mPassword);
                mValidateUserTask.execute((String) null);

        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
        }
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
               // callGetMyIssuesAPI();
            }
            //
            return resultToDisplay;

        }
        protected void onPostExecute(String result) {

            if(progressBar != null)
                progressBar.dismiss();

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

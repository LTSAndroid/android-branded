package com.pixelmags.android.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pixelmags.android.datamodels.MySubscription;
import com.pixelmags.android.datamodels.Subscription;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.pixelmagsapp.adapter.SubscriptionAdapter;
import com.pixelmags.android.storage.MySubscriptionsDataSet;
import com.pixelmags.android.storage.SubscriptionsDataSet;
import com.pixelmags.android.storage.UserPrefs;
import com.pixelmags.android.util.BaseApp;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SubscriptionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SubscriptionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubscriptionsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    List<String> subDescription;
    List<String> subPrice;
    List<String> subPaymentProvider;
    //TextView mTextView;
    List<String> androidStoreSku;
    List<Integer> id;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GetSubscriptionsTask mGetSubscriptions = null;
    private RecyclerView mRecyclerView;
    private SubscriptionAdapter subscriptionAdapter;
    private OnFragmentInteractionListener mListener;
    private String TAG = "SubscriptionFragment";
    private LinearLayout subscriptionDetailLayout;
    private TextView magazineId;
    private TextView creditsAvaliable;
    private TextView purchaseDate;
    private TextView expiryDate;
    private TextView subscriptionProductionId;

    public SubscriptionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubscriptionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SubscriptionsFragment newInstance(String param1, String param2) {
        SubscriptionsFragment fragment = new SubscriptionsFragment();
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


        View rootView =  inflater.inflate(R.layout.fragment_subscriptions, container, false);


        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        subscriptionDetailLayout = (LinearLayout) rootView.findViewById(R.id.subscription_details_layout);
        magazineId = (TextView) rootView.findViewById(R.id.magazine_id_value);
        creditsAvaliable = (TextView) rootView.findViewById(R.id.credits_available_value);
        purchaseDate = (TextView) rootView.findViewById(R.id.purchase_date_value);
        expiryDate = (TextView) rootView.findViewById(R.id.expiry_date_value);
        subscriptionProductionId = (TextView) rootView.findViewById(R.id.subscription_production_id_value);


        MySubscriptionsDataSet mySubscriptionData = new MySubscriptionsDataSet(BaseApp.getContext());
        ArrayList<MySubscription> mySubscriptionArrayList = mySubscriptionData.getMySubscriptions(mySubscriptionData.getReadableDatabase());
        mySubscriptionData.close();


        if(mySubscriptionArrayList != null && UserPrefs.getUserLoggedIn()){
            if(mySubscriptionArrayList.size() >= 1){
                mRecyclerView.setVisibility(View.GONE);
                subscriptionDetailLayout.setVisibility(View.VISIBLE);

                for(int i=0; i<mySubscriptionArrayList.size(); i++){
                    magazineId.setText(mySubscriptionArrayList.get(i).magazineID);
//                    creditsAvaliable.setText(mySubscriptionArrayList.get(i).creditsAvailable);
                    purchaseDate.setText(mySubscriptionArrayList.get(i).purchaseDate);
                    expiryDate.setText(mySubscriptionArrayList.get(i).expiresDate);
                    subscriptionProductionId.setText(mySubscriptionArrayList.get(i).subscriptionProductId);
                }

            }else{
                subscriptionDetailLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);


                ArrayList<Subscription> mySubsArray = null;

                SubscriptionsDataSet mDbReader = new SubscriptionsDataSet(BaseApp.getContext());

                boolean tableExists = mDbReader.isTableExists(mDbReader.getReadableDatabase());
                if(tableExists){
                    mySubsArray = mDbReader.getAllSubscriptions(mDbReader.getReadableDatabase());
                    mDbReader.close();
                }else{
                    mDbReader.close();
                }

                subDescription = new ArrayList<String>();
                subPrice = new ArrayList<String>();
                subPaymentProvider = new ArrayList<String>();
                androidStoreSku = new ArrayList<String>();
                id = new ArrayList<Integer>();

                id.clear();
                subDescription.clear();
                subPrice.clear();
                subPaymentProvider.clear();
                androidStoreSku.clear();


                if(mySubsArray != null){
                    for(int i=0; i< mySubsArray.size();i++) {
                        Subscription sub = mySubsArray.get(i);
                        Log.d(TAG,"Sub ID is : "+sub.id);
                        Log.d(TAG,"Complete Subscription is : "+sub.payment_provider);
                        Log.d(TAG,"Subscription Description is ::" + sub.description);
                        Log.d(TAG,"Subscription price is "+sub.price);
                        Log.d(TAG,"Subscription android store sku is "+sub.android_store_sku);

                        id.add(sub.id);
                        subDescription.add(sub.description);
                        subPrice.add(String.valueOf(sub.price));
                        subPaymentProvider.add(sub.payment_provider);
                        androidStoreSku.add(sub.android_store_sku);
                    }

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    subscriptionAdapter = new SubscriptionAdapter(id,subDescription, subPrice, subPaymentProvider, androidStoreSku, getActivity());
                    mRecyclerView.setAdapter(subscriptionAdapter);
                }else{
                    showAlertDialog("Currently no subscription released for this magazine");
                }

            }
        }else{
            subscriptionDetailLayout.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

            ArrayList<Subscription> mySubsArray = null;

            SubscriptionsDataSet mDbReader = new SubscriptionsDataSet(BaseApp.getContext());

            boolean tableExists = mDbReader.isTableExists(mDbReader.getReadableDatabase());
            if(tableExists){
                mySubsArray = mDbReader.getAllSubscriptions(mDbReader.getReadableDatabase());
                mDbReader.close();
            }else{
                mDbReader.close();
            }

            subDescription = new ArrayList<String>();
            subPrice = new ArrayList<String>();
            subPaymentProvider = new ArrayList<String>();
            androidStoreSku = new ArrayList<String>();
            id = new ArrayList<Integer>();

            id.clear();
            subDescription.clear();
            subPrice.clear();
            subPaymentProvider.clear();
            androidStoreSku.clear();

            if(mySubsArray != null){
                for(int i=0; i< mySubsArray.size();i++) {
                    Subscription sub = mySubsArray.get(i);
                    Log.d(TAG,"Sub ID is : "+sub.id);
                    Log.d(TAG,"Complete Subscription is : "+sub.payment_provider);
                    Log.d(TAG,"Subscription Description is ::" + sub.description);
                    Log.d(TAG,"Subscription price is "+sub.price);
                    Log.d(TAG,"Subscription android store sku is "+sub.android_store_sku);

                    id.add(sub.id);
                    subDescription.add(sub.description);
                    subPrice.add(String.valueOf(sub.price));
                    subPaymentProvider.add(sub.payment_provider);
                    androidStoreSku.add(sub.android_store_sku);
                }

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                subscriptionAdapter = new SubscriptionAdapter(id,subDescription, subPrice, subPaymentProvider, androidStoreSku, getActivity());
                mRecyclerView.setAdapter(subscriptionAdapter);
            }else{
                showAlertDialog("Currently no subscription released for this magazine");
            }


        }


        // Inflate the layout for this fragment
        return rootView;
    }

    private void showAlertDialog(String message) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Alert")
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();

                    }
                });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateTextView(String text){


        System.out.println("SUBS TEXT :::: " + text);

       // mTextView.setText(text);
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

    public class GetSubscriptionsTask extends AsyncTask<String, String, String> {

        ArrayList<Subscription> subscriptionsArray = null;

        GetSubscriptionsTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            try{
                // No longer api get subs here - done on launch
                //GetSubscriptions apiGetSubs = new GetSubscriptions();
                //apiGetSubs.init();

                SubscriptionsDataSet mDbHelper = new SubscriptionsDataSet(BaseApp.getContext());
                subscriptionsArray = mDbHelper.getAllSubscriptions(mDbHelper.getReadableDatabase());
                mDbHelper.close();

            }catch (Exception e){

            }

            return null;

        }

        protected void onPostExecute(String result) {

            mGetSubscriptions = null;
            //showProgress(false);

            String resultSubs = "Sub Results ::: ";

            for(int i=0; i< subscriptionsArray.size();i++) {

                Subscription sub = subscriptionsArray.get(i);

                resultSubs = resultSubs + "////"+sub.price+","+sub.description+","+sub.synopsis;

            }

            updateTextView(resultSubs);


        }

        @Override
        protected void onCancelled() {
            mGetSubscriptions = null;
        }
    }


}

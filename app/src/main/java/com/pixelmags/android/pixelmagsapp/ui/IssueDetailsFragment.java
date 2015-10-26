package com.pixelmags.android.pixelmagsapp.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.pixelmagsapp.R;



public class IssueDetailsFragment extends Fragment {


    public Magazine issueData;
    private static final String SERIALIZABLE_MAG_KEY = "serializable_mag_key";

    public IssueDetailsFragment() {
        // Required empty public constructor
    }

    public static IssueDetailsFragment newInstance(Magazine magazineData) {

        IssueDetailsFragment fragment = new IssueDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(SERIALIZABLE_MAG_KEY, magazineData);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            issueData = (Magazine) getArguments().getSerializable(SERIALIZABLE_MAG_KEY);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_issue_details, container, false);


        if(issueData!=null){

            // Load all data for the issue details page here

            ImageView issueDetailsImageView = (ImageView) rootView.findViewById(R.id.issueDetailsImageView);
            if(issueData.thumbnailBitmap!=null){
                issueDetailsImageView.setImageBitmap(issueData.thumbnailBitmap);
            }

            TextView issueDetailsTitle = (TextView) rootView.findViewById(R.id.issueDetailsTitle);
            issueDetailsTitle.setText(issueData.title);

            TextView issueDetailsSynopsis = (TextView) rootView.findViewById(R.id.issueDetailsSynopsis);
            issueDetailsSynopsis.setText(issueData.synopsis);

            Button issueDetailsPriceButton = (Button) rootView.findViewById(R.id.issueDetailsPriceButton);
            issueDetailsPriceButton.setText(String.valueOf(issueData.price));



        }

        return rootView;
    }




    @Override
    public void onDetach() {
        super.onDetach();

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
     */


}

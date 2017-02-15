package com.pixelmags.android.ui.uicomponents;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pixelmags.android.IssueView.NewIssueView;
import com.pixelmags.android.comms.Config;
import com.pixelmags.android.datamodels.AllDownloadsIssueTracker;
import com.pixelmags.android.download.DownloadThumbnails;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.pixelmagsapp.adapter.DownloadAdapter;
import com.pixelmags.android.storage.AllDownloadsDataSet;
import com.pixelmags.android.storage.BrandedSQLiteHelper;
import com.pixelmags.android.util.BaseApp;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadFragment extends Fragment {

    public static DownloadAdapter downloadAdapter;
    private RecyclerView recyclerView;
    private GetAllDownloadedIssuesTask mGetAllDownloadedIssuesTask;
    private ArrayList<AllDownloadsIssueTracker> allDownloadsIssuesListTracker = null;


    public DownloadFragment() {
        // Required empty public constructor
    }

    public static void updateAdapter(){
        downloadAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_download, container, false);

        mGetAllDownloadedIssuesTask = new GetAllDownloadedIssuesTask(Config.Magazine_Number,view);
        mGetAllDownloadedIssuesTask.execute((String) null);

        return view;
    }

    private void setListView(View rootView) {

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutmanager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutmanager);
        // recyclerView.addItemDecoration(new DividerItemDecaration(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        downloadAdapter = new DownloadAdapter(getActivity(),allDownloadsIssuesListTracker,
                fragmentManager);
        recyclerView.setAdapter(downloadAdapter);
    }

    private Bitmap loadImageFromStorage(String path)
    {

        Bitmap issueThumbnail = null;
        try {
            File file = new File(path);
            FileInputStream inputStream = new FileInputStream(file);
            issueThumbnail = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return issueThumbnail;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK)) {
            // recreate your fragment here
            Fragment frg = null;
            frg = getActivity().getSupportFragmentManager().findFragmentByTag("DownloadFragment");
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.detach(frg).attach(frg).commit();
        }
    }

    @Override
    public void onResume(){

        super.onResume();

        if(NewIssueView.issueViewOpen){
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();

            NewIssueView.issueViewOpen = false;
        }

    }

    public class GetAllDownloadedIssuesTask extends AsyncTask<String, String, String> {

        private final String mMagazineID;
        private View rootView;

        GetAllDownloadedIssuesTask(String MagazineID, View rootView) {
            mMagazineID = MagazineID;
            this.rootView = rootView;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            String resultToDisplay = "";

            try {

                allDownloadsIssuesListTracker = null; // clear the list

                AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());

                boolean isExists = mDbReader.isTableExists(mDbReader.getReadableDatabase(), BrandedSQLiteHelper.TABLE_ALL_DOWNLOADS);
                if(isExists) {
                    allDownloadsIssuesListTracker = mDbReader.getDownloadIssueList(mDbReader.getReadableDatabase(), Config.Magazine_Number);
                    mDbReader.close();
                }else{
                    mDbReader.close();
                }


                if(allDownloadsIssuesListTracker != null) {

                    for (int i = 0; i < allDownloadsIssuesListTracker.size(); i++) {

                        System.out.println("<< singleDownloadedIssue "+ allDownloadsIssuesListTracker.get(i).issueID+" >>");
                        allDownloadsIssuesListTracker.get(i).thumbnailBitmap = loadImageFromStorage(
                                DownloadThumbnails.getIssueDownloadedThumbnailStorageDirectory(
                                        String.valueOf(allDownloadsIssuesListTracker.get(i).issueID)
                                )
                        );

                    }

                }

            }catch (Exception e){
                e.printStackTrace();
            }
            return resultToDisplay;

        }

        protected void onPostExecute(String result) {

            // update the UI

            if(allDownloadsIssuesListTracker != null){
                setListView(rootView);
            }


        }

        @Override
        protected void onCancelled() {

        }
    }

}

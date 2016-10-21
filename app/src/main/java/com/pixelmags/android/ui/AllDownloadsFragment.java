package com.pixelmags.android.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.crashlytics.android.Crashlytics;
import com.pixelmags.android.comms.Config;
import com.pixelmags.android.datamodels.AllDownloadsIssueTracker;
import com.pixelmags.android.download.DownloadThumbnails;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.pixelmagsapp.adapter.CustomAllDownloadsGridAdapter;
import com.pixelmags.android.storage.AllDownloadsDataSet;
import com.pixelmags.android.util.BaseApp;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;


public class AllDownloadsFragment extends Fragment {

    private ArrayList<AllDownloadsIssueTracker> allDownloadsIssuesListTracker = null;
    public static CustomAllDownloadsGridAdapter gridDownloadAdapter;
    private GetAllDownloadedIssuesTask mGetAllDownloadedIssuesTask;
    private String TAG = "AllDownloadsFragment";
    private static View grid;
    public static int jumpTime = 0;
//    private RecyclerView recyclerView;
    GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(getActivity(), new Crashlytics());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_all_downloads, container, false);

//        recyclerView = (RecyclerView) rootView.findViewById(R.id.displayAllDownloadsGridView);
//        gridView = (GridView) rootView.findViewById(R.id.displayAllDownloadsGridView);

        // retrieving the downlaoded issues - run inside a async task as there is db access required.
        mGetAllDownloadedIssuesTask = new GetAllDownloadedIssuesTask(Config.Magazine_Number,rootView);
        mGetAllDownloadedIssuesTask.execute((String) null);

        // loadAllIssues();

//        setGridAdapter(rootView);

        // Inflate the layout for this fragment
        return rootView;
    }


   public void setGridAdapter(View rootView){

       // set the Grid Adapter

       Log.d(TAG, "All Downloads Issue List " + allDownloadsIssuesListTracker);

       // use rootview to fetch view (when called from onCreateView) else null returns
       gridView = (GridView) rootView.findViewById(R.id.displayAllDownloadsGridView);

//       FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//       gridDownloadAdapter = new CustomAllDownloadsGridAdapter(getActivity(),
//               allDownloadsIssuesListTracker,getActivity().getSupportFragmentManager());
//       RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
//       recyclerView.setLayoutManager(mLayoutManager);
//       recyclerView.setItemAnimator(new DefaultItemAnimator());
//       recyclerView.setAdapter(gridDownloadAdapter);

       FragmentManager fragmentManager = getFragmentManager();
       if(fragmentManager != null){
           gridDownloadAdapter = new CustomAllDownloadsGridAdapter(getActivity(),allDownloadsIssuesListTracker,
                   fragmentManager);
           gridView.setAdapter(gridDownloadAdapter);
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

                    gridDownloadAdapter.updateProgressCount();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // handle back button
                            Fragment fragment = new AllIssuesFragment();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_fragment_container, fragment, "All Issues")
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit();
                        }
                    }, 1000);


                    return true;

                }

                return false;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     *
     * Represents an asynchronous task used to fetch all the issues.
     *
     */
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
                allDownloadsIssuesListTracker = mDbReader.getDownloadIssueList(mDbReader.getReadableDatabase(), Config.Magazine_Number);
                mDbReader.close();

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

            setGridAdapter(rootView);

//            Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentByTag("ALLDOWNLOADFRAGMENT");
//            if(currentFragment != null && currentFragment.isVisible()) {
//                FragmentTransaction fragTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                fragTransaction.detach(currentFragment);
//                fragTransaction.attach(currentFragment);
//                fragTransaction.commit();
//            }

//           if(gridDownloadAdapter!=null){
//               gridDownloadAdapter.notifyDataSetChanged();
//            }


        }

        @Override
        protected void onCancelled() {

        }
    }

    public void deleteThumbnail(String path){
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
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
}

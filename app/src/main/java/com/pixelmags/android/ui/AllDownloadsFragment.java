package com.pixelmags.android.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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


public class AllDownloadsFragment extends Fragment {

    private ArrayList<AllDownloadsIssueTracker> allDownloadsIssuesListTracker = null;
    public static CustomAllDownloadsGridAdapter gridDownloadAdapter;
    private GetAllDownloadedIssuesTask mGetAllDownloadedIssuesTask;
    private String TAG = "AllDownloadsFragment";
    GridView gridView;
    private static View grid;
    public static int jumpTime = 0;
    View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_all_downloads, container, false);

        gridView = (GridView) rootView.findViewById(R.id.displayAllDownloadsGridView);

        // retrieving the downlaoded issues - run inside a async task as there is db access required.
        mGetAllDownloadedIssuesTask = new GetAllDownloadedIssuesTask(Config.Magazine_Number);
        mGetAllDownloadedIssuesTask.execute((String) null);

        // loadAllIssues();

//        setGridAdapter();

        // Inflate the layout for this fragment
        return rootView;
    }


   public void setGridAdapter(){

       // set the Grid Adapter

       Log.d(TAG, "All Downloads Issue List " + allDownloadsIssuesListTracker);

       // use rootview to fetch view (when called from onCreateView) else null returns
//       gridView = (GridView) rootView.findViewById(R.id.displayAllDownloadsGridView);
       gridDownloadAdapter = new CustomAllDownloadsGridAdapter(getActivity(),allDownloadsIssuesListTracker,getActivity().getSupportFragmentManager());
       gridView.setAdapter(gridDownloadAdapter);


       //   gridview.setNumColumns(4);

   }


//    public void refreshGrid(ArrayList<AllDownloadsIssueTracker> allDownloadsIssuesListTrackerNew){
//
//        gridView = (GridView) rootView.findViewById(R.id.displayAllDownloadsGridView);
//
//        gridDownloadAdapter = new CustomAllDownloadsGridAdapter(getActivity(),allDownloadsIssuesListTrackerNew,getFragmentManager());
//        gridView.setAdapter(gridDownloadAdapter);
//    }


    public void updateButtonStateFragment(int status){
        gridDownloadAdapter.updateButtonState(status);
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
                                    .replace(R.id.main_fragment_container, fragment, "HOME")
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

//    public void gridIssueImageClicked(int position){
//
//        navigateToIssueDetails(position);
//
//    }

//    private void navigateToIssueDetails(int position) {
//
//        FragmentManager fragmentManager = getFragmentManager();
//        String issueID = String.valueOf(allDownloadsIssuesListTracker.get(position).issueID);
//
//        Fragment fragment = IssueDetailsFragment.newInstance(issueID, Config.Magazine_Number);
//        fragmentManager.beginTransaction()
//                .replace(((ViewGroup) (getView().getParent())).getId(), fragment)
//                .addToBackStack(null)
//                .commit();
//
//    }



///**
// *  A custom GridView to display the Downloaded Issues.
// *
// */
//
//    public class CustomAllDownloadsGridAdapter extends BaseAdapter implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
//
//        private Context mContext;
//        private ProgressBar progressBar;
//        private static final int PROGRESS = 0*1;
//        private int mProgressStatus = 0;
//        private int listMenuItemPosition;
//        private CardView cardView;
//        private int downloadStatus;
//        private String documentKey;
//        ArrayList<IssueDocumentKey> issueDocumentKeys;
//        private boolean run = true;
//
//
//        public CustomAllDownloadsGridAdapter(Context c) {
//            mContext = c;
//        }
//
//        @Override
//        public int getCount() {
//
//            if(allDownloadsIssuesListTracker == null){
//                return 0;
//            }
//
//            return allDownloadsIssuesListTracker.size();
//        }
//
//        @Override
//        public Object getItem(int arg0) {
//            return allDownloadsIssuesListTracker.get(arg0).thumbnailBitmap;
//        }
//
//        @Override
//        public long getItemId(int arg0) {
//            return arg0;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            if(convertView==null){
//
//                grid = new View(mContext);
//                LayoutInflater inflater = getActivity().getLayoutInflater();
//                grid=inflater.inflate(R.layout.all_downloads_custom_grid_layout, parent, false);
//
//            }else{
//                grid = (View)convertView;
//            }
//
//            // Set the magazine image
//            cardView = (CardView) grid.findViewById(R.id.card_view);
//            cardView.setTag(position);
//
//                if(allDownloadsIssuesListTracker.get(position).thumbnailBitmap != null){
//
//                    ImageView imageView = (ImageView) grid.findViewById(R.id.gridDownloadedIssueImage);
//                    imageView.setImageBitmap(allDownloadsIssuesListTracker.get(position).thumbnailBitmap);
//                    //imageView.setImageBitmap(bmp);
//
//                    imageView.setTag(new Integer(position));
//                    imageView.setOnClickListener(new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//
//                             gridIssueImageClicked((Integer) v.getTag());
//
//                        }
//                    });
//                }
//
//            if(allDownloadsIssuesListTracker.get(position).issueTitle != null) {
//                TextView issueTitleText = (TextView) grid.findViewById(R.id.gridDownloadedTitleText);
//                issueTitleText.setText(allDownloadsIssuesListTracker.get(position).issueTitle);
//            }
//
//            progressBar = (ProgressBar) grid.findViewById(R.id.progressBar);
//            progressBar.setTag(position);
//
//            MultiStateButton gridDownloadStatusButton = (MultiStateButton) grid.findViewById(R.id.gridMultiStateButton);
//            final int status = allDownloadsIssuesListTracker.get(position).downloadStatus;
//            downloadStatus = status;
//            grid.setTag(position);
//
//            if(status == 1 || status == 2 || status == 6){
//
//                gridDownloadStatusButton.setAsView(Magazine.STATUS_VIEW);
//                updateTheProgressBar(progressBar.getId());
//            }
//
//            if(status == 3){
//                String pausedStatusText = AllDownloadsDataSet.getDownloadStatusText(status);
//                gridDownloadStatusButton.setAsDownload(pausedStatusText);
//                progressBar.setProgress(jumpTime);
//            }
//
//            if(status == 4 || status == -1){
//                String downloadStatusText = AllDownloadsDataSet.getDownloadStatusText(status);
//                gridDownloadStatusButton.setText(downloadStatusText);
//                progressBar.setProgress(0);
//            }
//
//            if(status == 0){
//                gridDownloadStatusButton.setAsView(Magazine.STATUS_VIEW);
//                progressBar.setProgress(100);
//            }
//
//            gridDownloadStatusButton.setTag(position); // save the gridview index
//            gridDownloadStatusButton.setOnClickListener(this);
//
//            ImageView popup = (ImageView) grid.findViewById(R.id.moreDownloadOptionsMenuButton);
//            popup.setTag(position);
//            popup.setOnClickListener(this);
//
//            return grid;
//        }
//
//
//
//    @Override
//    public void notifyDataSetChanged(){
//        super.notifyDataSetChanged();
//    }
//
//    public void updateAdapter(){
//        notifyDataSetChanged();
//    }
//
//    public void updateTheProgressBar(int id) {
//
//        Log.d(TAG,"Id of the progress Bar is : "+progressBar.getId());
//        Log.d(TAG,"Id of the progress bar should be updated is : "+id);
//
//        if (progressBar.getId() == id){
//
//            final int limit = 100;
//            final Thread t = new Thread() {
//                @Override
//                public void run() {
//                    while (run) {
//                        try {
//                            synchronized (this) {
//
//                                if (getActivity() == null)
//                                    return;
//
//                                getActivity().runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (jumpTime == limit) {
//                                            run = false;
//                                        }
//                                        jumpTime += 1;
//                                        if (progressBar != null) {
//                                            progressBar.setProgress(jumpTime);
//                                        }
//                                    }
//                                });
//                                wait(6600);
//                            }
//
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            };
//            t.start();
//
//        }
//    }
//
//    public void updateButtonState(int buttonState) {
//        if (buttonState == 0) {
//            progressBar = (ProgressBar) grid.findViewById(R.id.progressBar);
//            progressBar.setProgress(100);
//            MultiStateButton gridDownloadStatusButton = (MultiStateButton) grid.findViewById(R.id.gridMultiStateButton);
//            String StatusText = AllDownloadsDataSet.getDownloadStatusText(AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW);
//            gridDownloadStatusButton.setAsView(StatusText);
//        } else if (buttonState == 6) {
//            MultiStateButton gridDownloadStatusButton = (MultiStateButton) grid.findViewById(R.id.gridMultiStateButton);
//            gridDownloadStatusButton.setAsView(Magazine.STATUS_VIEW);
//
//        } else if (buttonState == 3) {
//            MultiStateButton gridDownloadStatusButton = (MultiStateButton) grid.findViewById(R.id.gridMultiStateButton);
//            String pauseText = AllDownloadsDataSet.getDownloadStatusText(AllDownloadsDataSet.DOWNLOAD_STATUS_PAUSED);
//            gridDownloadStatusButton.setAsView(pauseText);
//        } else if (buttonState == 1 || buttonState == 2) {
//            MultiStateButton gridDownloadStatusButton = (MultiStateButton) grid.findViewById(R.id.gridMultiStateButton);
//            String StatusText = AllDownloadsDataSet.getDownloadStatusText(AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW);
//            gridDownloadStatusButton.setAsView(StatusText);
//        }
//
//    }
//
//
//    @Override
//    public void onClick(View v) {
//
//        if(v.getId() == R.id.gridMultiStateButton){
//
//            // Based on Button status.
//            int pos = (int) v.getTag();
//            final int status = allDownloadsIssuesListTracker.get(pos).downloadStatus;
//
//            if(status == 4){
//
//                new AlertDialog.Builder(getActivity())
//                        .setTitle("Issue download is in queue!")
//                        .setMessage("You can view your Issue once download start.")
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .show();
//
//            }else if(status == 6 || status == 0){
//
//                String issueId = String.valueOf(allDownloadsIssuesListTracker.get(pos).issueID);
//
//                documentKey = getIssueDocumentKey(allDownloadsIssuesListTracker.get(pos).issueID);
//
//                Log.d(TAG,"Document Key is : " +documentKey);
//
//                Intent intent = new Intent(getActivity(),NewIssueView.class);
//                intent.putExtra("issueId",issueId);
//                intent.putExtra("documentKey",documentKey);
//                startActivity(intent);
//            }else if(status == 1 || status == 2){
//                String issueId = String.valueOf(allDownloadsIssuesListTracker.get(pos).issueID);
//
//                documentKey = getIssueDocumentKey(allDownloadsIssuesListTracker.get(pos).issueID);
//
//                Log.d(TAG,"Document Key is : " +documentKey);
//
//                Intent intent = new Intent(getActivity(),NewIssueView.class);
//                intent.putExtra("issueId",issueId);
//                intent.putExtra("documentKey",documentKey);
//                startActivity(intent);
//            }else if(status == -1){
//                new AlertDialog.Builder(getActivity())
//                        .setTitle("Error while downloading the issue.")
//                        .setMessage("Please trying downloading once again")
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .show();
//            }
//
//
//        }
//
//        if(v.getId() == R.id.moreDownloadOptionsMenuButton){
//            int pos = (int) v.getTag();
//            showPopup(v,pos);
//        }
//
//    }
//
//    public String getIssueDocumentKey(int issueId){
//
//        String issueKey = null;
//
//        MyIssueDocumentKey mDbReader = new MyIssueDocumentKey(BaseApp.getContext());
//        if(mDbReader != null) {
//            issueDocumentKeys = mDbReader.getMyIssuesDocumentKey(mDbReader.getReadableDatabase());
//            mDbReader.close();
//        }
//
//        for(int i=0; i<issueDocumentKeys.size(); i++){
//            if(issueId == issueDocumentKeys.get(i).issueID){
//                issueKey = issueDocumentKeys.get(i).documentKey;
//            }
//        }
//
//        return issueKey;
//    }
//
//
//    public void showPopup(View v, int listItemPopupPosition) {
//        listMenuItemPosition = listItemPopupPosition;
//        PopupMenu popup = new PopupMenu(getActivity(), v);
//        MenuInflater inflater = popup.getMenuInflater();
//        inflater.inflate(R.menu.alldownloadsoptionsmenu, popup.getMenu());
//        popup.setOnMenuItemClickListener(this);
//        popup.show();
//    }
//
//    @Override
//    public boolean onMenuItemClick(MenuItem item) {
//        switch (item.getItemId()) {
//
//            case R.id.download_menu_pause:
//
//                DownloadsManager.getInstance().downLoadPaused();
//
////                AllDownloadsDataSet mDbReader = new AllDownloadsDataSet(BaseApp.getContext());
////                mDbReader.setIssueToPaused(mDbReader.getReadableDatabase(), allDownloadsIssuesListTracker.get(listMenuItemPosition));
////                mDbReader.close();
//
//                allDownloadsIssuesListTracker.get(listMenuItemPosition).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_PAUSED;
//                MultiStateButton gridDownloadStatusButton1 = (MultiStateButton) grid.findViewById(R.id.gridMultiStateButton);
//                String pausedStatusText1 = AllDownloadsDataSet.getDownloadStatusText(AllDownloadsDataSet.DOWNLOAD_STATUS_PAUSED);
//                gridDownloadStatusButton1.setAsDownload(pausedStatusText1);
//                Log.d(TAG,"Jump Time is : " +jumpTime);
//                progressBar.setProgress(jumpTime);
//                run = false;
//
//                notifyDataSetChanged();
//                gridDownloadAdapter.notifyDataSetChanged();
//
//                break;
//
//            case R.id.download_menu_resume:
//
//                DownloadsManager.getInstance().downLoadResume();
//                allDownloadsIssuesListTracker.get(listMenuItemPosition).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW;
//                MultiStateButton gridDownloadStatusButton = (MultiStateButton) grid.findViewById(R.id.gridMultiStateButton);
//                String resumeStatusText = AllDownloadsDataSet.getDownloadStatusText(AllDownloadsDataSet.DOWNLOAD_STATUS_VIEW);
//                gridDownloadStatusButton.setAsView(resumeStatusText);
//                run = true;
//
//                notifyDataSetChanged();
//                gridDownloadAdapter.notifyDataSetChanged();
//
//                break;
//
//            case R.id.download_menu_delete:
//
//
//                try {
//
//                    // Deleting Thumbnail Images
//                    Log.d(TAG, "Thumb nail image before delete is : " + DownloadThumbnails.getIssueDownloadedThumbnailStorageDirectory
//                            (String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID)));
//                    DownloadsManager.getInstance().downLoadPaused();
//                    deleteThumbnail(DownloadThumbnails.getIssueDownloadedThumbnailStorageDirectory
//                            (String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID)));
//
//                    if(DownloadThumbnails.getIssueDownloadedThumbnailStorageDirectory
//                            (String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID)) != null)
//                    Log.d(TAG, "Thumb nail image after delete is : " + allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID);
//
//
//                    // Deleting all the downloaded pages
//                    AllDownloadsDataSet mDownloadReader = new AllDownloadsDataSet(BaseApp.getContext());
//                    AllDownloadsIssueTracker allDownloadsTracker = mDownloadReader.getAllDownloadsTrackerForIssue(mDownloadReader.getReadableDatabase(),
//                            String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID));
//                    mDownloadReader.close();
//
//
//                    if(allDownloadsTracker != null) {
//
//                        SingleIssueDownloadDataSet mDbDownloadTableReader = new SingleIssueDownloadDataSet(BaseApp.getContext());
//                        mDbDownloadTableReader.dropUniqueDownloadsTable(mDbDownloadTableReader.getWritableDatabase(), allDownloadsTracker.uniqueIssueDownloadTable);
//                        mDbDownloadTableReader.close();
//                    }
//
//                    // Deleting the all download data set table
//
//                    AllDownloadsDataSet allDownloadsDataSet = new AllDownloadsDataSet(BaseApp.getContext());
//                    allDownloadsDataSet.deleteIssueFromTable(allDownloadsDataSet.getWritableDatabase(),
//                            String.valueOf(allDownloadsIssuesListTracker.get(listMenuItemPosition).issueID));
//                    allDownloadsDataSet.close();
//
//                    allDownloadsIssuesListTracker.get(listMenuItemPosition).downloadStatus = AllDownloadsDataSet.DOWNLOAD_STATUS_FAILED;
//                    allDownloadsIssuesListTracker.remove(allDownloadsIssuesListTracker.get(listMenuItemPosition));
//
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//                gridDownloadAdapter.notifyDataSetChanged();
//                break;
//
//            default:
//                break;
//
//        }
//        return true;
//    }
//}


    /*
 This is defined in the allDownloadsFragment layout xml
*/



    /**
     *
     * Represents an asynchronous task used to fetch all the issues.
     *
     */
    public class GetAllDownloadedIssuesTask extends AsyncTask<String, String, String> {

        private final String mMagazineID;

        GetAllDownloadedIssuesTask(String MagazineID) {
            mMagazineID = MagazineID;
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

            setGridAdapter();

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



//    public void displayMagazineInGrid(int index){
//
//        // update the Grid View Adapter here
//
//        if(gridDownloadAdapter!=null){
//            gridDownloadAdapter.notifyDataSetChanged();
//        }
//
//    }

 // end of class
}

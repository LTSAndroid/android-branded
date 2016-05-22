package com.pixelmags.android.IssueView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pixelmags.android.IssueView.decode.Base64Utils;
import com.pixelmags.android.IssueView.decode.IssueDecode;
import com.pixelmags.android.datamodels.AllDownloadsIssueTracker;
import com.pixelmags.android.datamodels.SingleDownloadIssueTracker;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.AllDownloadsDataSet;
import com.pixelmags.android.storage.SingleIssueDownloadDataSet;
import com.pixelmags.android.util.BaseApp;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

import static java.lang.Character.digit;

/**
 * Created by Annie on 24/01/2016.
 */
public class NewIssueView extends FragmentActivity {


    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    public String issueID;
    public String documentKey;
    //

    AllDownloadsIssueTracker allDownloadsTracker;

    //
    //public static final String[] IMAGE_NAME = {"magone", "magtwo", "magthree", "magfour", "magfive", "magsix","magone","magtwo"};
    private static ArrayList<String> issuePagesLocations;

    // TODO : get the decrypt key and store here
    private String decrypt_key;
    private String TAG = "NewIssueView";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

//        //
//        String issueID = String.valueOf(120974);
//        //

 //       issueID = getIntent().getExtras().getString("issueId");

        issueID = "120997";
        documentKey ="pBBiBXvT96IffZ+gVFRd3EAqyA1juCV2pfNebwZsWbo="; // the key for 120997

        AllDownloadsDataSet mDownloadReader = new AllDownloadsDataSet(BaseApp.getContext());
        allDownloadsTracker = mDownloadReader.getAllDownloadsTrackerForIssue(mDownloadReader.getReadableDatabase(), issueID);
        mDownloadReader.close();


        issuePagesLocations = new ArrayList<String>();
        if(allDownloadsTracker != null) {

            SingleIssueDownloadDataSet mDbDownloadTableReader = new SingleIssueDownloadDataSet(BaseApp.getContext());
            ArrayList<SingleDownloadIssueTracker> allPagesOfIssue = mDbDownloadTableReader.getUniqueSingleIssueDownloadTable(mDbDownloadTableReader.getReadableDatabase(), allDownloadsTracker.uniqueIssueDownloadTable);
            mDbDownloadTableReader.close();

            if (allPagesOfIssue != null)
            {
                for (int i = 0; i < allPagesOfIssue.size() ; i++)
                {
                    String finalPath = allPagesOfIssue.get(i).downloadedLocationPdfLarge;
                    Log.d(TAG,"Final Path of pdf is : "+finalPath);
                    issuePagesLocations.add(finalPath);
                }
            }
        }


        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(imageFragmentPagerAdapter);


    }



    public class ImageFragmentPagerAdapter extends FragmentStatePagerAdapter {

        /*
            FragmentStatePagerAdapter more useful when there are a large number of pages, working more like a list view. When pages are not visible to the user,
            their entire fragment may be destroyed, only keeping the saved state of that fragment. This allows the pager to hold on to much less
            memory associated with each visited page as compared to FragmentPagerAdapter at the cost of potentially more overhead when switching between pages.
         */

        public ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            if(issuePagesLocations == null){
                return 0;
            }

            return issuePagesLocations.size();
        }

        @Override
        public Fragment getItem(int position) {
            SwipeFragment fragment = new SwipeFragment();
            return fragment.newInstance(position);
        }

    }

    public class SwipeFragment extends Fragment
    {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View swipeView = inflater.inflate(R.layout.swipe_fragment, container, false);
            ImageView imageView = (ImageView) swipeView.findViewById(R.id.imageView);
            Bundle bundle = getArguments();

            int position = bundle.getInt("position");

            Log.d(TAG,"Issue page locations is : " +issuePagesLocations.get(position));

            Bitmap imageForView = null;
            String imageLocation = issuePagesLocations.get(position);
            if(imageLocation != null){
                imageForView =  decryptFile(imageLocation, documentKey);
                Log.d(TAG,"Image for view is : "+imageForView);
                if(imageForView != null){
                    imageView.setImageBitmap(imageForView);
                }else{
                    System.out.println("Issue Image is Null");
                }
            }

            return swipeView;
        }

        public SwipeFragment newInstance(int position) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }
    }


    public Bitmap decryptFile(String path, String documentKey){

        Bitmap bitmap = null;

        try {
            // Create FileInputStream to read from the encrypted image file
            FileInputStream fis = new FileInputStream(path);

            // Decode and Save the decrypted image
            IssueDecode decoder = new IssueDecode();

//            byte[] bitmapdata =  decrypt( utils.getDocumentKeyDecryptedArray(encodedString), fis);
            byte[] bitmapdata =  decoder.getDecodedBitMap(documentKey, fis);

            fis.close();

            if(bitmapdata != null) {
                bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata.length);
            }
            

        } catch (Exception e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }



}
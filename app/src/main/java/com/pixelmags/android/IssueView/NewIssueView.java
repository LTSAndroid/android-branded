package com.pixelmags.android.IssueView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pixelmags.android.IssueView.decode.IssueDecode;
import com.pixelmags.android.datamodels.AllDownloadsIssueTracker;
import com.pixelmags.android.datamodels.SingleDownloadIssueTracker;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.AllDownloadsDataSet;
import com.pixelmags.android.storage.SingleIssueDownloadDataSet;
import com.pixelmags.android.util.BaseApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Character.digit;

/**
 * Created by Annie on 24/01/2016.
 */
public class NewIssueView extends FragmentActivity {


    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    public String issueID;
    //

    AllDownloadsIssueTracker allDownloadsTracker;

    //
    //public static final String[] IMAGE_NAME = {"magone", "magtwo", "magthree", "magfour", "magfive", "magsix","magone","magtwo"};
    private static ArrayList<String> issuePagesLocations;

    // TODO : get the decrypt key and store here
    private String decrypt_key;
    private static String TAG = "NewIssueView";
    private static String documentKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

//        //
//        String issueID = String.valueOf(120974);
//        //

        issueID = getIntent().getExtras().getString("issueId");
        documentKey = getIntent().getExtras().getString("documentKey");
        Log.d(TAG,"Issue Id is : "+issueID);
        Log.d(TAG,"Document Key is : "+documentKey);

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

    public static class SwipeFragment extends Fragment
    {

        private byte[] bitmap;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View swipeView = inflater.inflate(R.layout.swipe_fragment, container, false);
            IssueImagePinchZoom imageView = (IssueImagePinchZoom) swipeView.findViewById(R.id.imageView);
            Bundle bundle = getArguments();

            int position = bundle.getInt("position");

            Log.d(TAG,"Issue page locations is : " +issuePagesLocations.get(position));


            Bitmap imageForView = null;
            String imageLocation = issuePagesLocations.get(position);
            if(imageLocation != null){

//                bitmap =  decryptFilebyte(imageLocation, documentKey);
//                BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(imageView,bitmap);
//                bitmapWorkerTask.execute();


                imageForView =  decryptFile(imageLocation,documentKey);

                Log.d(TAG, "Image for view is : " + imageForView);

//                saveImage(imageForView);


                if(imageForView != null){
                    imageView.setImageBitmap(imageForView);
                }else{

                    System.out.println("Issue Image is Null");
                }
            }else{
                imageView.setBackgroundResource(R.drawable.placeholderissueview);
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

        private void saveImage(Bitmap finalBitmap) {

            Log.d(TAG,"Inside the save image method");

            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/brand_now");
            Log.d(TAG,"File path is : "+myDir);
            if(!myDir.exists()){
                myDir.mkdirs();
            }

//            String stf = myDir.getAbsolutePath();
//            File file = new File(stf +"/brand-now");
//
//            if(!file.exists()){
//                file.mkdir();
//            }


            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fname = "Image-"+ n +".png";
            File file = new File (myDir, fname);
            Log.d(TAG,"File name is : "+file);
            if (file.exists ()) file.delete ();
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    }




    private byte[] stringToBytes(String input) {

        int length = input.length();
        byte[] output = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            output[i / 2] = (byte) ((digit(input.charAt(i), 16) << 4) | digit(input.charAt(i+1), 16));
        }
        return output;

    }

    public static Bitmap decryptFile(String path, String documentKey){

        Bitmap bitmap = null;

        try {
            // Create FileInputStream to read from the encrypted image file
            FileInputStream fis = new FileInputStream(path);
            IssueDecode issueDecode = new IssueDecode();

            byte[] bitmapdata = issueDecode.getDecodedBitMap(documentKey,fis);

            fis.close();

            Log.d(TAG, "Bitmap data is : " + bitmapdata.toString());



            if(bitmapdata != null) {
//                bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);

                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length,opts);
//                Log.d(TAG,"Source is : "+source);
//                mask = mask.extractAlpha();
//                source.recycle();

                Log.d(TAG,"Bitmap is : "+bitmap);
            }
            

        } catch (Exception e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }

    public static byte[] decryptFilebyte(String path, String documentKey){

        Bitmap bitmap = null;
        byte[] bitmapdata = new byte[0];

        try {
            // Create FileInputStream to read from the encrypted image file
            FileInputStream fis = new FileInputStream(path);
            IssueDecode issueDecode = new IssueDecode();

            bitmapdata = issueDecode.getDecodedBitMap(documentKey,fis);

            fis.close();

            Log.d(TAG, "Bitmap data is : " + bitmapdata.toString());


        } catch (Exception e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmapdata;
    }


    static class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private byte[] bitmap;

        public BitmapWorkerTask(ImageView imageView,byte[] bitmap) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
            this.bitmap = bitmap;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            return BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    Log.d(TAG,"Inside the onPost execute method");
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }


}
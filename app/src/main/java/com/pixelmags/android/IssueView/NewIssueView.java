package com.pixelmags.android.IssueView;

import android.content.res.Configuration;
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

import com.crashlytics.android.Crashlytics;
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

import io.fabric.sdk.android.Fabric;

import static java.lang.Character.digit;

/**
 * Created by Annie on 24/01/2016.
 */
public class NewIssueView extends FragmentActivity {


    public static int xDim, yDim;
    public static boolean issueViewOpen = false;
    //
    //public static final String[] IMAGE_NAME = {"magone", "magtwo", "magthree", "magfour", "magfive", "magsix","magone","magtwo"};
    private static ArrayList<String> issuePagesLocations;
    private static String TAG = "NewIssueView";
    private static String documentKey;
    //
    public String issueID;
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    AllDownloadsIssueTracker allDownloadsTracker;
    // TODO : get the decrypt key and store here
    private String decrypt_key;

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
                opts.inJustDecodeBounds = true;
                opts.inSampleSize = calculateInSampleSize(opts,xDim, yDim);
                opts.inJustDecodeBounds = false;
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


    //Given the bitmap size and View size calculate a subsampling size (powers of 2)
    static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int inSampleSize = 1;	//Default subsampling size
        // See if image raw height and width is bigger than that of required view
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            //bigger
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);
        Fabric.with(this, new Crashlytics());

//        //
//        String issueID = String.valueOf(120974);
//        //

        issueViewOpen = true;

        issueID = getIntent().getExtras().getString("issueId");
        documentKey = getIntent().getExtras().getString("documentKey");

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

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);

        Log.d(TAG,"Table Size Value is : "+ tabletSize);

        if (tabletSize) {
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
                viewPager = (ViewPager) findViewById(R.id.pager);
                viewPager.setAdapter(imageFragmentPagerAdapter);
                viewPager.setOffscreenPageLimit(6);
            }else{
                imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
                viewPager = (ViewPager) findViewById(R.id.pager);
                viewPager.setAdapter(imageFragmentPagerAdapter);
            }

        } else {

                imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
                viewPager = (ViewPager) findViewById(R.id.pager);
                viewPager.setAdapter(imageFragmentPagerAdapter);

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

    public static class SwipeFragment extends Fragment
    {

        IssueImagePinchZoom imageView;
        private byte[] bitmap;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View swipeView = inflater.inflate(R.layout.swipe_fragment, container, false);
            imageView = (IssueImagePinchZoom) swipeView.findViewById(R.id.imageView);
            Bundle bundle = getArguments();

            int position = bundle.getInt("position");

            Log.d(TAG,"Issue page locations is : " +issuePagesLocations.get(position));

            Bitmap imageForView = null;
            String imageLocation = issuePagesLocations.get(position);

            xDim = imageView.getWidth();
            yDim = imageView.getHeight();

            if(imageLocation != null){



//                bitmap =  decryptFilebyte(imageLocation, documentKey);
//                BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(imageView,bitmap);
//                bitmapWorkerTask.execute();


                imageForView =  decryptFile(imageLocation,documentKey);

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

        @Override
        public float getPageWidth(int position) {
            boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
            if (tabletSize) {
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                    return(1f/2f);
                }else{
                    return(1f/1f);
                }

            } else {
                    return(1f/1f);
            }


        }

    }


}
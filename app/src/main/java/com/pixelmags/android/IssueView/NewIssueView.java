package com.pixelmags.android.IssueView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
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
    //

    AllDownloadsIssueTracker allDownloadsTracker;

    //
    //public static final String[] IMAGE_NAME = {"magone", "magtwo", "magthree", "magfour", "magfive", "magsix","magone","magtwo"};
    private static ArrayList<String> issuePagesLocations;

    // TODO : get the decrypt key and store here
    private String decrypt_key;
    private String TAG = "NewIssueView";
    private String documentKey;


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
                imageForView =  decryptFile(imageLocation,documentKey);

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


    private byte[] stringToBytes(String input) {

        int length = input.length();
        byte[] output = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            output[i / 2] = (byte) ((digit(input.charAt(i), 16) << 4) | digit(input.charAt(i+1), 16));
        }
        return output;

    }

    public Bitmap decryptFile(String path,String documentKey){

        Bitmap bitmap = null;

        try {
            // Create FileInputStream to read from the encrypted image file
            FileInputStream fis = new FileInputStream(path);
            IssueDecode issueDecode = new IssueDecode();

            byte[] bitmapdata = issueDecode.getDecodedBitMap(documentKey,fis);

            // Save the decrypted image
//            String encodedString = "1Ef95C6MaqkeDKBEuLuN49LV32FED/SkQHepcNEIUd0=";

//            encodedString = "C604DF8833E8CCAACAC44B51C195B1CB8CBD6AB6085FD3EC6A07E26CBCB55662";
//            encodedString = "KOiRC9ojNJmrQaYQd5YN0N46jL2WduwT+UFYv0J4wUA=";

//           String encodedString = "28E8910BDA233499AB41A61077960DD0DE3A8CBD9676EC13F94158BF4278C140";
//
//            byte[] bitmapdata =  decrypt( stringToBytes(encodedString), fis);
//
//            Log.d(TAG,"Bitmap Data is : " +bitmapdata);

            fis.close();

            if(bitmapdata != null) {
                bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                Log.d(TAG,"Bitmap is : "+bitmap);
            }
            

        } catch (Exception e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
    }

    private byte[] decrypt(byte[] skey, FileInputStream fis){


        SecretKeySpec skeySpec = new SecretKeySpec(skey, "AES");
        Cipher cipher;
        byte[] decryptedData=null;
        CipherInputStream cis=null;

        try {

            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            // Changed when Cipher error was coming
//            cipher = Cipher.getInstance("AES/CFB8/NoPadding");
//            Log.d(TAG,"Cipher Instance is : " +cipher);
//            Log.d(TAG,"Byte length is : " +skey.length);
//            IvParameterSpec ivSpeck = new IvParameterSpec(skey);
//            cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpeck);

            //Old one
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);

            // Create CipherInputStream to read and decrypt the image data
            cis = new CipherInputStream(fis, cipher);
            Log.d(TAG,"Cipher Input Stream is : " +cis);
            // Write encrypted image data to ByteArrayOutputStream
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            byte[] data = new byte[4096];

            Log.d(TAG,"Cis Read data is : "+cis.read(data));

            while ((cis.read(data)) != -1) {

                buffer.write(data);
            }

            buffer.flush();

            decryptedData=buffer.toByteArray();

        }catch(Exception e){
            e.printStackTrace();
        }
        finally{

            try {
                fis.close();

                cis.close();

            } catch (IOException e) {

                // TODO Auto-generated catch block

                e.printStackTrace();
            }
        }
        return decryptedData;

    }

}
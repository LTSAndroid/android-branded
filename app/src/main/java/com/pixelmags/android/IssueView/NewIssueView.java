package com.pixelmags.android.IssueView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pixelmags.android.datamodels.AllDownloadsIssueTracker;
import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.datamodels.SingleDownloadIssueTracker;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.AllDownloadsDataSet;
import com.pixelmags.android.storage.SingleIssueDownloadDataSet;
import com.pixelmags.android.util.BaseApp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static java.lang.Character.digit;

/**
 * Created by Annie on 24/01/2016.
 */
public class NewIssueView extends FragmentActivity {
    static final int NUM_ITEMS = 8; // here number of items will be based on collection
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    public String issueID;
    //
    AllDownloadsIssueTracker allDownloadsTracker;
    AllDownloadsDataSet mDownloadReader = new AllDownloadsDataSet(BaseApp.getContext());


    //
    public static final String[] IMAGE_NAME = {"magone", "magtwo", "magthree", "magfour", "magfive", "magsix","magone","magtwo"};
    public static ArrayList<Bitmap> issuePages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);
        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(imageFragmentPagerAdapter);

        //
        String issueID = String.valueOf(120974);
        //
        allDownloadsTracker = mDownloadReader.getAllDownloadsTrackerForIssue(mDownloadReader.getReadableDatabase(), issueID);
        mDownloadReader.close();
        issuePages = new ArrayList<Bitmap>();
        if(allDownloadsTracker != null) {

            SingleIssueDownloadDataSet mDbDownloadTableReader = new SingleIssueDownloadDataSet(BaseApp.getContext());
            ArrayList<SingleDownloadIssueTracker> allPagesOfIssue = mDbDownloadTableReader.getUniqueSingleIssueDownloadTable(mDbDownloadTableReader.getReadableDatabase(), allDownloadsTracker.uniqueIssueDownloadTable);
            mDbDownloadTableReader.close();

            if (allPagesOfIssue != null)
            {
                //allPagesOfIssue.size()
                for (int i = 0; i < 5 ; i++)
                {
                    String finalPath = allPagesOfIssue.get(i).downloadedLocationPdfLarge;

                    Bitmap loadedIssuePage =  decryptFile(finalPath);

                    if(loadedIssuePage == null)
                        System.out.println("Issue Image is Null");

                    issuePages.add(loadedIssuePage);

                }
            }
        }


    }



    public static class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
        public ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            SwipeFragment fragment = new SwipeFragment();
            return SwipeFragment.newInstance(position);
        }
    }

    public static class SwipeFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View swipeView = inflater.inflate(R.layout.swipe_fragment, container, false);
            ImageView imageView = (ImageView) swipeView.findViewById(R.id.imageView);
            Bundle bundle = getArguments();
            int position = bundle.getInt("position");
            String imageFileName = IMAGE_NAME[position];
            int imgResId = getResources().getIdentifier(imageFileName, "drawable", "com.pixelmags.android.pixelmagsapp");

            Bitmap imageForView = issuePages.get(position);
            /*String path = getIntent().getStringExtra("imagePath");
            Drawable image = Drawable.createFromPath(path);
            myImageView.setImageDrawable(image);
*/
            imageView.setImageBitmap(imageForView);
            return swipeView;
        }

        static SwipeFragment newInstance(int position) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }


    }


    private static byte[] stringToBytes(String input) {

        int length = input.length();
        byte[] output = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            output[i / 2] = (byte) ((digit(input.charAt(i), 16) << 4) | digit(input.charAt(i+1), 16));
        }
        return output;

    }

    public Bitmap decryptFile( String path){

        Bitmap bitmap = null;

        try {
            // Create FileInputStream to read from the encrypted image file
            FileInputStream fis = new FileInputStream(path);

            // Save the decrypted image
            String encodedString = "1Ef95C6MaqkeDKBEuLuN49LV32FED/SkQHepcNEIUd0=";

            encodedString = "C604DF8833E8CCAACAC44B51C195B1CB8CBD6AB6085FD3EC6A07E26CBCB55662";

            byte[] bitmapdata =  decrypt( stringToBytes(encodedString), fis);

            fis.close();

            bitmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata.length);
            

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
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);

            // Create CipherInputStream to read and decrypt the image data
            cis = new CipherInputStream(fis, cipher);

            // Write encrypted image data to ByteArrayOutputStream
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            byte[] data = new byte[4096];

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

    private static byte[] getIV(){


        SecureRandom random = new SecureRandom();


        byte[] iv = random.generateSeed(16);


        return iv;


    }

    public Bitmap loadImageFromStorage(String path)
    {
        Bitmap issuePage;
        issuePage = BitmapFactory.decodeFile(path);

        return issuePage;

    }
}
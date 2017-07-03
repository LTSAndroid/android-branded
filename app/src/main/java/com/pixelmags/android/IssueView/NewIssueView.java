package com.pixelmags.android.IssueView;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;
import com.pixelmags.android.IssueView.decode.IssueDecode;
import com.pixelmags.android.comms.Config;
import com.pixelmags.android.datamodels.AllDownloadsIssueTracker;
import com.pixelmags.android.datamodels.Bookmark;
import com.pixelmags.android.datamodels.PreviewImage;
import com.pixelmags.android.datamodels.SingleDownloadIssueTracker;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.AllDownloadsDataSet;
import com.pixelmags.android.storage.BookmarkDataSet;
import com.pixelmags.android.storage.SingleIssueDownloadDataSet;
import com.pixelmags.android.storage.SingleIssuePreviewDataSet;
import com.pixelmags.android.util.BaseApp;

import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

import static java.lang.Character.digit;

/**
 * Created by Annie on 24/01/2016.
 */
public class NewIssueView extends FragmentActivity implements View.OnClickListener {


    public static int xDim, yDim;
    public static boolean issueViewOpen = false;
    public static String issueID;
    public static int currentPageNumber;
    public static byte[] issueImage = null;
    public static BookmarkPage bookmarkPage;
    public static ViewPager viewPager;
    public static RecyclerView mRecyclerView;
    public static LinearLayout previewImagesLayout;
    public static ImageView share;
    public static ImageView bookmark;
    private static ArrayList<String> issuePagesLocations;
    private static String TAG = "NewIssueView";
    private static String documentKey;
    private static Handler handler;
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    PageListener pageListener;
    AllDownloadsIssueTracker allDownloadsTracker;
    // TODO : get the decrypt key and store here
    private String decrypt_key;
    private DownloadPreviewImagesAsyncTask mPreviewImagesTask = null;
    private ImageView previewImageView;
    private ArrayList<PreviewImage> previewImageArrayList;

    public static byte[] decryptFile(String path, String documentKey){

        byte[] bitmapdata = null;

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

    public static void getScreenResolution(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        xDim = metrics.widthPixels;
        yDim = metrics.heightPixels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.fragment_pager);
        Fabric.with(this, new Crashlytics());

        previewImagesLayout = (LinearLayout) findViewById(R.id.issuePagesPreviewImageLayout);

        share = (ImageView) findViewById(R.id.share);
        share.setOnClickListener(this);

        bookmark = (ImageView) findViewById(R.id.bookmark);
        bookmark.setOnClickListener(this);

//        //
//        String issueID = String.valueOf(120974);
//        //

        issueViewOpen = true;
        currentPageNumber = 0;

        issueID = getIntent().getExtras().getString("issueId");
        documentKey = getIntent().getExtras().getString("documentKey");

        // To See Preview Images
        loadPreviewImages(NewIssueView.this);


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


        if (tabletSize) {
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
                viewPager = (ViewPager) findViewById(R.id.pager);
                viewPager.setAdapter(imageFragmentPagerAdapter);
                pageListener = new PageListener();
                viewPager.setOnPageChangeListener(pageListener);
                viewPager.setOffscreenPageLimit(6);
            }else{
                imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
                viewPager = (ViewPager) findViewById(R.id.pager);
                viewPager.setAdapter(imageFragmentPagerAdapter);
                pageListener = new PageListener();
                viewPager.setOnPageChangeListener(pageListener);
            }

        } else {

            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
                viewPager = (ViewPager) findViewById(R.id.pager);
                viewPager.setAdapter(imageFragmentPagerAdapter);
                pageListener = new PageListener();
                viewPager.setOnPageChangeListener(pageListener);
                viewPager.setOffscreenPageLimit(6);
            }else{

                imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
                viewPager = (ViewPager) findViewById(R.id.pager);
                viewPager.setAdapter(imageFragmentPagerAdapter);
                pageListener = new PageListener();
                viewPager.setOnPageChangeListener(pageListener);
            }

        }


    }

    public void loadPreviewImages(Activity activity){

        mPreviewImagesTask = new DownloadPreviewImagesAsyncTask(Config.Magazine_Number, String.valueOf(issueID), activity);
        mPreviewImagesTask.execute((String) null);

    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.share){

            String url;
//                if(currentPageNumber == 0){
//                    url = "Take a look at this magazine in "+Config.Magazine_Title+"\n"+Config.Sharing_URL+"/"+Config.Magazine_Number+"/"+issueID+"/1";
//
//                }else{
            url = "Take a look at this magazine in "+Config.Magazine_Title+"\n"+Config.Sharing_URL+"/"+Config.Magazine_Number+"/"+issueID+"/"+(currentPageNumber);

//                }

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);

            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, url);
            startActivity(Intent.createChooser(intent, "Share"));
        }


        if(view.getId() == R.id.bookmark){

            issueImage  =  decryptFile(issuePagesLocations.get(currentPageNumber),documentKey);
            BitmapWorkerTaskForStorage bitmapWorkerTaskForStorage = new BitmapWorkerTaskForStorage(issueImage,NewIssueView.this);
            bitmapWorkerTaskForStorage.execute();

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

    private static class PageListener extends ViewPager.SimpleOnPageChangeListener {
        public void onPageSelected(int position) {
            Log.d(TAG, "Current page selected is " + position);
            currentPageNumber = position;
        }
    }

    public static class SwipeFragment extends Fragment implements View.OnClickListener {

        IssueImagePinchZoom imageView;
        private byte[] bitmap;
        private int position;



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View swipeView = inflater.inflate(R.layout.swipe_fragment, container, false);
            imageView = (IssueImagePinchZoom) swipeView.findViewById(R.id.imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(share.getVisibility() == View.VISIBLE && bookmark.getVisibility() == View.VISIBLE){
                        share.setVisibility(View.GONE);
                        bookmark.setVisibility(View.GONE);
                        previewImagesLayout.setVisibility(View.GONE);
                    }else{
                        share.setVisibility(View.VISIBLE);
                        bookmark.setVisibility(View.VISIBLE);
                        previewImagesLayout.setVisibility(View.VISIBLE);
                    }

                }
            });


            Bundle bundle = getArguments();

            position = bundle.getInt("position");

            Log.d(TAG,"Issue page locations is : " +position);

//            Bitmap imageForView = null;
            byte[] imageForView = null;
            String imageLocation = issuePagesLocations.get(position);

            if(getActivity() != null) {
                getScreenResolution(getActivity());
            }else{
                xDim = imageView.getWidth();
                yDim = imageView.getHeight();
            }


            if(imageLocation != null){

                imageForView =  decryptFile(imageLocation,documentKey);

                BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(imageView,imageForView);
                bitmapWorkerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


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

        @Override
        public void onClick(View view) {

        }

    }

    static class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private byte[] bitmap;
        private Bitmap bitmapImage;

        public BitmapWorkerTask(ImageView imageView,byte[] bitmap) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
            this.bitmap = bitmap;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            opts.inSampleSize = calculateInSampleSize(opts,xDim, yDim);
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;// Changed for better memory usage

            issueImage = bitmap;

            bitmapImage = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length,opts);
            return bitmapImage;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmapImage);
                }
            }
        }
    }

    static class BitmapWorkerTaskForStorage extends AsyncTask<Integer, Void, Bitmap> {

        private byte[] bitmap;
        private Activity activity;

        public BitmapWorkerTaskForStorage(byte[] bitmap,Activity activity) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            this.bitmap = bitmap;
            this.activity = activity;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            opts.inSampleSize = calculateInSampleSize(opts,xDim, yDim);
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;// Changed for better memory usage

            issueImage = bitmap;

           return null;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {

            android.app.FragmentManager fm = activity.getFragmentManager();
            bookmarkPage = new BookmarkPage();
            Bundle args = new Bundle();
            args.putString("issueId", issueID);
            args.putByteArray("issueImage",issueImage);
            args.putInt("pagePos",currentPageNumber);
            bookmarkPage.setArguments(args);
            bookmarkPage.show(fm, "Example");

        }
    }

    public static class BookmarkPage extends DialogFragment implements View.OnClickListener {

        public ImageButton addToBookmark;
        View view;
        private BookmarkListAdapter bookmarkListAdapter = null;
        private String issueId;
        private int pageNumber;
        private byte[] issueImage;
        private Bitmap bitmapImage;
        private int xDim, yDim;
        private ArrayList<Bookmark> bookmarkData;
        private boolean bookmarked = false;

        public BookmarkPage() {
            // Required empty public constructor
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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

            View view = inflater.inflate(R.layout.fragment_bookmark_page, container, false);

            mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
            addToBookmark = (ImageButton) view.findViewById(R.id.bookmark_current_page);
            addToBookmark.setOnClickListener(this);

            issueId = getArguments().getString("issueId");
            issueImage = getArguments().getByteArray("issueImage");
            pageNumber = getArguments().getInt("pagePos");
            setIssueImage(issueImage);

            BookmarkDataSet mDbReader = new BookmarkDataSet(BaseApp.getContext());
            bookmarkData = mDbReader.getIssueBookmark(mDbReader.getReadableDatabase(), issueId);
            mDbReader.close();

            if(bookmarkData.size()>0){
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                bookmarkListAdapter = new BookmarkListAdapter(getActivity(),bookmarkData,bitmapImage,String.valueOf(pageNumber),issueId);
                mRecyclerView.setAdapter(bookmarkListAdapter);
            }

            return  view;
        }

        public void setIssueImage(byte[] bitmap){

            WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            xDim = metrics.widthPixels;
            yDim = metrics.heightPixels;

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            opts.inSampleSize = calculateInSampleSize(opts,xDim, yDim);
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;// Changed for better memory usage
            bitmapImage = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length,opts);
        }

        @Override
        public void onClick(View view) {

            if(view.getId() == R.id.bookmark_current_page){

                Bookmark bookmark = new Bookmark(Integer.valueOf(issueId),pageNumber,issueImage);

                BookmarkDataSet mDbHelper = new BookmarkDataSet(BaseApp.getContext());
                ArrayList<Bookmark> bookmarkData = mDbHelper.getIssueBookmark(mDbHelper.getReadableDatabase(), issueId);

                for(int i=0; i<bookmarkData.size(); i++){

                    if(bookmarkData.get(i).pageNumber == Integer.valueOf(pageNumber)){
                        bookmarked = true;
                    }
                }

                if(!bookmarked){
                    mDbHelper.insertIssueBookmarkData(mDbHelper.getWritableDatabase(), bookmark);
                    ArrayList<Bookmark> bookmarkDataSecond = mDbHelper.getIssueBookmark(mDbHelper.getReadableDatabase(), issueId);
                    mDbHelper.close();

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    bookmarkListAdapter = new BookmarkListAdapter(getActivity(),bookmarkDataSecond,bitmapImage,
                            String.valueOf(pageNumber),issueId);
                    mRecyclerView.setAdapter(bookmarkListAdapter);
                }else{
                    mDbHelper.close();
                }

            }
        }
    }

    public static class BookmarkListAdapter  extends RecyclerView.Adapter<BookmarkListAdapter.ViewHolder> implements View.OnClickListener {

        private Activity activity;
        private ArrayList<Bookmark> bookmarkArrayList;
        private Bitmap issueImage;
        private String pageNumber,issueId;
        private String TAG = "BookmarkListAdapter";

        public BookmarkListAdapter(Activity activity, ArrayList<Bookmark> bookmarkArrayList, Bitmap issueImage,
                                   String pageNumber, String issueId) {
            this.activity = activity;
            this.bookmarkArrayList = bookmarkArrayList;
            this.issueImage = issueImage;
            this.pageNumber = pageNumber;
            this.issueId = issueId;
        }

        @Override
        public BookmarkListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bookmark_list_adapter_view, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final BookmarkListAdapter.ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            holder.cardView.setTag(position);
            holder.cardView.setOnClickListener(this);
            int pageNo = bookmarkArrayList.get(position).pageNumber+1;
            holder.bookmarkPage.setText(String.valueOf("Page No : "+pageNo));
            holder.issueImage.setImageBitmap(setIssueImage(bookmarkArrayList.get(position).pageImage));
            holder.removeBookmark.setTag(position);
            holder.removeBookmark.setOnClickListener(this);

        }

        public Bitmap setIssueImage(byte[] bitmap){

            WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            xDim = metrics.widthPixels;
            yDim = metrics.heightPixels;

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            opts.inSampleSize = calculateInSampleSize(opts,xDim, yDim);
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;// Changed for better memory usage
            issueImage = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length,opts);
            return issueImage;
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return bookmarkArrayList.size();
        }

        @Override
        public void onClick(View view) {

            if(view.getId() == R.id.card_view){
                Log.d(TAG,"Inside the onclick of list item");

                bookmarkPage.dismiss();

                int pos = (int) view.getTag();
                SwipeFragment fragment = new SwipeFragment();
                fragment.newInstance(bookmarkArrayList.get(pos).pageNumber);
                currentPageNumber = bookmarkArrayList.get(pos).pageNumber;
                viewPager.setCurrentItem(bookmarkArrayList.get(pos).pageNumber);

            }


            if(view.getId() == R.id.remove_bookmark){
                int pos = (int) view.getTag();

                int pageNo = bookmarkArrayList.get(pos).pageNumber;

                BookmarkDataSet mDbReader = new BookmarkDataSet(BaseApp.getContext());
                mDbReader.deleteBookmark(mDbReader.getWritableDatabase(), issueId, String.valueOf(pageNo));
                mDbReader.close();

                BookmarkDataSet mDbHelper = new BookmarkDataSet(BaseApp.getContext());
                bookmarkArrayList = mDbHelper.getIssueBookmark(mDbHelper.getReadableDatabase(), issueId);
                if(bookmarkArrayList.size() == 0){
                    mDbHelper.dropIssueTableData(mDbHelper.getWritableDatabase());
                    mDbHelper.close();
                }else{
                    mDbHelper.close();
                }

                notifyDataSetChanged();

            }


        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView bookmarkPage;
            public CardView cardView;
            public ImageView issueImage;
            public ImageButton removeBookmark;


            public ViewHolder(View itemView) {
                super(itemView);

                bookmarkPage = (TextView) itemView.findViewById(R.id.bookmark_page_no);
                cardView = (CardView) itemView.findViewById(R.id.card_view);
                issueImage = (ImageView) itemView.findViewById(R.id.issue_image);
                removeBookmark = (ImageButton) itemView.findViewById(R.id.remove_bookmark);

            }

        }

    }

    /**
     *
     * Represents an asynchronous task used to download an preview issues.
     *
     */


    public class DownloadPreviewImagesAsyncTask extends AsyncTask<String, String, String> {

        ArrayList<PreviewImage> previewImageArrayList;
        private String mIssueID;
        private String magID;
        private Activity activity;

        DownloadPreviewImagesAsyncTask(String magID, String issueID, Activity activity) {
            this.magID = magID;
            mIssueID = issueID;
            this.activity = activity;
            previewImageArrayList = null;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.

            try {

                SingleIssuePreviewDataSet mDbDownloadTableReader = new SingleIssuePreviewDataSet(BaseApp.getContext());

                previewImageArrayList = mDbDownloadTableReader.getUniqueSingleIssueDownloadTable(mDbDownloadTableReader.getReadableDatabase(),
                        "Preview_Issue_Table_"+magID+issueID);
                mDbDownloadTableReader.close();


            }catch (Exception e){
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {

            try{
                previewImagesLayout = (LinearLayout) activity.findViewById(R.id.issuePagesPreviewImageLayout);
                previewImagesLayout.removeAllViews();

                // start async task to download and update
                if(previewImageArrayList != null){
                    for (int i = 0; i < previewImageArrayList.size(); i++){

                        PreviewImage previewImage = previewImageArrayList.get(i);

//                        if(previewImage.previewImageBitmap != null) {

                            previewImageView = new ImageView(activity);
                            previewImageView.setId(i);
//                            imageView.setPadding(2, 2, 5, 2);
                            previewImageView.setPadding(10, 30, 10, 8);
                            previewImageView.setMinimumWidth(previewImage.imageWidth);
                            previewImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                            Log.d(TAG,"Preview Image URL is : "+previewImage.previewImageURL);
                            Glide.with(NewIssueView.this)
                                    .load(previewImage.previewImageURL)
                                    .crossFade()
                                    .override(previewImage.imageWidth,previewImage.imageHeight)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(previewImageView);

//                            previewImageView.setImageBitmap(previewImage.previewImageBitmap);
                            previewImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    currentPageNumber = view.getId();
                                    Log.d(TAG,"Current Page Number of on click image is : "+currentPageNumber);
                                    SwipeFragment fragment = new SwipeFragment();
                                    fragment.newInstance(currentPageNumber);

                                    viewPager.setCurrentItem(currentPageNumber);
                                    previewImagesLayout.setVisibility(View.GONE);
                                    if(share.getVisibility() == View.VISIBLE && bookmark.getVisibility() == View.VISIBLE){
                                        share.setVisibility(View.GONE);
                                        bookmark.setVisibility(View.GONE);
                                    }

                                }
                            });

                            previewImagesLayout.addView(previewImageView);

//                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
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
            Log.d(TAG,"Get Item Position is : "+position);
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
                if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                    return(1f/2f);
                }else{
                    return(1f/1f);
                }

            }


        }

    }


}
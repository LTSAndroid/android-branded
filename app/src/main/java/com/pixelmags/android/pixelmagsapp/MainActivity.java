package com.pixelmags.android.pixelmagsapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pixelmags.android.api.CanPurchase;
import com.pixelmags.android.comms.Config;
import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.datamodels.MyIssue;
import com.pixelmags.android.datamodels.MySubscription;
import com.pixelmags.android.pixelmagsapp.billing.CreatePurchaseTask;
import com.pixelmags.android.pixelmagsapp.service.PMService;
import com.pixelmags.android.pixelmagsapp.test.ResultsFragment;
import com.pixelmags.android.storage.AllIssuesDataSet;
import com.pixelmags.android.storage.MyIssuesDataSet;
import com.pixelmags.android.storage.MySubscriptionsDataSet;
import com.pixelmags.android.storage.UserPrefs;
import com.pixelmags.android.ui.LoginFragment;
import com.pixelmags.android.ui.NavigationDrawerFragment;
import com.pixelmags.android.ui.RegisterFragment;
import com.pixelmags.android.ui.SubscriptionsFragment;
import com.pixelmags.android.util.BaseApp;
import com.pixelmags.android.util.IabHelper;
import com.pixelmags.android.util.IabResult;
import com.pixelmags.android.util.Inventory;
import com.pixelmags.android.util.PMStrictMode;
import com.pixelmags.android.util.Purchase;
import com.pixelmags.android.util.SkuDetails;
import com.pixelmags.android.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*<<<<<<< Updated upstream*/
/*>>>>>>> Stashed changes*/

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, LoginFragment.OnFragmentInteractionListener ,
        RegisterFragment.OnFragmentInteractionListener, ResultsFragment.OnFragmentInteractionListener,
        SubscriptionsFragment.OnFragmentInteractionListener {

    public IabHelper mHelper;
    private ArrayList<Magazine> pixelmagsMagazinesList = null;
    public ArrayList<Magazine> billingMagazinesList;
    public ArrayList<Purchase> userOwnedSKUList;
    public ArrayList<String> skuList;
    private String TAG = "MainActivity";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * The service that will perform all the downloads in the background.
     * This is started whenever the App is launched and within the MainActivity.
     * The service should stop itself once all downloads are complete.
     */

    // Service Parameters
    private PMService mPMService;
    private ServiceConnection mConnection = null;
    boolean mIsBound = false;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    public CanPurchaseTask mCanPurchaseTask = null;
    public CreatePurchaseTask mCreatePurchaseTask = null;
    private int purchaseIssueId;
    private String purchaseSKU;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // start the service
        startDownloadService();

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // setting STRICT DEVELOPER MODE (Disable this for live apps)
        PMStrictMode.setStrictMode(Config.DEVELOPER_MODE);


        Util.doPreLaunchSteps();

        // This string is unique for each app
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhE2tqqq+WSoEXHyqOdeFjKFGgWVuhapArdTe/b0wzxAJE0pdsM8FlywwyIQlLd51hj6vvDkmd8T3dRi6LX2Ww2M8+fpK7jP3ydMyTZB9efuAiRZq2tlo2GmrFmO0vTdD0MkY4OdX9ROEvY9k/cbzXX73uNH0FAcZ38ypr/qf66IS2yI+z+Oiip7c39pDrG0P4kVamJQOjs7PLTmtwU1PWc43phqISxxpLJWxj0yW/YjfZ7Knk5n84p02CpDJcoZXdsBu7X4GOc79DRURDHuLu3tgkp3roXTQeX6y4Ht9843Hu5rSRgADQ/5828+SozdhIAhQ4CT/MZ0w0NEd0/OitwIDAQAB";
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new
                                   IabHelper.OnIabSetupFinishedListener()
                                   {
                                       public void onIabSetupFinished(IabResult result)
                                       {
                                           if (!result.isSuccess())
                                           {
                                               //failed
                                           }
                                           else
                                           {
                                               //success
                                           }
                                       }
                                   });


        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener()
        {
            public void onIabSetupFinished(IabResult result)
            {
                if (!result.isSuccess())
                {
                    /*Utilities.log("Unable to setup billing: " + result);
                    isBillingSetup = false;*/
                } else
                {
                    /*Utilities.log("Billing setup successfully");
                    isBillingSetup = true;*/
                    pixelmagsMagazinesList = null; // clear the list

                    AllIssuesDataSet mDbHelper = new AllIssuesDataSet(BaseApp.getContext());
                    pixelmagsMagazinesList = mDbHelper.getAllIssues(mDbHelper.getReadableDatabase());
                    mDbHelper.close();

                    skuList = new ArrayList<String>();

                    if (pixelmagsMagazinesList != null)
                    {

                        for (int i = 0; i < pixelmagsMagazinesList.size(); i++)
                        {
                            skuList.add(pixelmagsMagazinesList.get(i).android_store_sku);

                        }
                        skuList.add("com.pixelmags.androidbranded.test1");//This to confirm billing sku
                        skuList.add("com.pixelmags.androidbranded.test2");//This is to confirm billing sku
                    }
                    mHelper.queryInventoryAsync(true, skuList, mQueryFinishedListener);
                }
            }
        });
    }

    IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener()
    {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory)
        {
            if (result.isFailure())
            {
                // handle error
                return;
            }

            ArrayList<MyIssue> myIssueArray = null;
            ArrayList<MySubscription> mySubsArray = null;

            if(UserPrefs.getUserLoggedIn())
            {
                MyIssuesDataSet mDbReader = new MyIssuesDataSet(BaseApp.getContext());
                myIssueArray = mDbReader.getMyIssues(mDbReader.getReadableDatabase());
                mDbReader.close();

                //To Do
                MySubscriptionsDataSet mDbSubReader = new MySubscriptionsDataSet(BaseApp.getContext());
                mySubsArray = mDbSubReader.getMySubscriptions(mDbSubReader.getReadableDatabase());
                mDbSubReader.close();

                for(int i=0; i< mySubsArray.size();i++)
                {
                    MySubscription sub = mySubsArray.get(i);
                }

            }

            billingMagazinesList = new ArrayList<Magazine>();

            if(pixelmagsMagazinesList != null) {

                for (int i = 0; i < pixelmagsMagazinesList.size(); i++) {
                    String SKU = pixelmagsMagazinesList.get(i).android_store_sku;

                    if (inventory.hasDetails(SKU)) //yet to be changed,this is for billing test
                    {
                        SkuDetails details = inventory.getSkuDetails(SKU);

                        pixelmagsMagazinesList.get(i).price = details.getPrice();
                        Magazine finalMagazine = new Magazine();

                        finalMagazine.id = pixelmagsMagazinesList.get(i).id;
                        finalMagazine.isIssueOwnedByUser = false;

                        // check if the issue is already owned by user
                        if (myIssueArray != null) {

                            for (int issueCount = 0; issueCount < myIssueArray.size(); issueCount++) {
                                MyIssue issue = myIssueArray.get(issueCount);

                                if (issue.issueID == finalMagazine.id) {
                                    finalMagazine.isIssueOwnedByUser = true;
                                }
                            }
                        }

                        //    magazine.magazineId = unit.getInt("ID"); // Is this different from ID field ??
                        finalMagazine.synopsis = pixelmagsMagazinesList.get(i).synopsis;
                        finalMagazine.type = pixelmagsMagazinesList.get(i).type;
                        finalMagazine.title = pixelmagsMagazinesList.get(i).title;
                        finalMagazine.mediaFormat = pixelmagsMagazinesList.get(i).mediaFormat;
                        finalMagazine.manifest = pixelmagsMagazinesList.get(i).manifest;
                        // magazine.lastModified = unit.getString("lastModified"); // how to get date?
                        finalMagazine.android_store_sku = pixelmagsMagazinesList.get(i).android_store_sku;
                        finalMagazine.price = pixelmagsMagazinesList.get(i).price;
                        finalMagazine.thumbnailURL = pixelmagsMagazinesList.get(i).thumbnailURL;
                        finalMagazine.ageRestriction = pixelmagsMagazinesList.get(i).ageRestriction;
                        finalMagazine.removeFromSale = pixelmagsMagazinesList.get(i).removeFromSale;
                        finalMagazine.isPublished = pixelmagsMagazinesList.get(i).isPublished;
                        finalMagazine.exclude_from_subscription = pixelmagsMagazinesList.get(i).exclude_from_subscription;

                        billingMagazinesList.add(finalMagazine);
                    }
                }

                // Save the Magazine Objects into the SQlite DB
                AllIssuesDataSet mDbHelper = new AllIssuesDataSet(BaseApp.getContext());
                mDbHelper.insert_all_issues_data(mDbHelper.getWritableDatabase(), billingMagazinesList);
                mDbHelper.close();

            }

            // update the UI
            mHelper.queryInventoryAsync(mGotInventoryListener);
        }

    };

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener
            = new IabHelper.QueryInventoryFinishedListener()
    {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory)
        {

            if (result.isFailure())
            {
                // handle error here
            }
            else
            {
                // does the user have the premium upgrade?
                //mIsPremium = inventory.hasPurchase(SKU_PREMIUM);
                // update UI accordingly
                List<String> allOwnedSKUS = inventory.getAllOwnedSkus();
                userOwnedSKUList = new ArrayList<Purchase>();
                for ( int i = 0; i < allOwnedSKUS.size(); i++)
                {
                    Purchase purchaseData = inventory.getPurchase(allOwnedSKUS.get(i));
                    //Assign button Status here and also restore purchase if the issue is not purchased
                    userOwnedSKUList.add(purchaseData);
                }



            }
        }
    };

    @Override
    public void onNavigationDrawerItemSelected(int position)
    {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }


    // set the title based on the menu item
    public void onSectionAttached(int number) {

        switch (number) {
            case 0:
                mTitle = getString(R.string.menu_title_allissues);
                break;
            case 1:
                mTitle = Util.getLoginOrMyAccount();
            case 2:
                mTitle = getString(R.string.menu_title_subscriptions);
                break;
            case 3:
                mTitle = getString(R.string.menu_title_downloads);
                break;
            case 4:
                mTitle = getString(R.string.menu_title_contactsupport);
                break;
            case 5:
                mTitle = getString(R.string.menu_title_about);
                break;
            case 6:
                mTitle = getString(R.string.menu_issue_view);
                break;
            default:
                mTitle = getString(R.string.app_name);
                break;
        }
    }

    public void  canPurchaseLauncher(String sku, int issueId)
    {
        mCanPurchaseTask = new CanPurchaseTask(sku,issueId);
        mCanPurchaseTask.execute((String) null);
    }

    public void createPurchaseLauncher(String sku, int issueId)
    {
        mCanPurchaseTask = null;
        purchaseIssueId = issueId;
        purchaseSKU = sku;
        String userPixelMagsID = UserPrefs.getUserPixelmagsId();
        String encodeData = "{\"user_id\": "+ userPixelMagsID +"}";
        byte[] data = encodeData.getBytes();
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        mHelper.launchPurchaseFlow(this, "com.pixelmagsandroid.newtestapp4", 1001,
                mPurchaseFinishedListener,base64);
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener()
    {
        public void onIabPurchaseFinished(IabResult result,Purchase purchase)
        {
            if (result.isFailure())
            {
                // Handle error
                return;
            }
            else if (purchase.getSku().equals(purchaseSKU))
            {

                //

                //true
                //  consumeItem();
                // buyButton.setEnabled(false);
            }

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);

                    String purchase_receipt=jo.getString("purchaseToken");
                    String purchase_signature= dataSignature;

                    //
                    mCreatePurchaseTask = new CreatePurchaseTask(purchaseIssueId,purchase_receipt,purchase_signature);
                    mCreatePurchaseTask.execute((String) null);

                    //google result
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void restoreActionBar() {
        ViewGroup actionBarLayout = (ViewGroup) this.getLayoutInflater().inflate(R.layout.actionbar_layout, null);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        // replacing Color.DKGRAY with #FF0099CC
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF0099CC")));
        ActionBar.LayoutParams params = new
                ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        actionBar.setCustomView(actionBarLayout, params);
        View v = getSupportActionBar().getCustomView();
        TextView titleTxtView = (TextView) v.findViewById(R.id.textviewactivityname);
        titleTxtView.setText(Config.Magazine_Title);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.


            // Supressing the default Options Menu
            //getMenuInflater().inflate(R.menu.main, menu);


            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       /* int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/
        switch (item.getItemId()) {
            case R.id.action_settings:
// settings
                return true;
//            case R.id.action_manage_subscriptions:
// manage subscriptions action
            //               return true;
//            case R.id.action_sort:
// sort action
//            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }


    /* All added code here */

    @Override
    public void onResume(){
        super.onResume();

        resumeServiceDownloadProcessing();

    }


    @Override
    public void onDestroy() {

        Log.d(TAG, "OnDestroy method is called");
        stopDownloadService();
        super.onDestroy();

        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }

    }


    // Service :: Implementation

    public void startDownloadService() {

        startService(new Intent(getBaseContext(), PMService.class));

        mConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                // This is called when the connection with the service has been
                // established, giving us the service object we can use to
                // interact with the service.  Because we have bound to a explicit
                // service that we know is running in our own process, we can
                // cast its IBinder to a concrete class and directly access it.

                mPMService = ((PMService.LocalBinder)service).getService();

                Log.d(TAG,"M PMService value assigned is : " +mPMService);
            }

            public void onServiceDisconnected(ComponentName className) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                // Because it is running in our same process, we should never
                // see this happen.
                mPMService = null;
                Log.d(TAG,"M PMService value assigned 222 is : " +mPMService);
            }
        };

        doBindService();

    }


    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(MainActivity.this, PMService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {

        Log.d(TAG,"M Is Bound is : " +mIsBound);
        if (mIsBound) {
            Log.d(TAG,"Inside the do unbind service method");
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }


    // Service :: stop download service
    public void stopDownloadService() {


        // This will stop the service
//         stopService(new Intent(getBaseContext(), PMService.class));

        // The service should stop self after all it's downloads are complete.
        doUnbindService();
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    public void stopDownload(){
        stopService(new Intent(MainActivity.this, PMService.class));
    }



    public void resumeServiceDownloadProcessing(){

        // do a resume only after the AllIssues fragment is loaded

        Log.d(TAG," Pm Service is : "+mPMService);
        if(mPMService != null){
            mPMService.resumeDownloadsProcessing();
        }

    }


    public void notifyServiceOfNewDownload(){

        Log.d(TAG, "PM Service is : " + mPMService);

        if(mPMService != null){

            Log.d(TAG, "NOTIFYING SERVICE OF NEW DOWNLOAD");
            mPMService.newDownloadRequested();
        }

    }



    // implement function, mandatory
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    // to change the action bar dynamically
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    // refresh naviagtion drawer
    public void refreshNavigationDrawer(){
    }

    public class CanPurchaseTask extends AsyncTask<String, String, Boolean> {

        public int mIssue_id;
        public String mSKU;
        private CanPurchaseTask mCanPurchaseTask = null;
        public String result;

        public CanPurchaseTask(String SKU , int issue_id) {
            mIssue_id = issue_id;
            mSKU = SKU;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            Boolean resultToDisplay = false;

            try {
                CanPurchase apiCanPurchase = new CanPurchase();
                resultToDisplay = apiCanPurchase.init(mSKU,mIssue_id);

            } catch (Exception e) {
                    e.printStackTrace();
            }

            if(resultToDisplay == true)
            {
                System.out.println("CAN PURCHASE .......");

                //Launch google purchase
                createPurchaseLauncher(mSKU, mIssue_id);
            }

            return resultToDisplay;
        }

        protected void onPostExecute(Boolean result) {

           if(result == false){

            displayCanPurchaseFail();

           }

            mCanPurchaseTask = null;
        }
        @Override
        protected void onCancelled() {
            mCanPurchaseTask = null;
        }
    }


    private void displayCanPurchaseFail(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.can_purchase_fail_title));

        // set dialog message
        alertDialogBuilder
                .setMessage(getString(R.string.can_purchase_fail_message))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok),new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

}

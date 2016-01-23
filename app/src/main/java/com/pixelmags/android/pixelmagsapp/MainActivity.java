package com.pixelmags.android.pixelmagsapp;

import android.app.Activity;
/*<<<<<<< Updated upstream*/
import android.content.Intent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
/*>>>>>>> Stashed changes*/
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.pixelmags.android.comms.Config;
import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.pixelmagsapp.service.DownloadService;
import com.pixelmags.android.pixelmagsapp.test.ResultsFragment;
import com.pixelmags.android.pixelmagsapp.ui.AllIssuesFragment;
import com.pixelmags.android.pixelmagsapp.ui.LoginFragment;
import com.pixelmags.android.pixelmagsapp.ui.NavigationDrawerFragment;
import com.pixelmags.android.pixelmagsapp.ui.RegisterFragment;
import com.pixelmags.android.pixelmagsapp.ui.SubscriptionsFragment;
import com.pixelmags.android.storage.AllIssuesDataSet;
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
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, LoginFragment.OnFragmentInteractionListener ,
        RegisterFragment.OnFragmentInteractionListener, ResultsFragment.OnFragmentInteractionListener,
        SubscriptionsFragment.OnFragmentInteractionListener {

    public IabHelper mHelper;
    private ArrayList<Magazine> magazinesList = null;
    public ArrayList<Magazine> billingMagazinesList;

    public ArrayList<String> skuList;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */

    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

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

        //
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
                    magazinesList = null; // clear the list

                    AllIssuesDataSet mDbHelper = new AllIssuesDataSet(BaseApp.getContext());
                    magazinesList = mDbHelper.getAllIssues(mDbHelper.getReadableDatabase());
                    mDbHelper.close();
                    skuList = new ArrayList<String>();

                    if (magazinesList != null)
                    {

                        for (int i = 0; i < magazinesList.size(); i++)
                        {
                            skuList.add(magazinesList.get(i).android_store_sku);

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
            List<String> allOwnedSKUS = inventory.getAllOwnedSkus();

            billingMagazinesList = new ArrayList<Magazine>();

            for (int i = 0; i < magazinesList.size(); i++)
            {
                String SKU = magazinesList.get(i).android_store_sku;

                if(inventory.hasDetails("com.pixelmags.androidbranded.test1")) //yet to be changed,this is for billing test
                {
                    SkuDetails details = inventory.getSkuDetails("com.pixelmags.androidbranded.test1");

                   // String price = details.getPrice();
                    magazinesList.get(i).price = "939";
                    Magazine finalMagazine = new Magazine();

                    finalMagazine.id = magazinesList.get(i).id;
                    //    magazine.magazineId = unit.getInt("ID"); // Is this different from ID field ??
                    finalMagazine.synopsis = magazinesList.get(i).synopsis;
                    finalMagazine.type = magazinesList.get(i).type;
                    finalMagazine.title = magazinesList.get(i).title;
                    finalMagazine.mediaFormat = magazinesList.get(i).mediaFormat;
                    finalMagazine.manifest = magazinesList.get(i).manifest;
                    // magazine.lastModified = unit.getString("lastModified"); // how to get date?
                    finalMagazine.android_store_sku = magazinesList.get(i).android_store_sku;
                    finalMagazine.price = magazinesList.get(i).price;
                    finalMagazine.thumbnailURL = magazinesList.get(i).thumbnailURL;
                    finalMagazine.ageRestriction = magazinesList.get(i).ageRestriction;
                    finalMagazine.removeFromSale = magazinesList.get(i).removeFromSale;
                    finalMagazine.isPublished = magazinesList.get(i).isPublished;
                    finalMagazine.exclude_from_subscription = magazinesList.get(i).exclude_from_subscription;

                    billingMagazinesList.add(finalMagazine);
                }
            }

            // Save the Subscription Objects into the SQlite DB
            /*AllIssuesDataSet mDbHelper = new AllIssuesDataSet(BaseApp.getContext());
            mDbHelper.insert_all_issues_data(mDbHelper.getWritableDatabase(), billingMagazinesList);
            mDbHelper.close();*/

        
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

    public void purchaseLauncher(String sku)
    {

        mHelper.launchPurchaseFlow(this, sku, 10001,
                mPurchaseFinishedListener, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
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
            else if (purchase.getSku().equals("com.pixelmags.androidbranded.test1"))
            {
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
            String sku = jo.getString("productId");
			String orderID = jo.getString("orderId");
            String purchaseState = jo.getString("purchaseState");
            String purchaseToken = jo.getString("purchaseToken");
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
    public void onDestroy() {

        stopDownloadService();

        super.onDestroy();

        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }

    }


    // Service :: start download service
    public void startDownloadService() {
        startService(new Intent(getBaseContext(), DownloadService.class));
    }

    // Service :: stop download service
    public void stopDownloadService() {
        stopService(new Intent(getBaseContext(), DownloadService.class));
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


}

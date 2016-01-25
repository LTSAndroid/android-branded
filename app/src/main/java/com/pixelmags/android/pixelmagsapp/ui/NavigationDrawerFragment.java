package com.pixelmags.android.pixelmagsapp.ui;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.pixelmags.android.IssueView.FullScreenImages;
import com.pixelmags.android.IssueView.IssueViewActivity;
import com.pixelmags.android.IssueView.IssueViewAdapter;
import com.pixelmags.android.IssueView.NewIssueView;
import com.pixelmags.android.comms.Config;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.UserPrefs;
import com.pixelmags.android.util.Util;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private CharSequence mTitle;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);




    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });


        mDrawerListView.setAdapter(new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                new String[]{
                        getString(R.string.menu_title_allissues),
                        Util.getLoginOrMyAccount(),
                        getString(R.string.menu_title_subscriptions),
                        getString(R.string.menu_title_downloads),
                        getString(R.string.menu_title_contactsupport),
                        getString(R.string.menu_title_about),
                        getString(R.string.menu_issue_view)
                }));

        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                refreshNavigationDrawer();


                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }


        switch (position){

            case 0:
                mTitle = getString(R.string.menu_title_allissues);

                Fragment fragmentAllIsuues = new AllIssuesFragment();
                // Insert the fragment by replacing any existing fragment
                FragmentManager allIssuesFragmentManager = getFragmentManager();
                allIssuesFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, fragmentAllIsuues)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                 //       .addToBackStack(null)
                        .commit();
                break;
            case 1:

                /*Intent launch = new Intent(getActivity().getBaseContext(), LaunchActivity.class);
                startActivity(launch);*/
                if(UserPrefs.getUserLoggedIn())
               {

                    mTitle = getString(R.string.menu_title_my_account);

                    Fragment userFragment = new UserAccountFragment();
                    Bundle argsUser = new Bundle();
                    argsUser.putInt("4", position);
                    userFragment.setArguments(argsUser);

                    // fragment = (LoginFragment) getFragmentManager().findFragmentById(R.id.fragment_login);

                    // Insert the fragment by replacing any existing fragment
                    FragmentManager userFragmentManager = getFragmentManager();
                    userFragmentManager.beginTransaction()
                            .replace(R.id.main_fragment_container, userFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                //            .addToBackStack(null)
                            .commit();
              }
               else
                {
                    mTitle = getString(R.string.menu_title_login);

                    Fragment fragmentLogin = new LoginFragment();
                    FragmentManager loginFragmentManager = getFragmentManager();
                    loginFragmentManager.beginTransaction()
                            .replace(R.id.main_fragment_container, fragmentLogin)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                 //           .addToBackStack(null)
                            .commit();
                }

                break;
/*

            case 2:
                mTitle = getString(R.string.menu_title_login);
                //Intent a = new Intent(getActivity().getBaseContext(), LoginActivity.class);
                //startActivity(a);

                Fragment fragmentLogin = new LoginFragment();

                FragmentManager loginFragmentManager = getFragmentManager();
                loginFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, fragmentLogin)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
                break;

            case 3:
                mTitle = getString(R.string.menu_title_register);

                Fragment fragment = new RegisterFragment();
                Bundle args = new Bundle();
                args.putInt("4", position);
                fragment.setArguments(args);

                // fragment = (LoginFragment) getFragmentManager().findFragmentById(R.id.fragment_login);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
                break;
*/
            case 2:
                mTitle = getString(R.string.menu_title_subscriptions);
                Fragment fragmentSubscriptions = new SubscriptionsFragment();
                // Insert the fragment by replacing any existing fragment
                FragmentManager subscriptionsFragmentManager = getFragmentManager();
                subscriptionsFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, fragmentSubscriptions)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                 //       .addToBackStack(null)
                        .commit();
                break;
            case 3:
                mTitle = getString(R.string.menu_title_downloads);
                Fragment fragmentAllDownloads = new AllDownloadsFragment();
                FragmentManager downloadsFragmentManager = getFragmentManager();
                downloadsFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, fragmentAllDownloads)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                break;
            case 4:
                mTitle = getString(R.string.menu_title_contactsupport);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Contact PixelMags");
                intent.putExtra(Intent.EXTRA_TEXT, "Title: "+ Config.Magazine_Title +", Bundle ID: "+ Config.Bundle_ID +", App Version: "+Config.Version);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Config.Support_EMail});

                Intent mailer = Intent.createChooser(intent, null);
                getActivity().startActivity(mailer);


                break;
            case 5:
                mTitle = getString(R.string.menu_title_about);

                Fragment aboutFragment = new AboutFragment();
                Bundle args1 = new Bundle();
                args1.putInt("4", position);
                aboutFragment.setArguments(args1);

                // fragment = (AboutFragment) getFragmentManager().findFragmentById(R.id.fragment_about);

                // Insert the fragment by replacing any existing fragment
                FragmentManager aboutFragmentManager = getFragmentManager();
                aboutFragmentManager.beginTransaction()
                        .replace(R.id.main_fragment_container, aboutFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                 //       .addToBackStack(null)
                        .commit();
                break;

            case 6:
                mTitle = getString(R.string.menu_issue_view);

                /*Intent myIntent = new Intent(getActivity(), IssueViewActivity.class);
                startActivity(myIntent);*/
                startActivity(new Intent(getActivity(),NewIssueView.class));
                break;

            default:
                mTitle = getString(R.string.app_name);
                break;
        }


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.

        /*  // Disabling 'More' option in the action bar
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        */

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_example) {
            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);

    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }


    public void refreshNavigationDrawer(){

       if(mDrawerListView != null){

           mDrawerListView.setAdapter(new ArrayAdapter<String>(
                   getActionBar().getThemedContext(),
                   android.R.layout.simple_list_item_activated_1,
                   android.R.id.text1,
                   new String[]{
                           getString(R.string.menu_title_allissues),
                           Util.getLoginOrMyAccount(),
                           getString(R.string.menu_title_subscriptions),
                           getString(R.string.menu_title_downloads),
                           getString(R.string.menu_title_contactsupport),
                           getString(R.string.menu_title_about),
                           getString(R.string.menu_issue_view)
                   }));
       }

    }
}

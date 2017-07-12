package com.pixelmags.android.pixelmagsapp.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pixelmags.android.comms.Config;
import com.pixelmags.android.pixelmagsapp.MainActivity;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.UserPrefs;
import com.pixelmags.android.ui.LoginFragment;
import com.pixelmags.android.ui.uicomponents.MultiStateButton;

import java.util.List;

/**
 * Created by likith on 2/28/17.
 */
public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.ViewHolder> {

    private Activity activity;
    private List<String> subscriptionDesc;
    private List<String> subscriptionPrc;
    private List<String> subscriptionPaymentProvider;
    private List<String> androidStoreSku;
    private List<Integer> id;
    private String TAG = "SubscriptionAdapter";

    // Provide a suitable constructor (depends on the kind of dataset)
    public SubscriptionAdapter(List<Integer> id, List<String> subscriptionDesc, List<String> subscriptionPrc,List<String> subscriptionPaymentProvider,
                               List<String> androidStoreSku, Activity activity) {

        this.id = id;
        this.subscriptionDesc = subscriptionDesc;
        this.subscriptionPrc = subscriptionPrc;
        this.subscriptionPaymentProvider = subscriptionPaymentProvider;
        this.androidStoreSku = androidStoreSku;
        this.activity = activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SubscriptionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {

        View itemView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subscription_adapter_layout,parent,false);
        return new  ViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final SubscriptionAdapter.ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        holder.subscriptionDescription.setText(subscriptionDesc.get(position));
        holder.subscriptionDescription.setTag(position);

        holder.subscriptionPrice.setText(subscriptionPrc.get(position));
//        holder.subscriptionPrice.setText("£19.99");
        holder.subscriptionPrice.setTag(position);

        holder.subscriptionPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Launch Can Purchase Subscription, if user loggedin
                if(UserPrefs.getUserLoggedIn())
                {

                    // Need to check what should be passed when calling subscription

                    String modifiedPrice = null;
                    Log.d(TAG,"Before price is : "+subscriptionPrc.get(position));
                    for(int i=0; i<Config.currencyList.length; i++){
                        if(subscriptionPrc.get(position).contains(Config.currencyList[i])){
                            modifiedPrice = subscriptionPrc.get(position).replace(Config.currencyList[i],"");
                            modifiedPrice = modifiedPrice.replaceAll(",","");
                            modifiedPrice = modifiedPrice.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
                        }
                    }

                    Log.d(TAG,"Price is : "+modifiedPrice);

                    MainActivity myAct = (MainActivity) activity;
                    myAct.canPurchaseLauncher("sub",androidStoreSku.get(position), modifiedPrice, Config.localeValue, id.get(position));
                }
                else
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                    alertDialogBuilder.setTitle(activity.getString(R.string.purchase_initiation_fail_title));

                    // set dialog message
                    alertDialogBuilder
                            .setMessage(activity.getString(R.string.purchase_subscription_fail_message))
                            .setCancelable(false)
                            .setPositiveButton(activity.getString(R.string.ok),new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.dismiss();

                                    Fragment fragmentLogin = new LoginFragment();

                                    // Insert the fragment by replacing any existing fragment
                                    FragmentManager allIssuesFragmentManager = ((FragmentActivity)activity).getSupportFragmentManager();
                                    allIssuesFragmentManager.beginTransaction()
                                            .replace(R.id.main_fragment_container, fragmentLogin)
                                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                            .commit();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }

            }

        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return subscriptionDesc.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView subscriptionDescription;
        public MultiStateButton subscriptionPrice;
        public ViewHolder(View itemView) {
            super(itemView);

            subscriptionDescription = (TextView) itemView.findViewById(R.id.sub_desc);
            subscriptionPrice = (MultiStateButton) itemView.findViewById(R.id.sub_price);

        }
    }
}



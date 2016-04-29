package com.pixelmags.android.ui.uicomponents;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.util.BaseApp;

/**
 * Created by austincoutinho on 26/02/16.
 */
public class MultiStateButton extends Button {


    public MultiStateButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAsPurchase(String purchaseString){

        setBackgroundResource(R.drawable.multibuttonbaseshape);
        setText(purchaseString);

    }

    public void updateDownloadButtonState(int state){

    }

    public void setAsView(){

        setBackgroundResource(R.drawable.multibuttonviewshape);
        setText(BaseApp.getContext().getString(R.string.view));

    }

    public void setButtonState(Magazine mMagazine){

        if(mMagazine.isIssueOwnedByUser){

            setAsView();
        }else{

            setAsPurchase(mMagazine.price);

        }

    }


}

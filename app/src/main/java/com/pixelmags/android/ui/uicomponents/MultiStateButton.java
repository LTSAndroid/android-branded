package com.pixelmags.android.ui.uicomponents;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.pixelmags.android.datamodels.Magazine;
import com.pixelmags.android.pixelmagsapp.R;
import com.pixelmags.android.storage.AllDownloadsDataSet;
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

    public void setAsDownload(String download){

        setBackgroundResource(R.drawable.multibuttonbaseshape);
        setText(download);

    }

    public void updateDownloadButtonState(int state){

        setBackgroundResource(R.drawable.multibuttonbaseshape);


    }


    public void setAsView(){

        setBackgroundResource(R.drawable.multibuttonviewshape);
        setText(BaseApp.getContext().getString(R.string.view));

    }


    public void setButtonState(Magazine mMagazine){

        if(mMagazine.isIssueOwnedByUser && mMagazine.currentDownloadStatus == AllDownloadsDataSet.DOWNLOAD_STATUS_COMPLETED
                || mMagazine.currentDownloadStatus == AllDownloadsDataSet.DOWNLOAD_STATUS_IN_PROGRESS){
            mMagazine.status = Magazine.STATUS_VIEW;
            setAsView();
        }else if(mMagazine.isIssueOwnedByUser && mMagazine.currentDownloadStatus == AllDownloadsDataSet.DOWNLOAD_STATUS_NONE){
            mMagazine.status = Magazine.STATUS_DOWNLOAD;
            setAsDownload(mMagazine.STATUS_DOWNLOAD);
        }else if(mMagazine.status == Magazine.STATUS_DOWNLOAD){
            setAsDownload(mMagazine.STATUS_DOWNLOAD);
        }else{
            mMagazine.status = Magazine.STATUS_PRICE;
            setAsPurchase(mMagazine.price);
        }

    }

    public void setButtonState(String status){
        if(status == Magazine.STATUS_VIEW){
            setAsView();
        }
    }


}

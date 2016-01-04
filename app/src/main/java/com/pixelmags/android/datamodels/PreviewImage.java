package com.pixelmags.android.datamodels;


/*
* Created (rewritten) Austin Coutinho 4 Jan 2016
*/

import android.graphics.Bitmap;

public class PreviewImage
{
    public String previewImageURL;
    public int imageWidth;
    public int imageHeight;

    public Bitmap previewImageBitmap; // to store temporarily once downloaded

    public PreviewImage()
    {
        super();
    }

    public String issueId;


    public void setImageWidth(int value)
    {
        imageWidth = value;
    }

    public void setImageHeight(int value)
    {
        imageHeight = value;
    }


    public void setPreviewImageURL(String value)
    {
        previewImageURL = value;
    }

}


package com.pixelmags.android.datamodels;

/**
 * Created by Likith.Ts on 12/05/17.
 */
public class Bookmark {

    public int issueID;
    public int pageNumber;
    public byte[] pageImage;

    public Bookmark(int issueID, int pageNumber, byte[] pageImage){
        this.issueID = issueID;
        this.pageNumber = pageNumber;
        this.pageImage = pageImage;
    }
}

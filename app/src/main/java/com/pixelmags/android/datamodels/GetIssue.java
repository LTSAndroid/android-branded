package com.pixelmags.android.datamodels;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.Date;

/**
 * Created by Annie on 11/10/15.
 */
public class GetIssue
{
    public int issueID;
    public int pageCount;
    public String title;
    public String manifest;
    public JSONArray searchTerms;
    public JSONArray pageManifest;
    public Date issueDate;

}

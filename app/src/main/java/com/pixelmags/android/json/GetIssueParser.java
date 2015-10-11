package com.pixelmags.android.json;

import com.pixelmags.android.datamodels.GetIssue;
import com.pixelmags.android.datamodels.MyIssue;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Annie on 11/10/15.
 */
public class GetIssueParser extends JSONParser {

    public Object mData;
    public ArrayList<GetIssue> getIssueList;

    public GetIssueParser(String Data){
        super(Data);
        getIssueList = new ArrayList<GetIssue>();
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

            JSONArray arrayData = baseJSON.getJSONArray("issue");
            for(int i=0;i<arrayData.length();i++)
            {
                GetIssue getIssue = new GetIssue();
                JSONObject unit = arrayData.getJSONObject(i);

                getIssue.issueID = unit.getInt("ID");
                getIssue.pageCount = unit.getInt("pageCount");
                getIssue.title = unit.getString("title");
                getIssue.pageManifest = unit.getJSONArray("page_manifest");
                getIssue.searchTerms = unit.getJSONArray("search_terms");
                getIssue.manifest = unit.getString("manifest");

                getIssueList.add(getIssue);
            }

        }catch(Exception e){}


        return true;
    }

}




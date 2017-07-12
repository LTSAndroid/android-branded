package com.pixelmags.android.json;

import com.pixelmags.android.datamodels.MyIssue;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Annie on 09/10/15.
 */
public class GetMyIssuesParser extends JSONParser {

    public Object mData;
    public ArrayList<MyIssue> myIssuesList;

    public GetMyIssuesParser(String Data){
        super(Data);
        myIssuesList = new ArrayList<MyIssue>();
    }

    public boolean parse(){

        if(!initJSONParse())
            return false; // return false if the JSON base object cannot be parsed

        try{

            JSONArray arrayData = baseJSON.getJSONArray("OwnedByMe");
            for(int i=0;i<arrayData.length();i++)
            {
                MyIssue myIssue = new MyIssue();
                JSONObject unit = arrayData.getJSONObject(i);

                myIssue.magazineID = unit.getInt("magazine_id");
                myIssue.issueID = unit.getInt("issue_id");
                myIssue.removeFromSale = unit.getBoolean("remove_from_sale");

                myIssuesList.add(myIssue);
            }

        }catch(Exception e){
            e.printStackTrace();
        }


        return true;
    }

}




package com.pixelmags.android.json;

import com.pixelmags.android.datamodels.PageTypeImage;

import org.json.JSONObject;

/**
 * Created by austincoutinho on 11/11/15.
 *
 * This class is used to parse and return specific elements of a Page that is stored in JSON
 *
 */
public class PageTypeImageParser {


    public PageTypeImage.PageDetails getPageDetailsSmall(PageTypeImage.PageDetails pageDetails, String pageJSONData) {

        return getPageDetails("small",pageDetails, pageJSONData);

    }

    public PageTypeImage.PageDetails getPageDetailsMedium(PageTypeImage.PageDetails pageDetails, String pageJSONData) {

        return getPageDetails("medium",pageDetails, pageJSONData);

    }

    public PageTypeImage.PageDetails getPageDetailsLarge(PageTypeImage.PageDetails pageDetails, String pageJSONData) {

        return getPageDetails("large",pageDetails, pageJSONData);

    }

    private PageTypeImage.PageDetails getPageDetails(String filterString, PageTypeImage.PageDetails pageDetails, String pageJSONData) {

        try {
            JSONObject baseJSON = new JSONObject(pageJSONData);
            JSONObject unit = baseJSON.getJSONObject(filterString);

            pageDetails.mime_type = unit.getString("mime_type");
            pageDetails.url = unit.getString("url");
            pageDetails.checksum_md5 = unit.getString("checksum_md5");
            pageDetails.last_modified = unit.getString("last_modified");
            pageDetails.encryption = unit.getString("encryption");
            pageDetails.file_size = unit.getInt("file_size");
            pageDetails.width = unit.getInt("width");
            pageDetails.height = unit.getInt("height");


        } catch (Exception e) {
            e.printStackTrace();
        }

        return pageDetails;
    }


}
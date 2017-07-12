package com.pixelmags.android.datamodels;

/**
 * Created by austincoutinho on 09/10/15.
 *
 * Object to hold all data pertaining to a Subscription
 *
 */
public class Subscription {

    /*
    id 	51
    magazine_id 	2
    synopsis 	Save with a subscription
    itunes_store_sku 	ultimamedia_interior_motives.12
    price 	19.99
    payment_provider 	itunes
    parent_sku_id 	NULL
    thumbnail_url 	http://cdn.pixel-mags.com/prod/ultimamedia/interior_motives/thumbnails/51.png
    credits_included 	4
    description 	1 Year
    remove_from_sale 	TRUE
    auto_renewable 	No

    {"id":51,"magazine_id":"2","synopsis":"Save with a subscription","itunes_store_sku":"ultimamedia_interior_motives.12",
    "price":19.99,"payment_provider":"itunes","parent_sku_id":null,"thumbnail_url":"http:\/\/cdn.pixel-mags.com\/prod\/ultimamedia\/
    interior_motives\/thumbnails\/51.png","credits_included":4,"description":"1 Year","remove_from_sale":true,"auto_renewable":false}

    */

    public int id;
    public int magazine_id;
    public String synopsis;
    public String android_store_sku;
    public String price;
    public String payment_provider;
    public String parent_sku_id; // Seems like not being used
    public String thumbnail_url;
    public int credits_included;
    public String description;
    public boolean remove_from_sale;
    public boolean auto_renewable;




}

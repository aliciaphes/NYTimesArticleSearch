package com.codepath.nytimessearch.models;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Owner on 22-Oct-16.
 */

public class Query {

    String beginDate;
    String sortOrder;
    ArrayList<String> selectedCategories;


    public Query() {
        beginDate = "";
        sortOrder = "";
        selectedCategories = new ArrayList<>();
    }


    public String getBeginDate() {
        return beginDate;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public boolean categoriesExist() {
        return (selectedCategories.size() > 0);
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void addCategory(String category) {
        selectedCategories.add(category);
    }


    public String buildCategories() {
        String categoryGroup = "";
        String list = "";

        list = TextUtils.join(" ", selectedCategories);

        try {
            list = URLEncoder.encode(list, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        categoryGroup = categoryGroup.concat("(" + list + ")");
        return categoryGroup;
    }


    /**
    public String buildQuery() {
        //https://api.nytimes.com/svc/search/v2/articlesearch.json?begin_date=20160112&sort=oldest&fq=news_desk:(%22education%22%20%22health%22)&api-key=227c750bb7714fc39ef1559ef1bd8329

        boolean categoriesSelected = false;
        String categoryGroup = "";
        String list = "";
        if (selectedCategories.size() > 0) {
            categoriesSelected = true;

            list = TextUtils.join(" ", selectedCategories);

            //list = Uri.parse(list).toString();
            try {
                list = URLEncoder.encode(list, "UTF-8").replace("+", "%20");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            categoryGroup.concat(":(" + list + ")");
        }

        String url = Utilities.getSearchUrl()
                + "?"
                + "begin_date=" + getBeginDate()
                + "&sort=" + getSortOrder();
        if (categoriesSelected && categoryGroup.length() > 0) {
            url.concat("&fq=news_desk" + categoryGroup);
        }

        url.concat("&api-key=" + Utilities.getApiKey());


        //generate url from these values

        return url;
    }*/


}

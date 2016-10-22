package com.codepath.nytimessearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Alicia P on 21-Oct-16.
 */

public class Article implements Serializable{

    String articleUrl;
    String headline;
    String thumbnail;

    public Article(JSONObject jsonObject){
        try {
            articleUrl = jsonObject.getString("web_url");
            headline = jsonObject.getJSONObject("headline").getString("main");
            thumbnail = "";

            JSONArray multimedia = jsonObject.getJSONArray("media");
            if(multimedia.length() > 1){
                JSONObject multimediaJson = multimedia.getJSONObject(0);
                thumbnail = "http://www.nytimes.com" + multimediaJson.getString("url");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public static ArrayList<Article> fromJSONArray(JSONArray array){
        ArrayList<Article> results = new ArrayList<>();

        for(int i=0; i<array.length(); i++){
            try {
                results.add(new Article(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

}

package com.codepath.nytimessearch.util;

/**
 * Created by Owner on 22-Oct-16.
 */

public class Utilities {

    private static String apiKey = "f18fa33160fe414cac757019984c8b81";
    private static String searchUrl = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

    public static String getSearchUrl(){
        return searchUrl;
    }

    public static String getApiKey(){
        return apiKey;
    }


}

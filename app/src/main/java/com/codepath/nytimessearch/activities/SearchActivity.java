package com.codepath.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.adapters.ArticleArrayAdapter;
import com.codepath.nytimessearch.fragments.FilterFragment;
import com.codepath.nytimessearch.listeners.EndlessScrollListener;
import com.codepath.nytimessearch.listeners.FilterSearchDialogListener;
import com.codepath.nytimessearch.models.Article;
import com.codepath.nytimessearch.models.Query;
import com.codepath.nytimessearch.util.Utilities;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements FilterSearchDialogListener {

    //todo: move strings to strings.xml

    //todo: create 'loading' articles dialog IN UTILITIES.JAVA:
//    ProgressDialog pd = new ProgressDialog(context);
//    pd.setTitle("Loading...");
//    pd.setMessage("Please wait.");
//    pd.setCancelable(false);
//    pd.show();
//    pd.dismiss();

    //EditText etQuery;
    GridView gvResults;
    //Button btnSearch;

    ArrayList<Article> articles;
    ArticleArrayAdapter articleArrayAdapter;

    RequestParams params;
    String query = "";
    AsyncHttpClient client = new AsyncHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Find NY Times articles");
        setupViews();

    }


    public void setupViews() {
        //etQuery = (EditText) findViewById(R.id.et_query);
        gvResults = (GridView) findViewById(R.id.gv_results);
        //btnSearch = (Button) findViewById(R.id.btn_search);

        articles = new ArrayList<>();
        articleArrayAdapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(articleArrayAdapter);
        gvResults.setOnItemClickListener((parent, view, position, id) -> {
            //Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
            Intent i = new Intent(getApplicationContext(), ArticleChromeActivity.class);

            Article article = articles.get(position);
            i.putExtra("article", Parcels.wrap(article));
            startActivity(i);
        });

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadNextDataFromApi(page);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
    }


    // Append the next page of data into the adapter
    // This method sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        params = new RequestParams();
        params.put("page", offset);
        params.put("q", query);

        //Send the request including an offset value (i.e `page`) as a query parameter to retrieve appropriate paginated data
        retrieveArticles(params, false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        setupSearchLogic(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupSearchLogic(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.search_bar);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onArticleSearch(query);

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //do nothing
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //we will leave the switch statement even though there is only one option as of now
        //for the future
        switch (id) {
            case R.id.filter:
                showFilterDialog();
                return true;
//            case R.id.action_settings:
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //action performed when clicking on "search" button
    public void onArticleSearch(String tvContents) {

        if (query != tvContents) {//contents of the query have changed

            query = tvContents;//update value of the query

            params = new RequestParams();
            params.put("page", 0);
            params.put("sort", "newest");//let's retrieve the latest articles, we want to stay on top of things
            params.put("q", query);

            retrieveArticles(params, true);
        }
    }

    private void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FilterFragment filterFragment = FilterFragment.newInstance("Filter search");
        filterFragment.show(fm, "FilterSearch");
    }


    @Override
    public void onFinishChoosingOptions(Query q) {
        //https://api.nytimes.com/svc/search/v2/articlesearch.json?begin_date=20160112&sort=oldest&fq=news_desk:(%22education%22%20%22health%22)&api-key=xxxxxxxxxxxxxxxx
        //extract values from query:
        params = new RequestParams();
        params.put("begin_date", q.getBeginDate());
        params.put("sort", q.getSortOrder());
        if (q.categoriesExist()) {
            params.put("fq", "news_desk:" + q.buildCategories());
        }

        //retrieve new list of articles according to parameters selected:
        retrieveArticles(params, true);
    }


    public void retrieveArticles(RequestParams params, final boolean clearArticleList) {

        String url = Utilities.getSearchUrl();

        //add API key
        params.put("api-key", Utilities.getApiKey());

        if (Utilities.isNetworkAvailable(this)) {

            client.get(url, params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            JSONArray articleJsonResults = null;

                            try {
                                articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                                if (clearArticleList) {
                                    articles.clear();
                                }
                                articles.addAll(Article.fromJSONArray(articleJsonResults));
                                articleArrayAdapter.notifyDataSetChanged();
                                if(articles.isEmpty()){
                                    Toast.makeText(getBaseContext(), "No articles retrieved, try refining your search", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            //todo: make sure images are being retrieved
                            //Toast.makeText(getBaseContext(), "Data could not be retrieved", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        } else {
            Toast.makeText(this, "Network unavailable, please try again", Toast.LENGTH_SHORT).show();
        }
    }

}

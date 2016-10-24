package com.codepath.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.codepath.nytimessearch.FilterSearchDialogListener;
import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.adapters.ArticleArrayAdapter;
import com.codepath.nytimessearch.fragments.FilterFragment;
import com.codepath.nytimessearch.models.Article;
import com.codepath.nytimessearch.models.Query;
import com.codepath.nytimessearch.util.Utilities;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements FilterSearchDialogListener {

    EditText etQuery;
    GridView gvResults;
    Button btnSearch;

    ArrayList<Article> articles;
    ArticleArrayAdapter articleArrayAdapter;

    RequestParams params;
    AsyncHttpClient client = new AsyncHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();
    }



    public void setupViews() {
        etQuery = (EditText) findViewById(R.id.et_query);
        gvResults = (GridView) findViewById(R.id.gv_results);
        btnSearch = (Button) findViewById(R.id.btn_search);

        articles = new ArrayList<>();
        articleArrayAdapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(articleArrayAdapter);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = articles.get(position);
                i.putExtra("article", article);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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

    public void onArticleSearch(View view) {
        String query = etQuery.getText().toString();

        //AsyncHttpClient client = new AsyncHttpClient();
        //url = Utilities.getSearchUrl();

        params = new RequestParams();
        params.put("api-key", Utilities.getApiKey());
        params.put("page", 0);
        params.put("q", query);

        makeCall(params);
    }

    private void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FilterFragment filterFragment = FilterFragment.newInstance("Edit filters");
        filterFragment.show(fm, "FilterFragment");
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
        params.put("api-key", Utilities.getApiKey());

        //retrieve new list of articles according to parameters selected:
        makeCall(params);
    }


    public void makeCall(RequestParams params) {

        String url = Utilities.getSearchUrl();

        //todo: make sure url is well formed

        client.get(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        JSONArray articleJsonResults = null;

                        try {
                            articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                            articles.clear();
                            articles.addAll(Article.fromJSONArray(articleJsonResults));
                            articleArrayAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                }
        );
    }

}

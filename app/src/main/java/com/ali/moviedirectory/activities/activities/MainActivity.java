package com.ali.moviedirectory.activities.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ali.moviedirectory.R;
import com.ali.moviedirectory.activities.data.EndlessRecyclerViewScrollListener;
import com.ali.moviedirectory.activities.data.RecyclerViewAdapter;
import com.ali.moviedirectory.activities.modle.Movie;
import com.ali.moviedirectory.activities.util.Constant;
import com.ali.moviedirectory.activities.util.Pref;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NoCache;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Movie> movieList;
    private List<Movie> allMovies;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RequestQueue queue;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    private TextView tvPagesNo;
    private int totalPages;
    private Movie movie;
    private boolean isFirst = true;
    private boolean isNotSearch = true;
    private EndlessRecyclerViewScrollListener scrollListener;
    private Pref pref;
    private int totalMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycleViewID);
       // tvPagesNo = findViewById(R.id.tvPageID);
        setSupportActionBar(toolbar);
        queue = new RequestQueue(new NoCache(), new BasicNetwork(new HurlStack()));
        queue = Volley.newRequestQueue(this);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // Optionally customize the position you want to default scroll to
        linearLayoutManager.scrollToPosition(0);


        // Attach the layout manager to the recycler view
        recyclerView.setLayoutManager(linearLayoutManager);

        //Get Latest Saved Search word
        pref = new Pref(MainActivity.this);
        final String search = pref.getSearch();
        allMovies = new ArrayList<>();
        movieList = new ArrayList<>();
        movieList = getMovie(search, 1, "from-main");

        allMovies.addAll(movieList);
        Log.e("all Movie in main : ", "" + allMovies.size());
        recyclerViewAdapter = new RecyclerViewAdapter(this, allMovies);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();


        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                if (isNotSearch) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to the bottom of the list
                    Log.d("LOADmORE", "total : " + totalPages + " : ITEMS : " + totalItemsCount + " :Page : " + page);
                    //
                    if (totalPages >= page) {
                        movieList.clear();
                        String search = pref.getSearch();
                        movieList = getMovie(search, page, "from-onLoad");
                        allMovies.addAll(movieList);
                        Log.e("all Movie in Load : ", "" + allMovies.size());
                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                // Notify adapter with appropriate notify methods
                                recyclerViewAdapter.notifyItemInserted(allMovies.size() + 1);
                            }
                        });
                    }

                }
            }

        };

        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            showSearchDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showSearchDialog() {
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_view, null);
        final EditText inputTxt = view.findViewById(R.id.inputTxtID);
        Button submitBt = view.findViewById(R.id.btSearchID);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
        submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!inputTxt.getText().toString().isEmpty()) {
                    String search = inputTxt.getText().toString().trim();
                    pref.setSearch(search);
                    // 1. First, clear the array of data
                    isNotSearch = false;
                    Log.d("Click movie size : ", "" + allMovies.size());
                    recyclerViewAdapter.notifyItemRangeRemoved(0, allMovies.size());
                    movieList.clear();
                    allMovies.clear();
                    isFirst = true;
                    scrollListener.resetState();
                    getMovie(search, 1, "from-Search");

                    Log.i("Search : ", "Search Click");
                }
                alertDialog.dismiss();
            }
        });
    }

    public List<Movie> getMovie(String searchTerm, final int pageNo, String s) {
        movieList.clear();
        Log.i("page : ", Constant.LEFT_URL + searchTerm + Constant.MIDDLE_URL + pageNo + Constant.RIGHT_URL);
        Log.i("from : ", s);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                Constant.LEFT_URL + searchTerm + Constant.MIDDLE_URL + pageNo + Constant.RIGHT_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //  Log.i("REspone : ", response.toString());
                            boolean checkResponse = response.getBoolean("Response");
                            Log.i("checkResponse : ", " " + checkResponse);
                            if (checkResponse) {

                                totalPages = getPageNO(response.getInt("totalResults"));
                                Log.i("TotalPages : ", "" + totalPages);
                                JSONArray movieArray = response.getJSONArray("Search");
                                for (int i = 0; i < movieArray.length(); i++) {
                                    JSONObject movieObj = movieArray.getJSONObject(i);
                                    movie = new Movie();
                                    movie.setTitle(movieObj.getString("Title"));
                                    movie.setYear(movieObj.getString("Year"));
                                    movie.setType(movieObj.getString("Type"));
                                    movie.setPoster(movieObj.getString("Poster"));
                                    movie.setImdbID(movieObj.getString("imdbID"));
                                    movie.setPages(totalPages);
                                    //  Log.i("Movie : ", movie.getTitle());

                                    movieList.add(movie);
                                }
                                totalMovies = response.getInt("totalResults");
                             ///   tvPagesNo.setText("" + totalMovies + " movies");
                            } else {
                                Toast.makeText(MainActivity.this, "No Movies Try Again", Toast.LENGTH_SHORT).show();
                            }

                            allMovies.addAll(movieList);
                            Log.e("all Movie in get : ", "" + allMovies.size());
                            if (isFirst) {
                                // allMovies.clear();
                                recyclerViewAdapter.notifyDataSetChanged();
                                isFirst = false;
                            }
                            if (!isNotSearch) {
                                isNotSearch = true;

                            }
                        } catch (JSONException e) {
                            Log.e("Json error : ", response.toString());
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError : ", error.toString());
            }
        });
        queue.add(jsonObjectRequest);

        return movieList;
    }

    private int getPageNO(int totalResults) {
        int res = (totalResults / 10);
        if (totalResults % 10 > 0) {
            res = ((totalResults / 10) - ((totalResults % 10) / 10) + 1);
        }
        return res;
    }
}

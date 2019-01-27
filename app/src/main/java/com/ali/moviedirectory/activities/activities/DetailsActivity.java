package com.ali.moviedirectory.activities.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.ali.moviedirectory.R;
import com.ali.moviedirectory.activities.modle.Movie;
import com.ali.moviedirectory.activities.util.Constant;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailsActivity extends AppCompatActivity {

    private TextView movieTitle;
    private TextView movieYear;
    private TextView director;
    private TextView actors;
    private TextView category;
    private TextView rating;
    private TextView writers;
    private TextView plot;
    private TextView boxOffice;
    private TextView runTime;
    private ImageView movieImg;
    private Movie movie;

    private RequestQueue queue;
    private String movieID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        queue = Volley.newRequestQueue(this);
        movie = (Movie) getIntent().getSerializableExtra("movie");
        Log.d("url : ", movie.getImdbID());
        movieID = movie.getImdbID();
        setupUI();
        Log.d("url : ", Constant.MOVIE_URL + movieID);
        getMovieDetails(movieID);
    }

    private void getMovieDetails(String movieID) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                Constant.MOVIE_URL + movieID, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("Movie Response :", response.toString());
                    if (response.has("Ratings")) {
                        JSONArray rateArray = response.getJSONArray("Ratings");
                        String source = null;
                        String value = null;
                        if (rateArray.length() > 0) {
                            JSONObject rateObject = rateArray.getJSONObject(rateArray.length() - 1);
                            source = rateObject.getString("Source");
                            value = rateObject.getString("Value");
                            rating.setText(source + " : " + value);
                        } else {
                            rating.setText("Rating : N/A");
                        }
                        movieTitle.setText(response.getString("Title"));
                        movieYear.setText("Released : " + response.getString("Released"));
                        category.setText("Genre : " + response.getString("Genre"));
                        runTime.setText("Runtime : " + response.getString("Runtime"));
                        writers.setText("Writer : " + response.getString("Writer"));
                        actors.setText("Actors : " + response.getString("Actors"));
                        plot.setText("Plot : " + response.getString("Plot"));
                        director.setText("Director : " + response.getString("Director"));
                        boxOffice.setText("BoxOffice : " + response.getString("BoxOffice"));
                        Picasso.get().load(response.getString("Poster")).
                                fit().placeholder(android.R.drawable.stat_sys_download).into(movieImg);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error", error.getMessage());
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void setupUI() {
        movieTitle = findViewById(R.id.movieTitleIDDets);
        movieYear = findViewById(R.id.movieReleaseIDDets);
        director = findViewById(R.id.movieDirectIDDets);
        actors = findViewById(R.id.movieActorIDDets);
        category = findViewById(R.id.movieCatIDDets);
        rating = findViewById(R.id.movieRateIDDets);
        writers = findViewById(R.id.movieWriterIDDets);
        plot = findViewById(R.id.moviePlotIDDets);
        boxOffice = findViewById(R.id.movieBoxOfficeIDDets);
        runTime = findViewById(R.id.movieRunTimeIDDets);
        movieImg = findViewById(R.id.movieImgIDDets);
    }
}

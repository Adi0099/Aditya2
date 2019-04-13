package com.example.aditya2.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.aditya2.R;
import com.example.aditya2.model.Movie;
import com.example.aditya2.model.MoviePageResult;
import com.example.aditya2.network.GetMovieDataService;
import com.example.aditya2.network.RetrofitInstance;
import com.example.aditya2.ui.adapter.MovieAdapter;
import com.example.aditya2.ui.utils.EndlessRecyclerViewScrollListener;
import com.example.aditya2.ui.utils.MovieClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static final String API_KEY = "4b30c92e0e648f5bbc42d4beba7ca3d3";
    private static final int FIRST_PAGE = 1;
    private int totalPages;
    private int currentSortMode = 1;
    private Call<MoviePageResult> call;
    private List<Movie> movieResults;
    private MovieAdapter movieAdapter;

    @BindView(R.id.rv_movies) RecyclerView recyclerView;
    @BindView(R.id.tv_no_internet_error) ConstraintLayout mNoInternetMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if(!isNetworkAvailable()){
            recyclerView.setVisibility(View.GONE);
            mNoInternetMessage.setVisibility(View.VISIBLE);
        }

        GridLayoutManager manager = new GridLayoutManager(this, 1);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        recyclerView.setLayoutManager(manager);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if ((page + 1) <= totalPages && currentSortMode != 3) {
                    loadPage(page + 1);
                }
            }
        };

        recyclerView.addOnScrollListener(scrollListener);

        loadPage(FIRST_PAGE);
    }

    private void loadPage(final int page) {
        GetMovieDataService movieDataService = RetrofitInstance.getRetrofitInstance().create(GetMovieDataService.class);

        switch(currentSortMode){
            case 1:
                call = movieDataService.getPopularMovies(page, API_KEY);
                break;
        }

        call.enqueue(new Callback<MoviePageResult>() {
            @Override
            public void onResponse(@NonNull Call<MoviePageResult> call, @NonNull Response<MoviePageResult> response) {

                if(page == 1) {
                    assert response.body() != null;
                    movieResults = response.body().getMovieResult();
                    assert response.body() != null;
                    totalPages = response.body().getTotalPages();

                    movieAdapter = new MovieAdapter(movieResults, new MovieClickListener() {
                        @Override
                        public void onMovieClick(Movie movie) {
                            Intent intent = new Intent(MainActivity.this, MovieActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("movie", movie);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                    });
                    recyclerView.setAdapter(movieAdapter);
                } else {
                    assert response.body() != null;
                    List<Movie> movies = response.body().getMovieResult();
                    for(Movie movie : movies){
                        movieResults.add(movie);
                        movieAdapter.notifyItemInserted(movieResults.size() - 1);
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<MoviePageResult> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    public static int getMeasuredPosterHeight(int width) {
        return (int) (width * 1.5f);
    }

    public static String movieImagePathBuilder(String imagePath) {
        return "https://image.tmdb.org/t/p/" +
                "w500" +
                imagePath;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @OnClick(R.id.tv_no_internet_error_refresh)
    public void refreshActivity(){
        finish();
        startActivity(getIntent());
    }

}
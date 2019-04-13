package com.example.aditya2.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aditya2.R;
import com.example.aditya2.model.Genre;
import com.example.aditya2.model.Movie;
import com.example.aditya2.model.MovieDetails;
import com.example.aditya2.model.MoviePageResult;
import com.example.aditya2.network.GetMovieDataService;
import com.example.aditya2.network.RetrofitInstance;
import com.example.aditya2.ui.adapter.MovieAdapter;
import com.example.aditya2.ui.utils.MovieClickListener;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.aditya2.ui.activity.MainActivity.API_KEY;
import static com.example.aditya2.ui.activity.MainActivity.movieImagePathBuilder;


@SuppressWarnings("ALL")
public class MovieActivity extends AppCompatActivity {
//    @BindView(R.id.title)
//    TextView mMovieTitle;
    @BindView(R.id.movie_poster)
    ImageView mMoviePoster;
    @BindView(R.id.overview)
    TextView mMovieOverview;
    @BindView(R.id.release_date)
    TextView mMovieReleaseDate;
    @BindView(R.id.rating)
    TextView mMovieRating;
    @BindView(R.id.run_time)
    TextView duration;
    @BindView(R.id.genre)
    TextView tv_genre;
    @BindView(R.id.lang)
    TextView language;
    @BindView(R.id.budget)
    TextView budget;
    @BindView(R.id.revenue)
    TextView revenue;
    private Movie mMovie;
    String id;
    private Call<MovieDetails> call;

    String genreText;
    private List<Genre> allGenres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("test");


        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            mMovie = (Movie) bundle.getSerializable("movie");
            id = String.valueOf(mMovie.getId());

        } else{
            mMovie = (Movie) savedInstanceState.getSerializable("movie");
            id = String.valueOf(mMovie.getId());

        }
        loadPage();
    }

    private void loadPage() {
        GetMovieDataService movieDataService = RetrofitInstance.getRetrofitInstance().create(GetMovieDataService.class);

        call = movieDataService.getDetailMovie(id, API_KEY);

        call.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(@NonNull Call<MovieDetails> call, @NonNull Response<MovieDetails> response) {

                Glide.with(mMoviePoster.getContext()).load(movieImagePathBuilder(response.body().getBackdropPath())).into(mMoviePoster);
                getSupportActionBar().setTitle(response.body().getTitle());

                mMovieOverview.setText(response.body().getOverview());
                mMovieReleaseDate.setText(response.body().getReleaseDate());
                duration.setText(String.valueOf(response.body().getRuntime())+"min.");
                mMovieRating.setText(String.valueOf(response.body().getVoteAverage()));
//                StringBuilder stringBuilder = new StringBuilder();
//                for (int i = 0;i<response.body().getGenres().size();i++) {
//                    Genre genre1 = new Genre();
//                    stringBuilder.append(genre1.getName());
//
//                    Log.d("genre",".."+genre1.getName());
//
//                    Log.d("genre",".."+response.body().getGenres());
//
//                }


//                String finalString = stringBuilder.toString();
                tv_genre.setText(getGenres(response.body().getGenres()));
                language.setText(response.body().getOriginalLanguage());
                DecimalFormat df = new DecimalFormat("0.00M");

                budget.setText("Budget: $"+String.valueOf(df.format(response.body().getBudget() / 1000000))+" Million.");
                revenue.setText("Rvenue: $"+String.valueOf(df.format(response.body().getRevenue() / 1000000))+" Million.");


            }

            @Override
            public void onFailure(@NonNull Call<MovieDetails> call, @NonNull Throwable t) {
                Toast.makeText(MovieActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public  void onBackPressed(){
        Intent i  = new Intent(MovieActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i  = new Intent(MovieActivity.this,MainActivity.class);
                startActivity(i);
                finish();
                break;
        }
        return true;
    }

    private String getGenres(List<Genre> genre1) {
        List<String> movieGenres = new ArrayList<>();
            for (Genre genre : genre1) {

                    movieGenres.add(genre.getName());

            }

        return TextUtils.join(", ", movieGenres);
    }

}

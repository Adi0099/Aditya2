package com.example.aditya2.network;


import com.example.aditya2.model.MovieDetails;
import com.example.aditya2.model.MoviePageResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

@SuppressWarnings("ALL")
public interface GetMovieDataService {
    @GET("discover/movie")
    Call<MoviePageResult> getPopularMovies(@Query("page") int page, @Query("api_key") String userkey);
//    Call<MoviePageResult> getPopularMovies(@Query("sort_by") String sort,@Query("page") int page, @Query("api_key") String userkey);

    @GET("movie/{movieId}")
    Call<MovieDetails> getDetailMovie(@Path("movieId") String movieId, @Query("api_key") String apiKey);

}

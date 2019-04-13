package com.example.aditya2.ui.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.aditya2.R;
import com.example.aditya2.model.Movie;
import com.example.aditya2.ui.utils.MovieClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.aditya2.ui.activity.MainActivity.movieImagePathBuilder;


@SuppressWarnings("ALL")
public class MovieViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.iv_movie_poster)
    ImageView mMoviePoster;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.overview)
    TextView overView;
    @BindView(R.id.rating)
    TextView rating;
    @BindView(R.id.lang)
    TextView language;
    @BindView(R.id.release)
    TextView releaseDate;


    @BindView(R.id.cardView) CardView mMovieCard;

    public MovieViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(final Movie movie, final MovieClickListener movieClickListener) {
//        mMovieCard.setLayoutParams(new ViewGroup.LayoutParams(getScreenWidth(), getMeasuredPosterHeight(getScreenWidth())));

        Glide.with(mMoviePoster.getContext()).load(movieImagePathBuilder(movie.getPosterPath())).placeholder(R.drawable.placeholder).fitCenter().centerCrop().into(mMoviePoster);
        title.setText(movie.getTitle());
        overView.setText(movie.getOverview());
        rating.setText(String.valueOf(movie.getVoteAverage()));
        releaseDate.setText(movie.getReleaseDate());
        language.setText(movie.getOriginalLanguage());


        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieClickListener.onMovieClick(movie);
            }
        });
    }
}

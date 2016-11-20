package com.kmdev.flix.ui.RestClient;

import android.content.Context;

import com.kmdev.flix.models.ResponseMovieDetails;
import com.kmdev.flix.models.ResponseMovieReview;
import com.kmdev.flix.models.ResponseMovieVideo;
import com.kmdev.flix.models.ResponsePopularMovie;
import com.kmdev.flix.models.ResponseTopRated;
import com.kmdev.flix.ui.retrofilt.Rest;
import com.kmdev.flix.ui.retrofilt.RestService;
import com.kmdev.flix.ui.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ububtu on 13/7/16.
 */
public class RestClient extends BaseRestClient {
    ApiHitListener apiHitListener;
    private Rest api;
    private Object object;

    public RestClient(Context _context) {
        super(_context);
    }

    public RestClient callback(ApiHitListener apiHitListener) {
        this.apiHitListener = apiHitListener;
        return this;
    }

    private Rest getApi() {
        if (api == null) {
            api = RestService.getService();
        }

        return api;
    }

    public void getPopularMovies(int current_page) {
        if (ConnectionDetector.isConnectingToInternet(_context)) {


            Call<ResponsePopularMovie> call = getApi().popularMovies(Constants.API_KEY, "en-US", current_page);
            call.enqueue(new Callback<ResponsePopularMovie>() {
                @Override
                public void onResponse(Call<ResponsePopularMovie> call, Response<ResponsePopularMovie> response) {
                    apiHitListener.onSuccessResponse(ApiIds.ID_POPULAR_MOVIES, response.body());
                }

                @Override
                public void onFailure(Call<ResponsePopularMovie> call, Throwable t) {
                    apiHitListener.onFailResponse(ApiIds.ID_POPULAR_MOVIES, t.getMessage());
                }
            });
        } else {
            apiHitListener.networkNotAvailable();
        }
    }

    public void getTopRatedMovies(int mCurrentPage) {
        if (ConnectionDetector.isConnectingToInternet(_context)) {


            Call<ResponseTopRated> call = getApi().topRatedMovies(Constants.API_KEY, "en-US", mCurrentPage);
            call.enqueue(new Callback<ResponseTopRated>() {
                @Override
                public void onResponse(Call<ResponseTopRated> call, Response<ResponseTopRated> response) {
                    apiHitListener.onSuccessResponse(ApiIds.ID_TOP_RATED_MOVIES, response.body());
                }

                @Override
                public void onFailure(Call<ResponseTopRated> call, Throwable t) {
                    apiHitListener.onFailResponse(ApiIds.ID_TOP_RATED_MOVIES, t.getMessage());
                }
            });
        } else {
            apiHitListener.networkNotAvailable();
        }
    }

    public void getMovieDetails(String movieId) {
        if (ConnectionDetector.isConnectingToInternet(_context)) {
            Call<ResponseMovieDetails> call = getApi().movieDetails(movieId, Constants.API_KEY, "en-US", "videos/images");
            call.enqueue(new Callback<ResponseMovieDetails>() {
                @Override
                public void onResponse(Call<ResponseMovieDetails> call, Response<ResponseMovieDetails> response) {
                    apiHitListener.onSuccessResponse(ApiIds.ID_MOVIE_DETAILS, response.body());
                }

                @Override
                public void onFailure(Call<ResponseMovieDetails> call, Throwable t) {
                    apiHitListener.onFailResponse(ApiIds.ID_MOVIE_DETAILS, t.getMessage());
                }
            });
        } else {
            apiHitListener.networkNotAvailable();
        }
    }

    public void getReviews(String movieId) {
        if (ConnectionDetector.isConnectingToInternet(_context)) {
            Call<ResponseMovieReview> call = getApi().movieReviews(movieId, Constants.API_KEY);
            call.enqueue(new Callback<ResponseMovieReview>() {
                @Override
                public void onResponse(Call<ResponseMovieReview> call, Response<ResponseMovieReview> response) {
                    apiHitListener.onSuccessResponse(ApiIds.ID_MOVIE_REVIEW, response.body());
                }

                @Override
                public void onFailure(Call<ResponseMovieReview> call, Throwable t) {
                    apiHitListener.onFailResponse(ApiIds.ID_MOVIE_REVIEW, t.getMessage());
                }
            });
        } else {
            apiHitListener.networkNotAvailable();
        }
    }

    public void getVideos(String movieId) {
        if (ConnectionDetector.isConnectingToInternet(_context)) {
            Call<ResponseMovieVideo> call = getApi().movieVideos(movieId, Constants.API_KEY);
            call.enqueue(new Callback<ResponseMovieVideo>() {
                @Override
                public void onResponse(Call<ResponseMovieVideo> call, Response<ResponseMovieVideo> response) {
                    apiHitListener.onSuccessResponse(ApiIds.ID_MOVIE_VIDEO, response.body());
                }

                @Override
                public void onFailure(Call<ResponseMovieVideo> call, Throwable t) {
                    apiHitListener.onFailResponse(ApiIds.ID_MOVIE_VIDEO, t.getMessage());
                }
            });
        } else {
            apiHitListener.networkNotAvailable();
        }
    }
}
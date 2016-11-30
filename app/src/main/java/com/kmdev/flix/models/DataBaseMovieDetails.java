package com.kmdev.flix.models;

/**
 * Created by Kajal on 11/24/2016.
 */
public class DataBaseMovieDetails {
    private ResponseMovieDetails responseMovieDetails;
    private ResponseMovieReview responseMovieReview;
    private ResponseMovieVideo responseMovieVideo;
    private int movieId;

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public ResponseMovieDetails getResponseMovieDetails() {
        return responseMovieDetails;
    }

    public void setResponseMovieDetails(ResponseMovieDetails responseMovieDetails) {
        this.responseMovieDetails = responseMovieDetails;
    }

    public ResponseMovieReview getResponseMovieReview() {
        return responseMovieReview;
    }

    public void setResponseMovieReview(ResponseMovieReview responseMovieReview) {
        this.responseMovieReview = responseMovieReview;
    }

    public ResponseMovieVideo getResponseMovieVideo() {
        return responseMovieVideo;
    }

    public void setResponseMovieVideo(ResponseMovieVideo responseMovieVideo) {
        this.responseMovieVideo = responseMovieVideo;
    }
}

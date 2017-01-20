package com.kmdev.flix.models;

/**
 * Created by Kajal on 11/24/2016.
 */
public class DataBaseMovieDetails {
    private ResponseMovieDetails responseMovieDetails;
    private ResponseMovieReview responseMovieReview;
    private ResponseVideo responseMovieVideo;
    private ResponseTvDetails responseTvDetails;
    private int type;
    private int id;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ResponseTvDetails getResponseTvDetails() {
        return responseTvDetails;
    }

    public void setResponseTvDetails(ResponseTvDetails responseTvDetails) {
        this.responseTvDetails = responseTvDetails;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public ResponseVideo getResponseMovieVideo() {
        return responseMovieVideo;
    }

    public void setResponseMovieVideo(ResponseVideo responseMovieVideo) {
        this.responseMovieVideo = responseMovieVideo;
    }
}

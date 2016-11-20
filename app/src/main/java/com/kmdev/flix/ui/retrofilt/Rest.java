package com.kmdev.flix.ui.retrofilt;

import com.kmdev.flix.models.ResponseMovieDetails;
import com.kmdev.flix.models.ResponseMovieReview;
import com.kmdev.flix.models.ResponseMovieVideo;
import com.kmdev.flix.models.ResponsePopularMovie;
import com.kmdev.flix.models.ResponseTopRated;
import com.kmdev.flix.ui.utils.BaseArguments;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Vishvendra.Singh@Brsoftech on 20/6/16.
 */
public interface Rest {
    @GET("popular/")
    Call<ResponsePopularMovie> popularMovies(@Query(BaseArguments.ARG_API_KEY) String apiKey,
                                             @Query(BaseArguments.ARG_LANGUAGE) String language,
                                             @Query(BaseArguments.ARG_PAGE) int page);


    @GET("top_rated/")
    Call<ResponseTopRated> topRatedMovies(@Query(BaseArguments.ARG_API_KEY) String apiKey,
                                          @Query(BaseArguments.ARG_LANGUAGE) String s,
                                          @Query(BaseArguments.ARG_PAGE) int i);

    @GET("{movie_id}")
    Call<ResponseMovieDetails> movieDetails(@Path(BaseArguments.ARG_MOVIE_ID) String movieId,
                                            @Query(BaseArguments.ARG_API_KEY) String apiKey,
                                            @Query(BaseArguments.ARG_LANGUAGE) String language,
                                            @Query(BaseArguments.ARG_APPEND_RESPONSE) String appendToResponse);


    @GET("{movie_id}" + "/reviews")
    Call<ResponseMovieReview> movieReviews(@Path(BaseArguments.ARG_MOVIE_ID) String movieId,
                                           @Query(BaseArguments.ARG_API_KEY) String apiKey
    );

    @GET("{movie_id}" + "/videos")
    Call<ResponseMovieVideo> movieVideos(@Path(BaseArguments.ARG_MOVIE_ID) String movieId,
                                         @Query(BaseArguments.ARG_API_KEY) String apiKey);



  /*  @GET("/users/{user}/repos")
    Call<List<ResponseCategoryList>> repositories(@Path("user") String username);

    @FormUrlEncoded
    @POST("techwelt/api/web.php/")
    Call<ResponseCategoryList> allCategories(@Field("type") String type);
*/

}

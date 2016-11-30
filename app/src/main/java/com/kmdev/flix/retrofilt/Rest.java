package com.kmdev.flix.retrofilt;

import com.kmdev.flix.models.ResponseMovieDetails;
import com.kmdev.flix.models.ResponseMovieReview;
import com.kmdev.flix.models.ResponseMovieVideo;
import com.kmdev.flix.models.ResponsePopularMovie;
import com.kmdev.flix.models.ResponseSearchMovie;
import com.kmdev.flix.models.ResponseTopRated;
import com.kmdev.flix.utils.BaseArguments;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Vishvendra.Singh@Brsoftech on 20/6/16.
 */
public interface Rest {
    @GET("movie/popular/")
    Call<ResponsePopularMovie> popularMovies(@Query(BaseArguments.ARG_API_KEY) String apiKey,
                                             @Query(BaseArguments.ARG_LANGUAGE) String language,
                                             @Query(BaseArguments.ARG_PAGE) int page);


    @GET("movie/top_rated/")
    Call<ResponseTopRated> topRatedMovies(@Query(BaseArguments.ARG_API_KEY) String apiKey,
                                          @Query(BaseArguments.ARG_LANGUAGE) String s,
                                          @Query(BaseArguments.ARG_PAGE) int i);

    @GET("movie/" + "{movie_id}")
    Call<ResponseMovieDetails> movieDetails(@Path(BaseArguments.ARG_MOVIE_ID) String movieId,
                                            @Query(BaseArguments.ARG_API_KEY) String apiKey,
                                            @Query(BaseArguments.ARG_LANGUAGE) String language,
                                            @Query(BaseArguments.ARG_APPEND_RESPONSE) String appendToResponse);


    @GET("movie/" + "{movie_id}" + "/reviews")
    Call<ResponseMovieReview> movieReviews(@Path(BaseArguments.ARG_MOVIE_ID) String movieId,
                                           @Query(BaseArguments.ARG_API_KEY) String apiKey
    );

    @GET("movie/" + "{movie_id}" + "/videos")
    Call<ResponseMovieVideo> movieVideos(@Path(BaseArguments.ARG_MOVIE_ID) String movieId,
                                         @Query(BaseArguments.ARG_API_KEY) String apiKey);

    @GET("search/movie")
    Call<ResponseSearchMovie> searchMovie(@Query(BaseArguments.ARG_API_KEY) String apiKey,
                                          @Query(BaseArguments.ARG_LANGUAGE) String language,
                                          @Query(BaseArguments.ARG_QUERY) String query);



  /*  @GET("/users/{user}/repos")
    Call<List<ResponseCategoryList>> repositories(@Path("user") String username);

    @FormUrlEncoded
    @POST("techwelt/api/web.php/")
    Call<ResponseCategoryList> allCategories(@Field("type") String type);
*/

}

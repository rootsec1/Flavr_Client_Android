package io.github.abhishekwl.flavrclient.Helpers;

import io.github.abhishekwl.flavrclient.Models.Food;
import io.github.abhishekwl.flavrclient.Models.Hotel;
import io.github.abhishekwl.flavrclient.Models.User;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

  String USERS = "users";
  String HOTELS = "hotels";
  String FOOD = "food";
  String ORDERS = "orders";

  //USERS ENDPOINT
  @FormUrlEncoded
  @POST(USERS)
  Call<User> createUser(@Field("uid") String uid, @Field("name") String name, @Field("email") String email, @Field("phone") String phone, @Field("image") String image);
  @GET(USERS+"/{uid}")
  Call<User> getUser(@Path("uid") String uid);
  @FormUrlEncoded
  @PUT(USERS+"/{uid}")
  Call<User> updateUser(@Path("uid") String uid, @Field("name") String name, @Field("phone") String phone, @Field("image") String image);
  @DELETE(USERS+"/{uid}")
  Call<User> deleteUser(@Path("uid") String uid);


  //HOTELS ENDPOINT
  @GET(HOTELS+"/{uid}")
  Call<Hotel> getHotel(@Path("uid") String hotelUid);


  //FOOD ENDPOINT
  @GET(FOOD+"/{id}")
  Call<ArrayList<Food>> getFoodsFromHotel(@Path("id") String hotelId, @Query("uid") String hotelUid);

}

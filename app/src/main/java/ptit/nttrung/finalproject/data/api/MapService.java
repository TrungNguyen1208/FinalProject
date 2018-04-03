package ptit.nttrung.finalproject.data.api;


import ptit.nttrung.finalproject.model.pojo.DirectionRoot;
import ptit.nttrung.finalproject.model.pojo.GeoRoot;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by TrungNguyen on 9/11/2017.
 */

public interface MapService {

    @GET("directions/json?")
    Call<DirectionRoot> getDirectionResults(@Query("origin") String originLatLng,
                                            @Query("destination") String destinationLatLng,
                                            @Query("key") String key);

    @GET("directions/json?")
    Call<DirectionRoot> getDirectionResults(@Query("origin") String originLatLng,
                                            @Query("destination") String destinationLatLng);

    @GET("geocode/json?")
    Call<GeoRoot> getGeoLocaResults(@Query("address") String latLng,
                                    @Query("sensor") String bool,
                                    @Query("key") String key);

    @GET("geocode/json?")
    Call<GeoRoot> getLocationResults(@Query("address") String address,
                                     @Query("key") String key);


}

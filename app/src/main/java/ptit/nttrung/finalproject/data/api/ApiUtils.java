package ptit.nttrung.finalproject.data.api;

/**
 * Created by TrungNguyen on 9/11/2017.
 */

public class ApiUtils {
    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/";

    public static MapService getMapService(){
        return RetrofitClient.getClient(BASE_URL).create(MapService.class);
    }
}

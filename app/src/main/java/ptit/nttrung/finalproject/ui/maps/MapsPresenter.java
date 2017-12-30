package ptit.nttrung.finalproject.ui.maps;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.List;

import ptit.nttrung.finalproject.base.Presenter;
import ptit.nttrung.finalproject.data.api.ApiUtils;
import ptit.nttrung.finalproject.data.api.MapService;
import ptit.nttrung.finalproject.model.pojo.DirectionRoot;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by TrungNguyen on 12/28/2017.
 */

public class MapsPresenter extends Presenter<MapsView> {

    public MapsPresenter() {
    }

    @Override
    public void attachView(MapsView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void getDirectionRoot(LatLng origin, LatLng destination) {
        MapService mapService = ApiUtils.getMapService();
        String orginLatLng = String.valueOf(origin.latitude) + "," + String.valueOf(origin.longitude);
        String destinationLatLng = String.valueOf(destination.latitude) + "," + String.valueOf(destination.longitude);

//        Log.e("orginLatLng", String.valueOf(origin.latitude) + "," + String.valueOf(origin.longitude));
//        Log.e("destinationLatLng", String.valueOf(destination.latitude) + "," + String.valueOf(destination.longitude));
//        Log.e("key", key);

        Call<DirectionRoot> call = mapService.getDirectionResults(orginLatLng, destinationLatLng);
        call.enqueue(new Callback<DirectionRoot>() {
            @Override
            public void onResponse(Call<DirectionRoot> call, Response<DirectionRoot> response) {
                getView().hideProgressDialog();
                DirectionRoot directionRoot = response.body();

                if (directionRoot.getStatus().equals("OK")) {
                    String points = directionRoot.getRoutes().get(0).getOverviewPolyline().getPoints();
                    List<LatLng> latLngList = PolyUtil.decode(points);

                    getView().drawDirectionMap(latLngList);
                }
            }

            @Override
            public void onFailure(Call<DirectionRoot> call, Throwable t) {
                getView().hideProgressDialog();
                Log.e("Fail ", t.getMessage());
            }
        });
    }
}

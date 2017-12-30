package ptit.nttrung.finalproject.ui.add_restaurant;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.data.api.ApiUtils;
import ptit.nttrung.finalproject.data.api.MapService;
import ptit.nttrung.finalproject.model.pojo.GeoRoot;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by TrungNguyen on 10/23/2017.
 */

public class MapsChooseLocation extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.back_button_choose_location)
    LinearLayout backButtonChooseLocation;
    @BindView(R.id.text_view_select)
    TextView textViewSelect;
    @BindView(R.id.tv_name_location_map_choose)
    TextView textViewNameLocation;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.linear_layout_my_location)
    LinearLayout linearLayoutMyLocation;

    private Context context;
    private double selectedLat;
    private double selectedLong;
    private LatLng selectedPosition;
    private final float MAP_ZOOM = 15.0f;
    private Location myLocation = null;
    private GoogleMap mMap;

    private GoogleMap.OnMyLocationChangeListener listener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());

            if (mMap != null) {
                mMap.clear();

                mMap.addMarker(new MarkerOptions().position(loc));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            }
        }
    };

    private GoogleMap.OnMapClickListener onMapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            MapsChooseLocation.this.markLocation(latLng.latitude, latLng.longitude);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_choose_location);
        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        context = getApplicationContext();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        this.mMap.getUiSettings().setMyLocationButtonEnabled(false);
        this.mMap.getUiSettings().setZoomControlsEnabled(false);
        this.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        this.mMap.setOnMapClickListener(onMapClickListener);
        this.mMap.setOnCameraChangeListener(new onCameraChangeListener(mMap));

        markMyLocation();

        if (selectedLat == -1.0d || selectedLong == -1.0d) {
            markMyLocation();
        } else {
            markLocation(selectedLat, selectedLong);
        }
    }

    @OnClick({R.id.back_button_choose_location, R.id.text_view_select, R.id.linear_layout_my_location})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_button_choose_location:
                finish();
                break;
            case R.id.text_view_select:
                getCenterLocationMap();
                sendData();
                break;
            case R.id.linear_layout_my_location:
                mMap.clear();
                markMyLocation();
                break;
        }
    }

    private void markLocation(double lat, double lng) {
        this.selectedLat = lat;
        this.selectedLong = lng;
        this.mMap.clear();
        this.selectedPosition = new LatLng(lat, lng);
        this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedPosition, MAP_ZOOM), 100, null);
    }

    private void markMyLocation() {
        myLocation = getMyLocation();
        if (myLocation == null) {
            //defaut location Cat Linh HaNoi
            markLocation(21.0277644, 105.8341598);
        } else {
            markLocation(myLocation.getLatitude(), myLocation.getLongitude());
        }
    }

    private Location getMyLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = null;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        }
        return location;
    }

    private void sendData() {
        Intent intent = new Intent();
        intent.putExtra("lat", selectedLat);
        intent.putExtra("long", selectedLong);
        if (textViewNameLocation.getText() != null && !textViewNameLocation.getText().equals("")
                && !textViewNameLocation.getText().equals(getString(R.string.TEXT_NOT_FOUND_ADDRESS))) {
            intent.putExtra("address", textViewNameLocation.getText());
        }
        setResult(999, intent);
        finish();
    }

    private void getCenterLocationMap() {
        if (this.mMap != null) {
            VisibleRegion visibleRegion = this.mMap.getProjection().getVisibleRegion();
            LatLng center = this.mMap.getProjection().fromScreenLocation(new Point(this.mMap.getProjection().toScreenLocation(visibleRegion.farRight).x / 2,
                    this.mMap.getProjection().toScreenLocation(visibleRegion.nearLeft).y / 2));
            this.selectedLat = center.latitude;
            this.selectedLong = center.longitude;
            markLocation(this.selectedLat, this.selectedLong);
        }
    }

    class onCameraChangeListener implements GoogleMap.OnCameraChangeListener {
        private GoogleMap googleMap;

        public onCameraChangeListener(GoogleMap googleMap) {
            this.googleMap = googleMap;
        }

        @Override
        public void onCameraChange(final CameraPosition cameraPosition) {
            textViewNameLocation.setText("");
            progressBar.setVisibility(View.VISIBLE);

            MapService mapService = ApiUtils.getMapService();
            String latLng = String.valueOf(cameraPosition.target.latitude) + "," + String.valueOf(cameraPosition.target.longitude);

            Call<GeoRoot> call = mapService.getGeoLocaResults(latLng, "false", context.getString(R.string.google_map_api_key_server));
            call.enqueue(new Callback<GeoRoot>() {
                @Override
                public void onResponse(Call<GeoRoot> call, Response<GeoRoot> response) {
                    GeoRoot geoRoot = response.body();
                    if (geoRoot.getStatus().equals("OK")) {
                        progressBar.setVisibility(View.GONE);
                        String formattedAddress = geoRoot.getResults().get(0).getFormatted_address();
                        if (formattedAddress != null) {
                            textViewNameLocation.setText(formattedAddress.replaceAll("\"", "") + "");
                            selectedLat = cameraPosition.target.latitude;
                            selectedLong = cameraPosition.target.longitude;
                        } else {
                            textViewNameLocation.setText(getString(R.string.TEXT_NOT_FOUND_ADDRESS));
                        }
                    } else {
                        textViewNameLocation.setText(getString(R.string.TEXT_NOT_FOUND_ADDRESS));
                    }
                }

                @Override
                public void onFailure(Call<GeoRoot> call, Throwable t) {
                    textViewNameLocation.setText(getString(R.string.TEXT_NOT_FOUND_ADDRESS));
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}

package ptit.nttrung.finalproject.ui.restaurant_detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseActivity;
import ptit.nttrung.finalproject.model.entity.Restaurant;
import ptit.nttrung.finalproject.ui.maps.MapsActivity;

public class RestaurantDetailActivity extends BaseActivity implements OnMapReadyCallback {

    @BindView(R.id.fab_direction)
    FloatingActionButton fabDirection;

    private Restaurant restaurant;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurent_view);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        fabDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestaurantDetailActivity.this, MapsActivity.class);
                intent.putExtra("lat", restaurant.latitude);
                intent.putExtra("lng", restaurant.longitude);

                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        MarkerOptions markerOptions = new MarkerOptions();
        double lng = restaurant.longitude;
        double lat = restaurant.latitude;

        LatLng latLng = new LatLng(lat, lng);
        markerOptions.position(latLng);
        markerOptions.title(restaurant.name);
        googleMap.addMarker(markerOptions);
        googleMap.setMaxZoomPreference(13.0F);
        googleMap.getUiSettings().setAllGesturesEnabled(false);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13.0F);
        googleMap.moveCamera(cameraUpdate);
    }


}

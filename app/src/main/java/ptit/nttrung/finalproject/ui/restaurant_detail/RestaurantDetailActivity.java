package ptit.nttrung.finalproject.ui.restaurant_detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import ptit.nttrung.finalproject.util.helper.GlideUtil;

public class RestaurantDetailActivity extends BaseActivity implements OnMapReadyCallback, RestaurantView {

    @BindView(R.id.tv_title_toolbar)
    TextView tvTitleToolbar;
    @BindView(R.id.fab_direction)
    FloatingActionButton fabDirection;
    @BindView(R.id.iv_restaurent)
    ImageView ivRestaurent;
    @BindView(R.id.tv_rest_name)
    TextView tvRestName;
    @BindView(R.id.tv_rest_address)
    TextView tvRestAddress;
    @BindView(R.id.status_res)
    TextView mStatus;
    @BindView(R.id.opentime)
    TextView mOpenTime;
    @BindView(R.id.btn_call)
    LinearLayout mCall;
    @BindView(R.id.tv_cost)
    TextView tvCost;

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
        getSupportActionBar().setTitle("");

        this.restaurant = (Restaurant) getIntent().getExtras().getParcelable("restaurant");
        showRestaurant(this.restaurant);

        fabDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestaurantDetailActivity.this, MapsActivity.class);
                intent.putExtra("lat", RestaurantDetailActivity.this.restaurant.latitude);
                intent.putExtra("lng", RestaurantDetailActivity.this.restaurant.longitude);

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

    @Override
    public void showRestaurant(Restaurant restaurant) {
        tvTitleToolbar.setText(restaurant.name);
        tvRestName.setText(restaurant.name);
        tvRestAddress.setText(restaurant.address);
        GlideUtil.loadImage(restaurant.images.get(0), ivRestaurent);
        mOpenTime.setText(restaurant.openTime + " - " + restaurant.closeTime);
        tvCost.setText("Giá: " + restaurant.minCost + " - " + restaurant.maxCost);
        mStatus.setText("Đang Mở Cửa");

//        if (TimeUtils.betweenTo2Time(restaurant.openTime, restaurant.closeTime)) {
//            mStatus.setText("Đang Mở Cửa");
//        } else {
//            mStatus.setText("Đã Đóng Cửa");
//        }
        mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeToastError("Quán ăn chưa có số điện thoại!");
            }
        });

        //Map
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
}

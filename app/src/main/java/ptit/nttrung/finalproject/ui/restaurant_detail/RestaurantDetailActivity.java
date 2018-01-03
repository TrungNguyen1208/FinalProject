package ptit.nttrung.finalproject.ui.restaurant_detail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseActivity;
import ptit.nttrung.finalproject.data.firebase.FirebaseUtil;
import ptit.nttrung.finalproject.model.entity.Comment;
import ptit.nttrung.finalproject.model.entity.Restaurant;
import ptit.nttrung.finalproject.ui.comment.CommentActivity;
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
    @BindView(R.id.btn_like)
    Button btnLike;
    @BindView(R.id.btn_comment)
    Button btnComment;
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.btnShare)
    Button btnShare;
    @BindView(R.id.text_view_num_of_comment)
    TextView totalCmt;
    @BindView(R.id.totalLike)
    TextView totalLike;
    @BindView(R.id.rv_list_cmt)
    RecyclerView rvListCmt;

    private Restaurant restaurant;
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private List<Comment> comments = new ArrayList<>();
    private CommentAdapter adapter;
    private BroadcastReceiver receiver;


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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeToast("Tính năng sẽ được cập nhật sau!");
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/*");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        "Địa điểm ăn uống ngon " + restaurant.name + " địa chỉ " + restaurant.address + ".Miêu tả:" + restaurant.desciption);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RestaurantDetailActivity.this, CommentActivity.class);
                intent.putExtra("idRes", restaurant.resId);
                intent.putExtra("name", restaurant.name);
                intent.putExtra("address", restaurant.address);
                startActivity(intent);
            }
        });

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userKey = FirebaseUtil.getCurrentUserId();
                final DatabaseReference postLikesRef = FirebaseUtil.getLikesRef();
                postLikesRef.child(restaurant.resId).child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            postLikesRef.child(restaurant.resId).child(userKey).removeValue();
                        } else {
                            postLikesRef.child(restaurant.resId).child(userKey).setValue(ServerValue.TIMESTAMP);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        makeToastSucces("Có lỗi xảy ra!");
                    }
                });
            }
        });

        FirebaseUtil.getLikesRef().child(restaurant.resId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (RestaurantDetailActivity.this != null) {
                    totalLike.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvListCmt.setLayoutManager(linearLayoutManager);
        adapter = new CommentAdapter(this, comments);
        rvListCmt.setAdapter(adapter);

        FirebaseUtil.getCommentRef().child(restaurant.resId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataValue : dataSnapshot.getChildren()) {
                    Comment comment = dataValue.getValue(Comment.class);
                    comments.add(comment);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(CommentActivity.ACTION_SEND_CMT)) {
                    Comment comment = (Comment) intent.getExtras().getParcelable(CommentActivity.KEY_CMT);
                    if (comment != null) {
                        comments.add(comment);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        };

        FirebaseUtil.getCommentRef().child(restaurant.resId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (RestaurantDetailActivity.this != null) {
                    totalCmt.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        IntentFilter intentFilter = new IntentFilter(CommentActivity.ACTION_SEND_CMT);
        registerReceiver(receiver, intentFilter);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}

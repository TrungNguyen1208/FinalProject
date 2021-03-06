package ptit.nttrung.finalproject.ui.main;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.data.firebase.FirebaseUtil;
import ptit.nttrung.finalproject.model.entity.Comment;
import ptit.nttrung.finalproject.util.helper.GlideUtil;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_rate_item_place)
    TextView tvRateItemPlace;
    @BindView(R.id.tv_name_item_place)
    TextView tvNameItemPlace;
    @BindView(R.id.tv_address_item_place)
    TextView tvAddressItemPlace;
    @BindView(R.id.tv_distance_item_place)
    TextView tvDistanceItemPlace;
    @BindView(R.id.iv_item_place)
    ImageView mPhotoView;
    @BindView(R.id.contentFrame)
    LinearLayout contentFrame;
    @BindView(R.id.card_view_restau_item)
    CardView cardView;

    public final LinearLayout.LayoutParams params;
    public enum LikeStatus {LIKED, NOT_LIKED}
    private PostClickListener mListener;
    private TextSwitcher mNumLikesView;
    private final ImageView mLikeIcon;
    private ProgressBar mProgress;

    private final View mView;

    public RestaurantViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        ButterKnife.bind(this, itemView);

        mNumLikesView = (TextSwitcher) itemView.findViewById(R.id.tsLikesCounter);
        mProgress = (ProgressBar) itemView.findViewById(R.id.pbImageLoading);

        itemView.findViewById(R.id.btnComments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.showComments();
            }
        });
        mLikeIcon = (ImageView) itemView.findViewById(R.id.btnLike);
        mLikeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.toggleLike();
            }
        });

        contentFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.showDetail();
            }
        });

        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setPhoto(String url) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                GlideUtil.loadImage(url, mPhotoView, mProgress);
            } else {
                GlideUtil.loadImage(url, mPhotoView);
            }
        } catch (Exception e) {
        }
    }

    public void setNameRestaurant(String text) {
        if (text == null || text.isEmpty()) {
            tvNameItemPlace.setText("Quán Ăn");
        } else {
            tvNameItemPlace.setText(text);
        }
    }

    public void setAddresss(String text) {
        tvAddressItemPlace.setText(text);
    }

    public void setDistanceRestaurant(double lat, double lng, LatLng latLng) {
        Location locBrachRestaunt = new Location("");
        locBrachRestaunt.setLatitude(lat);
        locBrachRestaunt.setLongitude(lng);

        Location currentLoc = new Location("");
        currentLoc.setLatitude(latLng.latitude);
        currentLoc.setLongitude(latLng.longitude);

        NumberFormat formatter = new DecimalFormat("#0.0");
        double distance = currentLoc.distanceTo(locBrachRestaunt) / 1000;
        tvDistanceItemPlace.setText(formatter.format(distance) + " km");
    }

    public void setPostClickListener(PostClickListener listener) {
        mListener = listener;
    }

    public void setLikeStatus(LikeStatus status, Context context) {
        mLikeIcon.setImageDrawable(ContextCompat.getDrawable(context,
                status == LikeStatus.LIKED ? R.drawable.ic_heart_red : R.drawable.ic_heart_outline_grey));
    }

    public void setNumLikes(long numLikes) {
        String suffix = numLikes == 1 ? " like" : " likes";
        mNumLikesView.setText(numLikes + suffix);
    }

    public void setRateRestaurant(String postKey) {
        final List<Comment> list = new ArrayList<>();
        final double[] total = {0};
        final double[] point = {5};
        FirebaseUtil.getCommentRef().child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataValue : dataSnapshot.getChildren()) {
                    Comment comment = dataValue.getValue(Comment.class);
                    list.add(comment);
                }

                if (list.size() != 0) {
                    for (Comment comment : list) total[0] = total[0] + comment.survey.dtb;
                    point[0] = total[0] / list.size();
                }
                NumberFormat formatter = new DecimalFormat("#0.0");
                tvRateItemPlace.setText(formatter.format(point[0]));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void hide() {
        params.height = 0;
        //itemView.setLayoutParams(params); //This One.
        cardView.setLayoutParams(params);   //Or This one.
    }

    public interface PostClickListener {
        void showComments();

        void toggleLike();

        void showDetail();
    }
}

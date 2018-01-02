package ptit.nttrung.finalproject.ui.user_detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseActivity;
import ptit.nttrung.finalproject.data.firebase.FirebaseUtil;
import ptit.nttrung.finalproject.data.local.FriendDB;
import ptit.nttrung.finalproject.data.local.SharedPreferenceHelper;
import ptit.nttrung.finalproject.data.local.StaticConfig;
import ptit.nttrung.finalproject.model.entity.Friend;
import ptit.nttrung.finalproject.model.entity.ListFriend;
import ptit.nttrung.finalproject.model.entity.Restaurant;
import ptit.nttrung.finalproject.model.entity.User;
import ptit.nttrung.finalproject.ui.main.RestaurantViewHolder;
import ptit.nttrung.finalproject.ui.main.newfeed.NewfeedFragment;
import ptit.nttrung.finalproject.ui.restaurant_detail.RestaurantDetailActivity;
import ptit.nttrung.finalproject.util.helper.ImageUtils;

public class UserDetailActivity extends BaseActivity {

    @BindView(R.id.ivUserProfilePhoto)
    ImageView ivAvata;
    @BindView(R.id.tv_name_detail)
    TextView tvName;
    @BindView(R.id.tv_email_detail)
    TextView tvEmail;
    @BindView(R.id.btnFollow)
    Button mAddFriend;
    @BindView(R.id.user_posts_grid)
    RecyclerView mRecyclerView;
    @BindView(R.id.totalFriend)
    TextView totalFriend;
    @BindView(R.id.totalPost)
    TextView totalPost;

    private RecyclerView.Adapter<RestaurantViewHolder> mAdapter;
    private final String TAG = "UserDetailActivity";
    private static final String KEY_LAYOUT_POSITION = "layoutPosition";
    private int mRecyclerViewPosition = 0;
    private NewfeedFragment.OnPostSelectedListener mListener;
    public static final String USER_ID_EXTRA_NAME = "user_name";

    private String mUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        mUserId = intent.getStringExtra(USER_ID_EXTRA_NAME);

        mListener = new NewfeedFragment.OnPostSelectedListener() {
            @Override
            public void onPostComment(String postKey) {

            }

            @Override
            public void onPostLike(final String postKey) {
                final String userKey = FirebaseUtil.getCurrentUserId();
                final DatabaseReference postLikesRef = FirebaseUtil.getLikesRef();
                postLikesRef.child(postKey).child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            postLikesRef.child(postKey).child(userKey).removeValue();
                        } else {
                            postLikesRef.child(postKey).child(userKey).setValue(ServerValue.TIMESTAMP);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        makeToastError("Có lỗi xảy ra!");
                    }
                });
            }
        };

        showHeader();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mRecyclerViewPosition = (int) savedInstanceState
                    .getSerializable(KEY_LAYOUT_POSITION);
            mRecyclerView.scrollToPosition(mRecyclerViewPosition);
        }

        showProgressDialog("Đang tải");
        Query allPostsQuery = FirebaseUtil.getRestaurantRef().limitToLast(2);

        mAdapter = getFirebaseRecyclerAdapter(allPostsQuery);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (itemCount == 1) {
                    hideProgressDialog();
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void showHeader() {
        if (mUserId.equals(StaticConfig.UID)) {
            mAddFriend.setText("Trang cá nhân");
        } else {
            if (isFriend(mUserId)) {
                mAddFriend.setText("Bạn bè");
                mAddFriend.setClickable(false);
            } else {
                mAddFriend.setText("Kết bạn");
                mAddFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }

        FirebaseUtil.getUserRef().child(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    HashMap map = (HashMap) dataSnapshot.getValue();

                    User user = new User();
                    user.name = (String) map.get("name");
                    user.email = (String) map.get("email");
                    user.uid = (String) map.get("uid");
                    user.avata = (String) map.get("avata");

                    ImageUtils.loadAvata(UserDetailActivity.this, user.avata, ivAvata);
                    tvEmail.setText(user.email);
                    tvName.setText(user.name);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("TAG", firebaseError.getMessage());
            }
        });

        FirebaseUtil.getFriendRef().child(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (UserDetailActivity.this != null) {
                    totalFriend.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private boolean isFriend(String idFriend) {
        ListFriend dataListFriend = null;
        ArrayList<String> listFriendID = null;
        if (dataListFriend == null) {
            dataListFriend = FriendDB.getInstance(this).getListFriend();
            if (dataListFriend.getListFriend().size() > 0) {
                listFriendID = new ArrayList<>();
                for (Friend friend : dataListFriend.getListFriend()) {
                    listFriendID.add(friend.uid);
                }
            }
        }
        if (listFriendID.contains(idFriend)) return true;
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder> getFirebaseRecyclerAdapter(Query query) {
        return new FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder>(
                Restaurant.class, R.layout.item_row_place, RestaurantViewHolder.class, query) {
            @Override
            public void populateViewHolder(final RestaurantViewHolder viewHolder,
                                           final Restaurant post, final int position) {
                setupPost(viewHolder, post, position, null);
            }

            @Override
            public void onViewRecycled(RestaurantViewHolder holder) {
                super.onViewRecycled(holder);
            }
        };
    }

    private void setupPost(final RestaurantViewHolder postViewHolder, final Restaurant restaurant, final int position, final String inPostKey) {
        if (restaurant != null) {
            postViewHolder.setPhoto(restaurant.images.get(0));
            postViewHolder.setAddresss(restaurant.address);
            postViewHolder.setNameRestaurant(restaurant.name);
            postViewHolder.setDistanceRestaurant(restaurant.latitude, restaurant.longitude,
                    SharedPreferenceHelper.getInstance(UserDetailActivity.this).getCurrentLocation());

            final String postKey;
            if (mAdapter instanceof FirebaseRecyclerAdapter) {
                postKey = ((FirebaseRecyclerAdapter) mAdapter).getRef(position).getKey();
            } else {
                postKey = inPostKey;
            }
            postViewHolder.setRateRestaurant(postKey);
            ValueEventListener likeListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (UserDetailActivity.this != null) {
                        postViewHolder.setNumLikes(dataSnapshot.getChildrenCount());
                        if (dataSnapshot.hasChild(FirebaseUtil.getCurrentUserId())) {
                            postViewHolder.setLikeStatus(RestaurantViewHolder.LikeStatus.LIKED, UserDetailActivity.this);
                        } else {
                            postViewHolder.setLikeStatus(RestaurantViewHolder.LikeStatus.NOT_LIKED, UserDetailActivity.this);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            FirebaseUtil.getLikesRef().child(postKey).addValueEventListener(likeListener);

            postViewHolder.setPostClickListener(new RestaurantViewHolder.PostClickListener() {
                @Override
                public void showComments() {
                    Log.d(TAG, "Comment position: " + position);
                    mListener.onPostComment(postKey);
                }

                @Override
                public void toggleLike() {
                    Log.d(TAG, "Like position: " + position);
                    mListener.onPostLike(postKey);
                }

                @Override
                public void showDetail() {
                    Intent intent = new Intent(UserDetailActivity.this, RestaurantDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("restaurant", restaurant);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null && mAdapter instanceof FirebaseRecyclerAdapter) {
            ((FirebaseRecyclerAdapter) mAdapter).cleanup();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        int recyclerViewScrollPosition = getRecyclerViewScrollPosition();
        Log.d(TAG, "Recycler view scroll position: " + recyclerViewScrollPosition);
        savedInstanceState.putSerializable(KEY_LAYOUT_POSITION, recyclerViewScrollPosition);
        super.onSaveInstanceState(savedInstanceState);
    }

    public int getmRecyclerViewPosition() {
        return mRecyclerViewPosition;
    }

    private int getRecyclerViewScrollPosition() {
        int scrollPosition = 0;
        // TODO: Is null check necessary?
        if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        return scrollPosition;
    }
}

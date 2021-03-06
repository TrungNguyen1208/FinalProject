package ptit.nttrung.finalproject.ui.main.newfeed;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseFragment;
import ptit.nttrung.finalproject.data.firebase.FirebaseUtil;
import ptit.nttrung.finalproject.data.local.SharedPreferenceHelper;
import ptit.nttrung.finalproject.model.entity.Restaurant;
import ptit.nttrung.finalproject.ui.main.RestaurantViewHolder;
import ptit.nttrung.finalproject.ui.restaurant_detail.RestaurantDetailActivity;


public class NewfeedFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    Unbinder unbinder;

    private static final String TAG = "NewFeedFragment";
    private static final String KEY_LAYOUT_POSITION = "layoutPosition";
    private int mRecyclerViewPosition = 0;
    private OnPostSelectedListener mListener;
    private RecyclerView.Adapter<RestaurantViewHolder> mAdapter;

    public static ProgressDialog dialog;

    public static void showDialog(Context context) {
        dialog = new ProgressDialog(context);
        dialog.setMessage("Đang lấy dữ liệu...");
        dialog.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newfeed, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mRecyclerViewPosition = (int) savedInstanceState
                    .getSerializable(KEY_LAYOUT_POSITION);
            mRecyclerView.scrollToPosition(mRecyclerViewPosition);
        }

        Log.d(TAG, "Restoring recycler view position (all): " + mRecyclerViewPosition);

        showDialog(getContext());
        Query allPostsQuery = FirebaseUtil.getRestaurantRef();

        mAdapter = getFirebaseRecyclerAdapter(allPostsQuery);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (itemCount == 1) {
                    if (dialog != null) {
                        if (dialog.isShowing())
                            dialog.hide();
                    }
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                recyclerView.setTranslationY(recyclerView.getTranslationY() - dy);
//            }
//        });
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
            postViewHolder.setDistanceRestaurant(restaurant.latitude, restaurant.longitude, SharedPreferenceHelper.getInstance(getContext()).getCurrentLocation());

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
                    if (getActivity() != null) {
                        postViewHolder.setNumLikes(dataSnapshot.getChildrenCount());
                        if (dataSnapshot.hasChild(FirebaseUtil.getCurrentUserId())) {
                            postViewHolder.setLikeStatus(RestaurantViewHolder.LikeStatus.LIKED, getActivity());
                        } else {
                            postViewHolder.setLikeStatus(RestaurantViewHolder.LikeStatus.NOT_LIKED, getActivity());
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
                    Intent intent = new Intent(getActivity(), RestaurantDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("restaurant", restaurant);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }

    public interface OnPostSelectedListener {
        void onPostComment(String postKey);

        void onPostLike(String postKey);

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPostSelectedListener) {
            mListener = (OnPostSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPostSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}

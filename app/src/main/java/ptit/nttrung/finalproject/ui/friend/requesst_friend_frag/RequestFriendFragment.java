package ptit.nttrung.finalproject.ui.friend.requesst_friend_frag;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseFragment;
import ptit.nttrung.finalproject.model.entity.User;
import ptit.nttrung.finalproject.ui.friend.friend_frag.FriendsFragment;
import ptit.nttrung.finalproject.ui.user_detail.UserDetailActivity;

public class RequestFriendFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, FriendRequestView {

    @BindView(R.id.tv_header)
    TextView tvHeader;
    @BindView(R.id.rv_friend_request)
    RecyclerView rvFriendRequest;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.tv_no_friend)
    TextView tvNoFriend;

    private Unbinder unbinder;
    private List<User> friends = new ArrayList<>();
    private FriendRequestPresenter presenter;
    private FriendRequestAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_request, container, false);
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvFriendRequest.setLayoutManager(linearLayoutManager);
        adapter = new FriendRequestAdapter(getContext(), friends);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        presenter = new FriendRequestPresenter();
        presenter.attachView(this);

        adapter.setOnItemClick(new FriendRequestAdapter.OnItemClick() {
            @Override
            public void onAcceptClick(final int position) {
                presenter.onAccecptFriendClick(friends.get(position).uid);
            }

            @Override
            public void onDeniedClick(final int position) {
                presenter.onDeniedRequestClick(friends.get(position).uid);
            }

            @Override
            public void onImageClick(int position) {
                Intent userDetailIntent = new Intent(getContext(), UserDetailActivity.class);
                userDetailIntent.putExtra(UserDetailActivity.USER_ID_EXTRA_NAME, friends.get(position).uid);
                getContext().startActivity(userDetailIntent);
            }
        });
        rvFriendRequest.setAdapter(adapter);

        presenter.getAllFriendRequest();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        presenter.detachView();
    }

    @Override
    public void onRefresh() {
        friends.clear();
        adapter.notifyDataSetChanged();
        presenter.getAllFriendRequest();
    }

    @Override
    public void onAddRequestSuccess(DataSnapshot dataSnapshot) {
        if (getActivity() != null && dataSnapshot.getValue() != null) {
            HashMap map = (HashMap) dataSnapshot.getValue();

            User user = new User();
            user.name = (String) map.get("name");
            user.email = (String) map.get("email");
            user.uid = (String) map.get("uid");
            user.avata = (String) map.get("avata");

            friends.add(user);
            adapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDeniedRequestSuccess(DataSnapshot dataSnapshot) {
        if (getActivity() != null && dataSnapshot.getValue() != null) {
            HashMap map = (HashMap) dataSnapshot.getValue();
            String uid = (String) map.get("uid");

            for (User author : friends) {
                if (author.uid != null && author.uid.equals(uid)) {
                    Intent intentAdd = new Intent(FriendsFragment.ACTION_ADD_FRIEND);
                    intentAdd.putExtra("idFriend", uid);
                    intentAdd.putExtra("avata", (String) map.get("avata"));
                    intentAdd.putExtra("email", (String) map.get("email"));
                    intentAdd.putExtra("name", (String) map.get("name"));
                    getContext().sendBroadcast(intentAdd);

                    friends.remove(author);
                }
            }
            adapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}

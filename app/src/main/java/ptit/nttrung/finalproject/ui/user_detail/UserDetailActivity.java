package ptit.nttrung.finalproject.ui.user_detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseActivity;
import ptit.nttrung.finalproject.data.firebase.FirebaseUtil;
import ptit.nttrung.finalproject.data.local.FriendDB;
import ptit.nttrung.finalproject.data.local.StaticConfig;
import ptit.nttrung.finalproject.model.entity.Friend;
import ptit.nttrung.finalproject.model.entity.ListFriend;
import ptit.nttrung.finalproject.model.entity.User;
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
    RecyclerView recyclerView;

    private final String TAG = "UserDetailActivity";
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
}

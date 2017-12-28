package ptit.nttrung.finalproject.ui.friend.friend_frag;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseFragment;
import ptit.nttrung.finalproject.data.firebase.FirebaseUtil;
import ptit.nttrung.finalproject.data.firebase.ServiceUtils;
import ptit.nttrung.finalproject.data.local.FriendDB;
import ptit.nttrung.finalproject.data.local.SharedPreferenceHelper;
import ptit.nttrung.finalproject.data.local.StaticConfig;
import ptit.nttrung.finalproject.model.entity.Friend;
import ptit.nttrung.finalproject.model.entity.ListFriend;
import ptit.nttrung.finalproject.model.entity.User;

/**
 * Created by TrungNguyen on 12/17/2017.
 */


public class FriendsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, FriendsView {

    public static final String ACTION_DELETE_FRIEND = "com.android.ptit.DELETE_FRIEND";
    public static final String ACTION_ADD_FRIEND = "com.android.ptit.ADD_FRIEND";

    private RecyclerView recyclerListFrends;
    private ListFriendsAdapter adapter;
    public FragFriendClickFloatButton onClickFloatButton;
    private ListFriend dataListFriend = new ListFriend();
    private ArrayList<String> listFriendID = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CountDownTimer detectFriendOnline;
    public static int ACTION_START_CHAT = 1;
    private FriendsPresenter presenter;

    private TextView tvNoFriend;

    private BroadcastReceiver deleteFriendReceiver, addFriendRecevier;

    public FriendsFragment() {
        onClickFloatButton = new FragFriendClickFloatButton();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        detectFriendOnline = new CountDownTimer(System.currentTimeMillis(), StaticConfig.TIME_TO_REFRESH) {
            @Override
            public void onTick(long l) {
                ServiceUtils.updateFriendStatus(getContext(), dataListFriend);
                ServiceUtils.updateUserStatus(getContext());
            }

            @Override
            public void onFinish() {
            }
        };
        if (dataListFriend == null) {
            dataListFriend = FriendDB.getInstance(getContext()).getListFriend();
            if (dataListFriend.getListFriend().size() > 0) {
                listFriendID = new ArrayList<>();
                for (Friend friend : dataListFriend.getListFriend()) {
                    listFriendID.add(friend.id);
                }
                detectFriendOnline.start();
            }
        }

        View layout = inflater.inflate(R.layout.fragment_friends, container, false);

        tvNoFriend = (TextView) layout.findViewById(R.id.tv_no_friend);
        tvNoFriend.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerListFrends = (RecyclerView) layout.findViewById(R.id.recycleListFriend);
        recyclerListFrends.setLayoutManager(linearLayoutManager);
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        adapter = new ListFriendsAdapter(getContext(), dataListFriend, this);
        recyclerListFrends.setAdapter(adapter);

        initRecevier();

        presenter = new FriendsPresenter(getContext());
        presenter.attachView(this);
        presenter.getListFriendUId();


        return layout;
    }

    @Override
    public void onRefresh() {
        listFriendID.clear();
        if (dataListFriend != null) dataListFriend.getListFriend().clear();
        adapter.notifyDataSetChanged();
        FriendDB.getInstance(getContext()).dropDB();
        detectFriendOnline.cancel();
        presenter.getListFriendUId();

    }

    @Override
    public void showListFriendUId(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() != null) {
            HashMap mapRecord = (HashMap) dataSnapshot.getValue();
            Iterator listKey = mapRecord.keySet().iterator();
            while (listKey.hasNext()) {
                String key = listKey.next().toString();
                listFriendID.add(mapRecord.get(key).toString());
            }
            tvNoFriend.setVisibility(View.GONE);
            getAllFriendInfo(0);
        } else {
            hideProgressDialog();
            tvNoFriend.setVisibility(View.VISIBLE);
        }
    }

    public class FragFriendClickFloatButton implements View.OnClickListener {

        Context context;
        LovelyProgressDialog dialogWait;


        public FragFriendClickFloatButton() {
        }

        public FragFriendClickFloatButton getInstance(Context context) {
            this.context = context;
            dialogWait = new LovelyProgressDialog(context);
            return this;
        }

        @Override
        public void onClick(View view) {
            new LovelyTextInputDialog(view.getContext(), R.style.EditTextTintTheme)
                    .setTopColorRes(R.color.colorAccent)
                    .setTitle("Kết bạn")
                    .setMessage("Nhập email")
                    .setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                    .setInputFilter("Không tìm thấy email", new LovelyTextInputDialog.TextFilter() {
                        @Override
                        public boolean check(String text) {
                            Pattern VALID_EMAIL_ADDRESS_REGEX =
                                    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
                            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(text);
                            return matcher.find();
                        }
                    })
                    .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                        @Override
                        public void onTextInputConfirmed(String text) {
                            //Tim id user id
                            //TODO
                            findIDEmail(text);
                            //Check xem da ton tai ban ghi friend chua
                            //Ghi them 1 ban ghi
                        }
                    })
                    .show();
        }

        private void findIDEmail(String email) {
            dialogWait.setCancelable(false)
                    .setIcon(R.drawable.ic_add_friend)
                    .setTitle("Đang tìm....")
                    .setTopColorRes(R.color.colorAccent)
                    .show();
            FirebaseUtil.getUserRef()
                    .orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dialogWait.dismiss();
                    if (dataSnapshot.getValue() == null) {
                        //email not found
                        new LovelyInfoDialog(context)
                                .setTopColorRes(R.color.colorAccent)
                                .setIcon(R.drawable.ic_add_friend)
                                .setTitle("Lỗi")
                                .setMessage("Không tìm thấy email")
                                .show();
                    } else {

                        String id = ((HashMap) dataSnapshot.getValue()).keySet().iterator().next().toString();
                        Log.e("id", id);
                        if (id.equals(StaticConfig.UID)) {
                            new LovelyInfoDialog(context)
                                    .setTopColorRes(R.color.colorAccent)
                                    .setIcon(R.drawable.ic_add_friend)
                                    .setTitle("Lỗi")
                                    .setMessage("Email không hợp lệ!")
                                    .show();
                        } else {
                            HashMap userMap = (HashMap) ((HashMap) dataSnapshot.getValue()).get(id);
                            Friend user = new Friend();
                            user.name = (String) userMap.get("name");
                            user.email = (String) userMap.get("email");
                            user.avata = (String) userMap.get("avata");
                            user.uid = id;
                            String idRoom = id.compareTo(StaticConfig.UID) > 0 ? (StaticConfig.UID + id).hashCode() + "" : "" + (id + StaticConfig.UID).hashCode();
                            user.idRoom = idRoom;
                            checkBeforAddFriend(id, user);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        /**
         * Kiểm tra xem đã là bạn bè chưa
         */
        private void checkBeforAddFriend(final String idFriend, Friend userInfo) {
            dialogWait.setCancelable(false)
                    .setIcon(R.drawable.ic_add_friend)
                    .setTitle("Kết bạn....")
                    .setTopColorRes(R.color.colorPrimary)
                    .show();

            //Check xem da ton tai id trong danh sach id chua
            if (listFriendID.contains(idFriend)) {
                dialogWait.dismiss();
                new LovelyInfoDialog(context)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_add_friend)
                        .setTitle("Bạn bè")
                        .setMessage("User " + userInfo.email + " đã là bạn bè")
                        .show();
            } else {
                //Gửi yêu cầu kết bạn
                sendRequestFriend(idFriend);
            }
        }

        private void sendRequestFriend(final String idFriend) {
            FirebaseUtil.getRequestFriendRef().child(idFriend)
                    .child(StaticConfig.UID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dialogWait.dismiss();
                        new LovelyInfoDialog(context)
                                .setTopColorRes(R.color.colorAccent)
                                .setIcon(R.drawable.ic_add_friend)
                                .setTitle("Lỗi")
                                .setMessage("Bạn đã gửi yêu cầu kết bạn rồi!")
                                .show();
                    } else {
                        User user1 = SharedPreferenceHelper.getInstance(context).getUserInfo();

                        User user2 = new User(user1.uid, user1.name, user1.email, user1.avata);
                        Log.e("user add", "uid " + user1.uid);

                        FirebaseUtil.getRequestFriendRef()
                                .child(idFriend)
                                .child(StaticConfig.UID)
                                .setValue(user2)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialogWait.dismiss();
                                        makeToastSucces("Gửi yêu cầu kết bạn thành công");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialogWait.dismiss();
                                        makeToastError("Có lỗi xảy ra!");
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    /**
     * Truy cap bang user lay thong tin id nguoi dung
     */
    private void getAllFriendInfo(final int index) {
        if (index == listFriendID.size()) {
            //save list friend
            adapter.notifyDataSetChanged();
            hideProgressDialog();
            mSwipeRefreshLayout.setRefreshing(false);
            detectFriendOnline.start();
        } else {
            final String id = listFriendID.get(index);
            FirebaseDatabase.getInstance().getReference().child("user/" + id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Friend user = new Friend();
                        HashMap mapUserInfo = (HashMap) dataSnapshot.getValue();

                        user.name = (String) mapUserInfo.get("name");
                        user.email = (String) mapUserInfo.get("email");
                        user.avata = (String) mapUserInfo.get("avata");
                        user.uid = id;
                        String idRoom = id.compareTo(StaticConfig.UID) > 0 ? (StaticConfig.UID + id).hashCode() + "" : "" + (id + StaticConfig.UID).hashCode();
                        user.idRoom = idRoom;
                        dataListFriend.getListFriend().add(user);
                        FriendDB.getInstance(getContext()).addFriend(user);
                    }
                    getAllFriendInfo(index + 1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    hideProgressDialog();
                }
            });
        }
    }

    private void addFriend(final String idFriend, boolean isIdFriend) {
        if (idFriend != null) {
            if (isIdFriend) {
                FirebaseDatabase.getInstance().getReference().child("friend/" + StaticConfig.UID)
                        .push()
                        .setValue(idFriend)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    addFriend(idFriend, false);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeToastError("Có lỗi! Không thể kết bạn!");
                            }
                        });
            } else {
                FirebaseDatabase.getInstance().getReference().child("friend/" + idFriend)
                        .push()
                        .setValue(StaticConfig.UID)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    addFriend(null, false);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                makeToastError("Có lỗi! Không thể kết bạn!");
                            }
                        });
            }
        } else {
            new LovelyInfoDialog(getContext())
                    .setTopColorRes(R.color.colorAccent)
                    .setIcon(R.drawable.ic_add_friend)
                    .setTitle("Success")
                    .setMessage("Add friend success")
                    .show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getContext().unregisterReceiver(deleteFriendReceiver);
        getContext().unregisterReceiver(addFriendRecevier);
        presenter.detachView();
    }

    private void initRecevier() {
        addFriendRecevier = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String idFriend = intent.getExtras().getString("idFriend");
                String name = intent.getExtras().getString("name");
                String avata = intent.getExtras().getString("avata");
                String email = intent.getExtras().getString("email");

                if (listFriendID.contains(idFriend)) {
                    new LovelyInfoDialog(context)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_add_friend)
                            .setTitle("Bạn bè")
                            .setMessage("User " + email + " đã là bạn bè")
                            .show();
                } else {
                    tvNoFriend.setVisibility(View.GONE);
                    Friend user = new Friend();
                    user.name = name;
                    user.email = email;
                    user.avata = avata;
                    user.id = idFriend;
                    user.uid = idFriend;

                    String idRoom = idFriend.compareTo(StaticConfig.UID) > 0 ? (StaticConfig.UID + idFriend).hashCode() + "" : "" + (idFriend + StaticConfig.UID).hashCode();
                    user.idRoom = idRoom;

                    addFriend(idFriend, true);
                    listFriendID.add(idFriend);
                    dataListFriend.getListFriend().add(user);
                    FriendDB.getInstance(getContext()).addFriend(user);
                    adapter.notifyDataSetChanged();
                }
            }
        };

        deleteFriendReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String idDeleted = intent.getExtras().getString("idFriend");
                for (Friend friend : dataListFriend.getListFriend()) {
                    if (idDeleted.equals(friend.id)) {
                        ArrayList<Friend> friends = dataListFriend.getListFriend();
                        FriendDB.getInstance(getContext()).deleteFriend(friend);
                        friends.remove(friend);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        };

        IntentFilter intentFilter = new IntentFilter(ACTION_DELETE_FRIEND);
        getContext().registerReceiver(deleteFriendReceiver, intentFilter);

        IntentFilter intentFilterAddFr = new IntentFilter(ACTION_ADD_FRIEND);
        getContext().registerReceiver(addFriendRecevier, intentFilterAddFr);
    }
}

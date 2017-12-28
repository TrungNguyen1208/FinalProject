package ptit.nttrung.finalproject.ui.friend.friend_frag;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ptit.nttrung.finalproject.base.Presenter;
import ptit.nttrung.finalproject.data.local.StaticConfig;

/**
 * Created by TrungNguyen on 12/28/2017.
 */

public class FriendsPresenter extends Presenter<FriendsView> {
    private Context context;

    public FriendsPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void attachView(FriendsView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    /**
     * Lay danh sach ban be tren server
     */
    public void getListFriendUId() {
        getView().showProgressDialog("Lấy danh sách bạn bè");
        FirebaseDatabase.getInstance().getReference()
                .child("friend/" + StaticConfig.UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getView().showListFriendUId(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getView().hideProgressDialog();
            }
        });
    }

    public void getAllFriendInfo() {

    }
}

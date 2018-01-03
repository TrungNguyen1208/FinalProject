package ptit.nttrung.finalproject.ui.main;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import ptit.nttrung.finalproject.base.Presenter;
import ptit.nttrung.finalproject.data.firebase.FirebaseUtil;
import ptit.nttrung.finalproject.model.entity.Restaurant;

public class MainPresenter extends Presenter<MainView> {

    public MainPresenter() {
    }

    @Override
    public void attachView(MainView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void updateLike(final String restaurantId) {
        final String userKey = FirebaseUtil.getCurrentUserId();
        final DatabaseReference postLikesRef = FirebaseUtil.getLikesRef();
        postLikesRef.child(restaurantId).child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User already liked this post, so we toggle like off.
                    postLikesRef.child(restaurantId).child(userKey).removeValue();
                } else {
                    postLikesRef.child(restaurantId).child(userKey).setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                getView().makeToastSucces("Có lỗi xảy ra!");
            }
        });
    }

    public Restaurant[] getRestaurant(String restaurantId) {
        final Restaurant[] restaurant = {null};
        final String userKey = FirebaseUtil.getCurrentUserId();
        final DatabaseReference postLikesRef = FirebaseUtil.getLikesRef();
        FirebaseUtil.getRestaurantRef().child(restaurantId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                restaurant[0] = dataSnapshot.getValue(Restaurant.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return restaurant;
    }
}

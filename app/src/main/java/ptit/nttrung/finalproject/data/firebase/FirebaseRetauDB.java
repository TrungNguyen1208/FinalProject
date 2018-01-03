package ptit.nttrung.finalproject.data.firebase;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

import ptit.nttrung.finalproject.model.entity.Restaurant;

public class FirebaseRetauDB {

    private DatabaseReference databaseReference;

    public FirebaseRetauDB() {
        databaseReference = FirebaseUtil.getRestaurantRef();
    }

    public void addRestaurant(final Restaurant restaurant, List<Uri> uriList) {
        Long timestamp = System.currentTimeMillis();
        FirebaseStorage storageRef = FirebaseStorage.getInstance();
        final String newKey = databaseReference.push().getKey();
        restaurant.resId = newKey;
        Uri uri = uriList.get(0);
        StorageReference riversRef = storageRef.getReference().child(uri.getLastPathSegment() + timestamp.toString());
        riversRef.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                databaseReference.child(newKey).setValue(restaurant);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

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

    public void updateRestaurant(Restaurant restaurant) {
    }
}

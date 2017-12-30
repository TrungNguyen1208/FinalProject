package ptit.nttrung.finalproject.ui.add_restaurant;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

import ptit.nttrung.finalproject.base.Presenter;
import ptit.nttrung.finalproject.data.firebase.FirebaseUtil;
import ptit.nttrung.finalproject.model.entity.Restaurant;

public class AddPresenter extends Presenter<AddView> {

    @Override
    public void attachView(AddView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void uploadRestaurant(final Restaurant restaurant, List<Uri> uriList) {
        Long timestamp = System.currentTimeMillis();
        FirebaseStorage storageRef = FirebaseStorage.getInstance();

        for (Uri uri : uriList) {
            StorageReference riversRef = storageRef.getReference().child(uri.getLastPathSegment() + timestamp.toString());
            riversRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri fullSizeUrl = taskSnapshot.getDownloadUrl();
                    restaurant.images.add(fullSizeUrl.toString());

//                    restaurant.images = images;
                    DatabaseReference restauRef = FirebaseUtil.getRestaurantRef();
                    final String newPostKey = restauRef.push().getKey();
                    restaurant.resId = (newPostKey);

                    restauRef.child(newPostKey).setValue(restaurant).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            getView().makeToastSucces("Đăng quán ăn thành công");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            getView().makeToastError("Có lỗi xảy ra");
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    getView().makeToastError("Có lỗi xảy ra");
                }
            });
        }
    }
}

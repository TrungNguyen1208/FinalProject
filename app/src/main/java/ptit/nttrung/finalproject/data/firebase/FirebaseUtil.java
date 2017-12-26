package ptit.nttrung.finalproject.data.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtil {

    public static DatabaseReference getBaseRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

    public static DatabaseReference getCurrentUserRef() {
        String uid = getCurrentUserId();
        if (uid != null) {
            return getBaseRef().child("people").child(getCurrentUserId());
        }
        return null;
    }

    public static DatabaseReference getFriendRef() {
        return getBaseRef().child("friend");
    }

    public static DatabaseReference getUserRef() {
        return getBaseRef().child("user");
    }

    public static DatabaseReference getGroupRef() {
        return getBaseRef().child("group");
    }

    public static DatabaseReference getMessageRef() {
        return getBaseRef().child("message");
    }

    public static DatabaseReference getRequestFriendRef() {
        return getBaseRef().child("resquest_friend");
    }

    public static DatabaseReference getRestaurentRef() {
        return getBaseRef().child("restaurant");
    }

    public static DatabaseReference getCommentRef() {
        return getBaseRef().child("comment");
    }

    public static DatabaseReference getAddressRef() {
        return getBaseRef().child("address");
    }
//
//    public static DatabaseReference getLikesRef() {
//        return getBaseRef().child("likes");
//    }
//

}

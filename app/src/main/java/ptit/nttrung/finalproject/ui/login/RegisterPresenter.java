package ptit.nttrung.finalproject.ui.login;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.Presenter;
import ptit.nttrung.finalproject.data.local.StaticConfig;
import ptit.nttrung.finalproject.model.entity.User;

/**
 * Created by TrungNguyen on 12/16/2017.
 */

public class RegisterPresenter extends Presenter<RegisterView> {

    private String TAG = "Register";

    private Context context;
    private FirebaseAuth mAuth;

    public RegisterPresenter(Context context) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void attachView(RegisterView view) {
        super.attachView(view);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void onCreateAccountClick() {
        if (!getView().checkValidate()) {
            getView().makeToastError("Lỗi");
            return;
        }

        getView().showProgressDialog("Tạo tài khoản...");
        String email = getView().getEmail();
        String password = getView().getPassword();
        Log.e("email + pass ", email + " " + password);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        getView().hideProgressDialog();
                        if (!task.isSuccessful()) {
                            new LovelyInfoDialog(context) {
                                @Override
                                public LovelyInfoDialog setConfirmButtonText(String text) {
                                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dismiss();
                                        }
                                    });
                                    return super.setConfirmButtonText(text);
                                }
                            }
                                    .setTopColorRes(R.color.colorAccent)
                                    .setTitle("Đăng ký không thành công")
                                    .setMessage("Đã tồn tại email hoặc mật khẩu quá ngắn!")
                                    .setConfirmButtonText("Đồng ý")
                                    .setCancelable(false)
                                    .show();
                        } else {
                            //Đăng ký thành công
                            FirebaseUser user = mAuth.getCurrentUser();
                            initNewUserInfo(user);
                            mAuth.signOut();
                            getView().makeToastSucces("Đăng ký thành công!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.getMessage());
                        getView().hideProgressDialog();
                    }
                });
    }

    /**
     * Khoi tao thong tin mac dinh cho tai khoan moi
     */
    private void initNewUserInfo(FirebaseUser user) {
        User newUser = new User();
        newUser.email = user.getEmail();
        newUser.name = user.getEmail().substring(0, user.getEmail().indexOf("@"));
        newUser.avata = StaticConfig.STR_DEFAULT_BASE64;
        FirebaseDatabase.getInstance().getReference().child("user/" + user.getUid()).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,e.getMessage());
            }
        });
    }
}

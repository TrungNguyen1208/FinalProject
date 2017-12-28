package ptit.nttrung.finalproject.ui.change_profile;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseFragment;
import ptit.nttrung.finalproject.data.firebase.FirebaseUtil;
import ptit.nttrung.finalproject.data.local.SharedPreferenceHelper;
import ptit.nttrung.finalproject.data.local.StaticConfig;
import ptit.nttrung.finalproject.model.entity.Configuration;
import ptit.nttrung.finalproject.model.entity.User;
import ptit.nttrung.finalproject.util.helper.ActivityUtils;
import ptit.nttrung.finalproject.util.helper.ImageUtils;
import ptit.nttrung.finalproject.util.widget.RecyclerItemClickListener;

public class ProfileFragment extends BaseFragment implements ProfileView {
    private static final String TAG = ProfileFragment.class.getName();

    private static final String USERNAME_LABEL = "Username";
    private static final String EMAIL_LABEL = "Email";
    private static final String SIGNOUT_LABEL = "Sign out";
    private static final String RESETPASS_LABEL = "Change Password";

    private static final int PICK_IMAGE = 1994;

    @BindView(R.id.img_avatar)
    ImageView imgAvatar;
    @BindView(R.id.tv_username)
    TextView tvUserName;
    @BindView(R.id.info_recycler_view)
    RecyclerView mRecyclerView;
    Unbinder unbinder;

    private UserInfoAdapter infoAdapter;
    private List<Configuration> configList = new ArrayList<>();
    private User myAccount;
    private SharedPreferenceHelper preferenceHelper;
    private ProfilePresenter presenter;

    private DatabaseReference userDB = FirebaseUtil.getUserRef().child(StaticConfig.UID);

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, v);

        preferenceHelper = SharedPreferenceHelper.getInstance(getContext());
        myAccount = preferenceHelper.getUserInfo();

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Ảnh đại diện")
                        .setMessage("Bạn có muốn thay đổi ảnh đại diện?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_PICK);
                                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE);
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (presenter == null) {
            presenter = new ProfilePresenter(getContext(),myAccount);
        }
        configList = presenter.setupArrayListInfo(myAccount);
        setAdapterData(configList);
        tvUserName.setText(myAccount.name);
        setImageAvatar(getContext(), myAccount.avata);

        presenter.attachView(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                makeToastError("Có lỗi xảy ra, vui lòng thử lại");
                return;
            }
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());

                Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream);
                imgBitmap = ImageUtils.cropToSquare(imgBitmap);
                InputStream is = ImageUtils.convertBitmapToInputStream(imgBitmap);
                final Bitmap liteImage = ImageUtils.makeImageLite(is,
                        imgBitmap.getWidth(), imgBitmap.getHeight(),
                        ImageUtils.AVATAR_WIDTH, ImageUtils.AVATAR_HEIGHT);

                String imageBase64 = ImageUtils.encodeBase64(liteImage);
                myAccount.avata = imageBase64;

                showProgressDialog("Đang xử lý");

                userDB.child("avata").setValue(imageBase64)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    hideProgressDialog();
                                    preferenceHelper.saveUserInfo(myAccount);
                                    imgAvatar.setImageDrawable(ImageUtils.roundedImage(getContext(), liteImage));

                                    new LovelyInfoDialog(getContext())
                                            .setTopColorRes(R.color.colorAccent)
                                            .setTitle("Success")
                                            .setMessage("Update avatar successfully!")
                                            .show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                hideProgressDialog();
                                new LovelyInfoDialog(getContext())
                                        .setTopColorRes(R.color.colorPrimary)
                                        .setTitle("False")
                                        .setMessage("False to update avatar")
                                        .show();
                            }
                        });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setAdapterData(List<Configuration> configurations) {
        infoAdapter = new UserInfoAdapter(getActivity(), configurations);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivityContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
                        String title = tvTitle.getText().toString();
                        if (title.equals(SIGNOUT_LABEL)) {
                            ActivityUtils.signOutConfirmation(getContext());
                        } else if (title.equals(USERNAME_LABEL)) {
                            presenter.onUserNameLabelClick();
                        } else if (title.equals(RESETPASS_LABEL)) {
                            presenter.onResetPassLabelClick();
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(infoAdapter);
    }

    @Override
    public Activity getActivityContext() {
        return getActivity();
    }

    @Override
    public void setTextName(String name) {
        if (tvUserName != null)
            tvUserName.setText(name);
    }

    @Override
    public void showRenameDialog(final User myAccount) {
        View vewInflater = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_edit_username, (ViewGroup) getView(), false);
        final EditText input = (EditText) vewInflater.findViewById(R.id.edit_username);
        input.setText(myAccount.name);

         /*Hiển thị dialog với dEitText cho phép người dùng nhập username mới*/
        new AlertDialog.Builder(getContext())
                .setTitle("Thay đổi tên")
                .setView(vewInflater)
                .setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newName = input.getText().toString();
                        if (!myAccount.name.equals(newName)) {
                            presenter.onConfimRenameClick(myAccount, newName);
                        }
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    @Override
    public void showOkCofimDialog() {
        ActivityUtils.alert(getContext(), true, "Mật khẩu",
                "Bạn có muốn đổi lại mật khẩu?",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myAccount = preferenceHelper.getUserInfo();
                        presenter.resetPassword(myAccount);
                        dialog.dismiss();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void showCofimDialogResetPass() {
        ActivityUtils.showAlertCofirm(getContext(), true, "Lấy lại mật khẩu",
                "Gửi đến email " + myAccount.email,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void showConfimDialogErrorSent() {
        ActivityUtils.showAlertCofirm(getContext(), true, "Lỗi",
                "Lỗi khi gửi email cho " + myAccount.email,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void notifyDataSetChanged(List<Configuration> configList) {
        if (infoAdapter != null) {
            infoAdapter.notifyData(configList);
        }
    }

    private void setImageAvatar(Context context, String imgBase64) {
        try {
            Resources res = getResources();
            //Nếu chưa có avatar thì để hình mặc định
            Bitmap src;
            if (imgBase64.equals("default")) {
                src = BitmapFactory.decodeResource(res, R.drawable.default_avata);
            } else {
                byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            }

            imgAvatar.setImageDrawable(ImageUtils.roundedImage(context, src));
        } catch (Exception e) {
        }
    }
}

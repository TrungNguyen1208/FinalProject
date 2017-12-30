package ptit.nttrung.finalproject.ui.comment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import butterknife.BindView;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseActivity;
import ptit.nttrung.finalproject.data.firebase.FirebaseUtil;
import ptit.nttrung.finalproject.data.local.StaticConfig;
import ptit.nttrung.finalproject.model.entity.Comment;


/**
 * Created by TrungNguyen on 9/27/2017.
 */

public class CommentActivity extends BaseActivity {

    @BindView(R.id.edt_title_cmt)
    EditText edtTitleCmt;
    @BindView(R.id.edt_content_cmt)
    EditText edtContentCmt;
    @BindView(R.id.txt_name)
    TextView tvName;
    @BindView(R.id.txt_address)
    TextView tvAddress;
    @BindView(R.id.txt_submit)
    TextView txtSubmit;
    @BindView(R.id.frame_layout_rate)
    FrameLayout layoutRate;

    int vitri = 0, khonggian, giaca, dichvu, chatluong;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setTitle("Đăng bình luận");

        Intent intent = getIntent();
        final String idRes = intent.getStringExtra("idRes");
        String resName = intent.getStringExtra("name");
        String resAddress = intent.getStringExtra("address");

        tvName.setText(resName);
        tvAddress.setText(resAddress);
        layoutRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(CommentActivity.this, ReviewActivity.class), 123);
            }
        });

        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String titleCmt = edtTitleCmt.getText().toString();
                final String contentCmt = edtContentCmt.getText().toString();
                if (contentCmt.isEmpty()) {
                    makeToastError("Hãy nhập nội dung bình luận!");
                } else if (vitri == 0) {
                    makeToastError("Vui lòng đánh giá quán ăn!");
                } else {
                    String newKey = FirebaseUtil.getCommentRef().child(idRes).push().getKey();
                    final Comment comment = new Comment();
                    comment.title = titleCmt;
                    comment.text = contentCmt;
                    comment.uId = StaticConfig.UID;
                    comment.resId = idRes;
                    comment.survey.chatluong = chatluong;
                    comment.survey.vitri = vitri;
                    comment.survey.dichvu = dichvu;
                    comment.survey.giaca = giaca;
                    comment.survey.khonggian = khonggian;
                    comment.survey.dtb = (chatluong + vitri + dichvu + giaca + khonggian) / 5;
                    comment.commentId = newKey;

                    FirebaseUtil.getCommentRef().child(idRes).child(newKey).setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            makeToastSucces("Đăng bình luận thành công");

                            Intent intent2 = new Intent("ACTION_COMMENT");
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("comment", comment);
                            CommentActivity.this.sendBroadcast(intent2);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            makeToastError("Có lỗi xảy ra!");
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (resultCode == RESULT_OK) {
                vitri = data.getExtras().getInt("vitri");
                giaca = data.getExtras().getInt("giaca");
                chatluong = data.getExtras().getInt("chatluong");
                khonggian = data.getExtras().getInt("khonggian");
                dichvu = data.getExtras().getInt("dichvu");
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}

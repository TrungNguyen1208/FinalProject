package ptit.nttrung.finalproject.ui.comment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseActivity;


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

    int vitri = 0;

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
//        ivRate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivityForResult(new Intent(CommentActivity.this, ReviewActivity.class), 123);
//            }
//        });

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
//                    String newKey = FirebaseUtil.getCommentRef().child(idRes).push().getKey();

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
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}

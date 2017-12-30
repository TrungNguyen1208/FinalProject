package ptit.nttrung.finalproject.ui.user_detail;

import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.ButterKnife;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseActivity;

public class UserDetailActivity extends BaseActivity {

    private final String TAG = "UserDetailActivity";
    public static final String USER_ID_EXTRA_NAME = "user_name";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("");
    }
}

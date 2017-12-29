package ptit.nttrung.finalproject.ui.add_restaurant;

import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.ButterKnife;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseActivity;

public class AddRestaurantActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurant);
        ButterKnife.bind(this);

    }
}

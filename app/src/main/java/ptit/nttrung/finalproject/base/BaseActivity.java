package ptit.nttrung.finalproject.base;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.util.widget.ProgressDialogFragment;


/**
 * Created by TrungNguyen on 7/10/2017.
 */

public class BaseActivity extends AppCompatActivity {

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private static final String TAG_DIALOG_FRAGMENT = "tagDialogFragment";

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        bindViews();
    }

    protected void bindViews() {
        ButterKnife.bind(this);
        setupToolbar();
    }

    public void setContentViewWithoutInject(int layoutResId) {
        super.setContentView(layoutResId);
    }

    protected void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    public void showProgressDialog(String message) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getExistingDialogFragment();
        if (prev == null) {
            ProgressDialogFragment fragment = ProgressDialogFragment.newInstance(message);
            fragment.show(ft, TAG_DIALOG_FRAGMENT);
        }
    }

    public void hideProgressDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getExistingDialogFragment();
        if (prev != null) {
            try {
                ft.remove(prev).commit();
            } catch (Exception e) {
                ft.remove(prev).commitAllowingStateLoss();
            }
        }
    }

    private Fragment getExistingDialogFragment() {
        return getSupportFragmentManager().findFragmentByTag(TAG_DIALOG_FRAGMENT);
    }

    public void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void makeToastSucces(String message) {
        Toasty.success(this, message, Toast.LENGTH_SHORT).show();
    }

    public void makeToastError(String message) {
        Toasty.error(this, message, Toast.LENGTH_SHORT).show();
    }

    public Toolbar getToolbar() {
        return toolbar;
    }
}

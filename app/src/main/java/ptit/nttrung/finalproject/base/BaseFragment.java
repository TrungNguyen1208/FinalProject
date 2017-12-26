package ptit.nttrung.finalproject.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;
import ptit.nttrung.finalproject.util.widget.ProgressDialogFragment;

/**
 * Created by TrungNguyen on 8/16/2017.
 */

public class BaseFragment extends Fragment {

    private static final String TAG_DIALOG_FRAGMENT = "tagDialogFragment";

    public void showProgressDialog(String message) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getExistingDialogFragment();
        if (prev == null) {
            ProgressDialogFragment fragment = ProgressDialogFragment.newInstance(message);
            fragment.show(ft, TAG_DIALOG_FRAGMENT);
        }
    }

    public void hideProgressDialog() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
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
        return getActivity().getSupportFragmentManager().findFragmentByTag(TAG_DIALOG_FRAGMENT);
    }

    public void makeToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void makeToastSucces(String message) {
        Toasty.success(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void makeToastError(String message) {
        Toasty.error(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}

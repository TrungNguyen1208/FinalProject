package ptit.nttrung.finalproject.base;

public interface BaseView {
    void makeToastSucces(String msg);

    void makeToastError(String msg);

    void showProgressDialog(String msg);

    void hideProgressDialog();
}

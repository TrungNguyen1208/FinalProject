package ptit.nttrung.finalproject.util.helper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ptit.nttrung.finalproject.R;

/**
 * Created by TrungNguyen on 7/25/2017.
 */

public class ActivityUtils {

    public static Dialog alert(Context context, String title, String message) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);

        if (!title.isEmpty()) {
//            adb.setIcon(R.drawable.alert_dialog_icon);
            adb.setTitle(title);
        }
        adb.setMessage(message);
        adb.setPositiveButton(context.getString(R.string.ok), null);
        return adb.show();
    }


    public static void showOkCancelConfirmAlertDialog(Context context,
                                                      boolean cancelable,
                                                      String title,
                                                      String content,
                                                      String btnOkText,
                                                      String btnCancelText,
                                                      DialogInterface.OnClickListener okClickListene,
                                                      DialogInterface.OnClickListener cancelClickListene) {
        String msgTitle;
        String msgContent;
        String txtOkContent;
        String txtCancelContent;

        if (title.isEmpty()) {
            msgTitle = "";
        } else {
            msgTitle = title;
        }

        if (content.isEmpty()) {
            msgContent = "";
        } else {
            msgContent = content;
        }

        if (btnOkText.isEmpty()) {
            txtOkContent = "";
        } else {
            txtOkContent = btnOkText;
        }

        if (btnCancelText.isEmpty()) {
            txtCancelContent = "";
        } else {
            txtCancelContent = btnCancelText;
        }
        //Title
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (null != msgTitle && !msgTitle.isEmpty()) {
            builder.setTitle(msgTitle);
        }
        // Content
        if (null != msgContent && !msgContent.isEmpty()) {
            builder.setMessage(msgContent);
        }

        builder.setCancelable(cancelable);

        builder.setNegativeButton(btnOkText, okClickListene);
        builder.setPositiveButton(btnCancelText, cancelClickListene);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public static void alert(Context context, boolean hasTitle, String title,
                             String message,
                             DialogInterface.OnClickListener yes_onclicklistener,
                             DialogInterface.OnClickListener no_onclicklistener) {

        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setCancelable(true);
        if (hasTitle) {
            title = "";
            if (!title.isEmpty()) {
                adb.setTitle(title);
            }
        }
        adb.setMessage(message);
        adb.setPositiveButton(context.getString(R.string.yes), yes_onclicklistener);
        adb.setNegativeButton(context.getString(R.string.dismiss), no_onclicklistener);
        // adb.create();
        adb.show();
    }


    public static void showAlertCofirm(Context context, boolean hasTitle,
                                       String title,
                                       String message,
                                       DialogInterface.OnClickListener yes_onclicklistener) {

        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setCancelable(true);
        if (hasTitle) {
            title = "";
            if (!title.isEmpty()) {
                adb.setTitle(title);
            }
        }
        adb.setMessage(message);
        adb.setPositiveButton(context.getString(R.string.yes), yes_onclicklistener);
        // adb.create();
        adb.show();
    }


    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "[a-zA-Z0-9._-]+@[a-z]+(\\.+[a-z]+)+";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static void addFragmentToActivity(FragmentManager fragmentManager,
                                             Fragment fragment,
                                             int frameId,
                                             String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment, tag);
        transaction.commit();
    }

    public static void signOutConfirmation(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Đăng xuất")
                .setMessage("Bạn thực sự muốn đăng xuất khỏi ứng dụng")
                .setIcon(R.drawable.nav_logout)
                .setPositiveButton("Đăng xuất", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        FirebaseAuth.getInstance().signOut();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

}

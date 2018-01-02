package ptit.nttrung.finalproject.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

import ptit.nttrung.finalproject.model.entity.User;

public class SharedPreferenceHelper {

    private static SharedPreferenceHelper instance = null;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private static String SHARE_USER_INFO = "userinfo";
    private static String SHARE_KEY_NAME = "name";
    private static String SHARE_KEY_EMAIL = "email";
    private static String SHARE_KEY_AVATA = "avata";
    private static String SHARE_KEY_UID = "uid";


    private SharedPreferenceHelper() {
    }

    public static SharedPreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceHelper();
            preferences = context.getSharedPreferences(SHARE_USER_INFO, Context.MODE_PRIVATE);
            editor = preferences.edit();
        }
        return instance;
    }

    public void saveUserInfo(User user) {
        editor.putString(SHARE_KEY_NAME, user.name);
        editor.putString(SHARE_KEY_EMAIL, user.email);
        editor.putString(SHARE_KEY_AVATA, user.avata);
        editor.putString(SHARE_KEY_UID, StaticConfig.UID);
        editor.commit();
    }

    public User getUserInfo() {
        String userName = preferences.getString(SHARE_KEY_NAME, "");
        String email = preferences.getString(SHARE_KEY_EMAIL, "");
        String avatar = preferences.getString(SHARE_KEY_AVATA, "default");

        User user = new User();
        user.uid = StaticConfig.UID;
        user.name = userName;
        user.email = email;
        user.avata = avatar;

        return user;
    }

    public String getUID() {
        return preferences.getString(SHARE_KEY_UID, StaticConfig.UID);
    }

    public void saveDouble(String key, double value) {
        String dValue = String.valueOf(value);
        editor.putString(key, dValue);
        editor.commit();
    }

    public double getDouble(String key, double defVa) {
        String strDefVa = String.valueOf(defVa);
        String dValue = preferences.getString(key, strDefVa);
        return (dValue.equals(strDefVa)) ? defVa : Double
                .valueOf(dValue);
    }

    public void saveCurrentLocation(LatLng latLng) {
        saveDouble("current_location_lat", latLng.latitude);
        saveDouble("current_location_lng", latLng.longitude);
    }

    public LatLng getCurrentLocation() {
        return new LatLng(getDouble("current_location_lat", 21.0277644), getDouble("current_location_lng", 105.8341598));
    }
}

package ptit.nttrung.finalproject.model.entity;

import java.util.List;

/**
 * Created by TrungNguyen on 12/29/2017.
 */

public class Restaurant {
    public String resId;
    public String name;
    public String openTime;
    public String closeTime;
    public String desciption;
    public String uIdPost;
    public String minCost;
    public String maxCost;
    public String phoneNumber;
    public long timestamp;

    public String address;
    public double latitude;
    public double longitude;
    public List<String> images;

    public Restaurant() {
    }
}

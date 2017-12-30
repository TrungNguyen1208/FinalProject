package ptit.nttrung.finalproject.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Restaurant implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.resId);
        dest.writeString(this.name);
        dest.writeString(this.openTime);
        dest.writeString(this.closeTime);
        dest.writeString(this.desciption);
        dest.writeString(this.uIdPost);
        dest.writeString(this.minCost);
        dest.writeString(this.maxCost);
        dest.writeString(this.phoneNumber);
        dest.writeLong(this.timestamp);
        dest.writeString(this.address);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeStringList(this.images);
    }

    protected Restaurant(Parcel in) {
        this.resId = in.readString();
        this.name = in.readString();
        this.openTime = in.readString();
        this.closeTime = in.readString();
        this.desciption = in.readString();
        this.uIdPost = in.readString();
        this.minCost = in.readString();
        this.maxCost = in.readString();
        this.phoneNumber = in.readString();
        this.timestamp = in.readLong();
        this.address = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.images = in.createStringArrayList();
    }

    public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel source) {
            return new Restaurant(source);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
}

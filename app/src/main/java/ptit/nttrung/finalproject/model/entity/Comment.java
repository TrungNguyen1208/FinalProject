package ptit.nttrung.finalproject.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class Comment implements Parcelable, Serializable {
    public String commentId;
    public String resId;
    public String uId;
    public String title;
    public String text;
    public List<String> images;
    public long timestamp;
    public Survey survey;

    public Comment() {
        this.survey = new Survey();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.commentId);
        dest.writeString(this.resId);
        dest.writeString(this.uId);
        dest.writeString(this.title);
        dest.writeString(this.text);
        dest.writeStringList(this.images);
        dest.writeLong(this.timestamp);
        dest.writeParcelable(this.survey, flags);
    }

    protected Comment(Parcel in) {
        this.commentId = in.readString();
        this.resId = in.readString();
        this.uId = in.readString();
        this.title = in.readString();
        this.text = in.readString();
        this.images = in.createStringArrayList();
        this.timestamp = in.readLong();
        this.survey = in.readParcelable(Survey.class.getClassLoader());
    }

    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}

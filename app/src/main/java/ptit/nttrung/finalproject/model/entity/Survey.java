package ptit.nttrung.finalproject.model.entity;


import android.os.Parcel;
import android.os.Parcelable;

public class Survey implements Parcelable {
    public int vitri;
    public int giaca;
    public int chatluong;
    public int dichvu;
    public int khonggian;
    public double dtb;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.vitri);
        dest.writeInt(this.giaca);
        dest.writeInt(this.chatluong);
        dest.writeInt(this.dichvu);
        dest.writeInt(this.khonggian);
        dest.writeDouble(this.dtb);
    }

    public Survey() {
    }

    protected Survey(Parcel in) {
        this.vitri = in.readInt();
        this.giaca = in.readInt();
        this.chatluong = in.readInt();
        this.dichvu = in.readInt();
        this.khonggian = in.readInt();
        this.dtb = in.readDouble();
    }

    public static final Parcelable.Creator<Survey> CREATOR = new Parcelable.Creator<Survey>() {
        @Override
        public Survey createFromParcel(Parcel source) {
            return new Survey(source);
        }

        @Override
        public Survey[] newArray(int size) {
            return new Survey[size];
        }
    };
}

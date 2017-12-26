package ptit.nttrung.finalproject.model.pojo;

/**
 * Created by TrungNguyen on 10/23/2017.
 */

public class Result {
    private String formatted_address;
    private String place_id;

    public Result() {
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }
}

package ptit.nttrung.finalproject.model.entity;

import java.util.List;

/**
 * Created by TrungNguyen on 12/29/2017.
 */

public class Comment {
    public String commentId;
    public String resId;
    public String uId;
    public String title;
    public String text;
    public List<String> images;
    public long timestamp;
    public Survey survey;

    public Comment() {
    }
}

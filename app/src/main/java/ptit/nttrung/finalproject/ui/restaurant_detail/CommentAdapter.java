package ptit.nttrung.finalproject.ui.restaurant_detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.data.firebase.FirebaseUtil;
import ptit.nttrung.finalproject.model.entity.Comment;
import ptit.nttrung.finalproject.util.helper.ImageUtils;


/**
 * Created by TrungNguyen on 10/25/2017.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context mContext;
    private List<Comment> commentList;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.mContext = context;
        this.commentList = commentList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_row_cmt, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.tvContentCmtItem.setText(comment.text);
        holder.tvTitleCmtItem.setText(comment.text);
        holder.tvPointCmtItem.setText(String.valueOf(comment.survey.dtb));

        FirebaseUtil.getUserRef().child(comment.uId).child("avata").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String imageAvata64 = dataSnapshot.getValue(String.class);
                ImageUtils.loadAvata(mContext, imageAvata64, holder.ivAvatarCmtItem);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (commentList != null) {
            return commentList.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_cmt_avatar_item)
        CircleImageView ivAvatarCmtItem;
        @BindView(R.id.tv_title_cmt_item)
        TextView tvTitleCmtItem;
        @BindView(R.id.tv_content_cmt_item)
        TextView tvContentCmtItem;
        @BindView(R.id.tv_point_cmt_item)
        TextView tvPointCmtItem;
        @BindView(R.id.ll_cmt)
        LinearLayout llCmt;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

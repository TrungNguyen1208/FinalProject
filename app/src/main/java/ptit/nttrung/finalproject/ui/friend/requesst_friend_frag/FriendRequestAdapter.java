package ptit.nttrung.finalproject.ui.friend.requesst_friend_frag;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.model.entity.User;
import ptit.nttrung.finalproject.util.helper.ImageUtils;


/**
 * Created by TrungNguyen on 10/10/2017.
 */

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    private Context context;
    private List<User> friends;
    public OnItemClick onItemClick;

    public void setFriends(List<User> friends) {
        this.friends.clear();
        this.friends.addAll(friends);
        this.notifyDataSetChanged();
    }

    public FriendRequestAdapter(Context context, List<User> friends) {
        this.context = context;
        this.friends = friends;
    }

    public interface OnItemClick {
        public void onAcceptClick(int position);

        public void onDeniedClick(int position);

        public void onImageClick(int position);
    }


    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_friend_request, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final User friend = friends.get(position);
        holder.txtNameFriendRq.setText(friend.name);
        ImageUtils.loadAvata(context, friend.avata, holder.ivAvataFriendRq);
        final String idFriendRq = friend.uid;

        holder.btnAcceptRq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onAcceptClick(position);
            }
        });

        holder.btnDeteleRq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onDeniedClick(position);
            }
        });

        holder.ivAvataFriendRq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onImageClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_avata_friend_rq)
        CircleImageView ivAvataFriendRq;
        @BindView(R.id.txt_name_friend_rq)
        TextView txtNameFriendRq;
        @BindView(R.id.btn_detele_rq)
        Button btnDeteleRq;
        @BindView(R.id.btn_accept_rq)
        Button btnAcceptRq;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

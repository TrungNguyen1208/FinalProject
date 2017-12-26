package ptit.nttrung.finalproject.ui.add_group;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.data.local.StaticConfig;
import ptit.nttrung.finalproject.model.entity.Friend;
import ptit.nttrung.finalproject.model.entity.Group;
import ptit.nttrung.finalproject.model.entity.ListFriend;


/**
 * Created by TrungNguyen on 9/26/2017.
 */

public class ListPeopleAdapter extends RecyclerView.Adapter<ListPeopleAdapter.ViewHolder> {

    private Context context;
    private ListFriend listFriend;
    private LinearLayout btnAddGroup;
    private Set<String> listIDChoose;
    private Set<String> listIDRemove;
    private boolean isEdit;
    private Group editGroup;

    public ListPeopleAdapter(Context context, ListFriend listFriend, LinearLayout btnAddGroup, Set<String> listIDChoose, Set<String> listIDRemove, boolean isEdit, Group editGroup) {
        this.context = context;
        this.listFriend = listFriend;
        this.btnAddGroup = btnAddGroup;
        this.listIDChoose = listIDChoose;
        this.listIDRemove = listIDRemove;

        this.isEdit = isEdit;
        this.editGroup = editGroup;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_add_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Friend friend = listFriend.getListFriend().get(position);
        holder.txtName.setText(friend.name);
        holder.txtEmail.setText(friend.email);

        String avata = friend.avata;
        final String id = friend.id;
        if (!avata.equals(StaticConfig.STR_DEFAULT_BASE64)) {
            byte[] decodedString = Base64.decode(avata, Base64.DEFAULT);
            holder.avata.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
        } else {
            holder.avata.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.default_avata));
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    listIDChoose.add(id);
                    listIDRemove.remove(id);
                } else {
                    listIDRemove.add(id);
                    listIDChoose.remove(id);
                }
                if (listIDChoose.size() >= 3) {
                    btnAddGroup.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                } else {
                    btnAddGroup.setBackgroundColor(context.getResources().getColor(R.color.grey_500));
                }
            }
        });
        if (isEdit && editGroup.member.contains(id)) {
            holder.checkBox.setChecked(true);
        } else if (editGroup != null && !editGroup.member.contains(id)) {
            holder.checkBox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return listFriend.getListFriend().size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.icon_avata)
        CircleImageView avata;
        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.txtEmail)
        TextView txtEmail;
        @BindView(R.id.checkAddPeople)
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
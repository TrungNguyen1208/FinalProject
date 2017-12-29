package ptit.nttrung.finalproject.ui.add_group;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;

import java.util.HashSet;
import java.util.Set;

import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseActivity;
import ptit.nttrung.finalproject.data.local.GroupDB;
import ptit.nttrung.finalproject.data.local.StaticConfig;
import ptit.nttrung.finalproject.model.entity.Group;
import ptit.nttrung.finalproject.model.entity.ListFriend;
import ptit.nttrung.finalproject.model.entity.Room;

public class AddGroupActivity extends BaseActivity implements AddGroupView {

    private RecyclerView recyclerListFriend;
    private ListPeopleAdapter adapter;
    private ListFriend listFriend;
    private LinearLayout btnAddGroup;
    private Set<String> listIDChoose;
    private Set<String> listIDRemove;
    private EditText editTextGroupName;
    private TextView txtGroupIcon, txtActionName;
    private LovelyProgressDialog dialogWait;
    private boolean isEditGroup;
    private Group groupEdit;

    private AddGroupPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        Intent intentData = getIntent();
        txtActionName = (TextView) findViewById(R.id.txtActionName);

        presenter = new AddGroupPresenter(this);
        presenter.attachView(this);
        listFriend = presenter.getListFriend();

        listIDChoose = new HashSet<>();
        listIDRemove = new HashSet<>();
        listIDChoose.add(StaticConfig.UID);
        btnAddGroup = (LinearLayout) findViewById(R.id.btnAddGroup);
        editTextGroupName = (EditText) findViewById(R.id.editGroupName);
        txtGroupIcon = (TextView) findViewById(R.id.icon_group);
        dialogWait = new LovelyProgressDialog(this).setCancelable(false);

        editTextGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 1) {
                    txtGroupIcon.setText((charSequence.charAt(0) + "").toUpperCase());
                } else {
                    txtGroupIcon.setText("R");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("listIDChoose", String.valueOf(listIDChoose.size()));
                if (listIDChoose.size() < 3) {
                    Toast.makeText(AddGroupActivity.this, "Cần ít nhất 2 người để tạo nhóm trò chuyện", Toast.LENGTH_SHORT).show();
                } else {
                    if (editTextGroupName.getText().length() == 0) {
                        Toast.makeText(AddGroupActivity.this, "Nhập tên nhóm", Toast.LENGTH_SHORT).show();
                    } else {
                        if (isEditGroup) {
                            editGroup();
                        } else {
                            createGroup();
                        }
                    }
                }
            }
        });

        if (intentData.getStringExtra("groupId") != null) {
            isEditGroup = true;
            String idGroup = intentData.getStringExtra("groupId");
            txtActionName.setText("Save");
            btnAddGroup.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            groupEdit = GroupDB.getInstance(this).getGroup(idGroup);
            editTextGroupName.setText(groupEdit.groupInfo.get("name"));
        } else {
            isEditGroup = false;
        }

        recyclerListFriend = (RecyclerView) findViewById(R.id.recycleListFriend);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerListFriend.setLayoutManager(linearLayoutManager);
        adapter = new ListPeopleAdapter(this, listFriend, btnAddGroup, listIDChoose, listIDRemove, isEditGroup, groupEdit);
        recyclerListFriend.setAdapter(adapter);
    }

    private void editGroup() {
        dialogWait.setIcon(R.drawable.ic_add_group_dialog)
                .setTitle("Editing....")
                .setTopColorRes(R.color.colorPrimary)
                .show();
        //Delete group
        final String idGroup = groupEdit.id;
        Room room = new Room();
        for (String id : listIDChoose) {
            room.member.add(id);
        }
        room.groupInfo.put("name", editTextGroupName.getText().toString());
        room.groupInfo.put("admin", StaticConfig.UID);
        presenter.editGroup(idGroup, room);
    }

    private void createGroup() {
        dialogWait.setIcon(R.drawable.ic_add_group_dialog)
                .setTitle("Registering....")
                .setTopColorRes(R.color.colorAccent)
                .show();

        final String idGroup = (StaticConfig.UID + System.currentTimeMillis()).hashCode() + "";
        Room room = new Room();
        for (String id : listIDChoose) {
            room.member.add(id);
        }
        room.groupInfo.put("name", editTextGroupName.getText().toString());
        room.groupInfo.put("admin", StaticConfig.UID);
        presenter.createGroup(idGroup, room);
    }

    private void deleteRoomForUser(final String roomId, final int userIndex) {
        if (userIndex == listIDRemove.size()) {
            dialogWait.dismiss();
            Toast.makeText(this, "Edit group success", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK, null);
            AddGroupActivity.this.finish();
        } else {
            FirebaseDatabase.getInstance().getReference().child("user/" + listIDRemove.toArray()[userIndex] + "/group/" + roomId).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            deleteRoomForUser(roomId, userIndex + 1);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialogWait.dismiss();
                            new LovelyInfoDialog(AddGroupActivity.this) {
                                @Override
                                public LovelyInfoDialog setConfirmButtonText(String text) {
                                    findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dismiss();
                                        }
                                    });
                                    return super.setConfirmButtonText(text);
                                }
                            }
                                    .setTopColorRes(R.color.colorAccent)
                                    .setIcon(R.drawable.ic_add_group_dialog)
                                    .setTitle("False")
                                    .setMessage("Cannot connect database")
                                    .setCancelable(false)
                                    .setConfirmButtonText("Ok")
                                    .show();
                        }
                    });
        }
    }

    @Override
    public void addRoomForUser(final String roomId, final int userIndex) {
        if (userIndex == listIDChoose.size()) {
            if (!isEditGroup) {
                dialogWait.dismiss();
                Toast.makeText(this, "Tạo nhóm thành công", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, null);
                AddGroupActivity.this.finish();
            } else {
                deleteRoomForUser(roomId, 0);
            }
        } else {
            FirebaseDatabase.getInstance().getReference().child("user/" + listIDChoose.toArray()[userIndex] + "/group/" + roomId).setValue(roomId).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    addRoomForUser(roomId, userIndex + 1);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialogWait.dismiss();
                    new LovelyInfoDialog(AddGroupActivity.this) {
                        @Override
                        public LovelyInfoDialog setConfirmButtonText(String text) {
                            findView(com.yarolegovich.lovelydialog.R.id.ld_btn_confirm).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dismiss();
                                }
                            });
                            return super.setConfirmButtonText(text);
                        }
                    }
                            .setTopColorRes(R.color.colorAccent)
                            .setIcon(R.drawable.ic_add_group_dialog)
                            .setTitle("False")
                            .setMessage("Create group false")
                            .setCancelable(false)
                            .setConfirmButtonText("Ok")
                            .show();
                }
            });
        }
    }

    @Override
    public void hideDialogWait() {
        if (dialogWait != null) dialogWait.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}

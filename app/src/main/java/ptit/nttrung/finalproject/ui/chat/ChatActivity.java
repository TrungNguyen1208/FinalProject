package ptit.nttrung.finalproject.ui.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseActivity;
import ptit.nttrung.finalproject.data.local.SharedPreferenceHelper;
import ptit.nttrung.finalproject.data.local.StaticConfig;
import ptit.nttrung.finalproject.model.entity.Consersation;
import ptit.nttrung.finalproject.model.entity.Message;


/**
 * Created by TrungNguyen on 9/26/2017.
 */

public class ChatActivity extends BaseActivity implements ChatView {

    @BindView(R.id.btnSend)
    ImageButton btnSend;
    @BindView(R.id.recyclerChat)
    RecyclerView recyclerChat;
    @BindView(R.id.editWriteMessage)
    EditText editWriteMessage;

    public static final int VIEW_TYPE_USER_MESSAGE = 0;
    public static final int VIEW_TYPE_FRIEND_MESSAGE = 1;

    private ListMessageAdapter adapter;
    private String roomId;
    private ArrayList<CharSequence> idFriend;
    private Consersation consersation = new Consersation();
    private LinearLayoutManager linearLayoutManager;
    private ChatPresenter presenter = new ChatPresenter();

    public static HashMap<String, Bitmap> bitmapAvataFriend;
    public Bitmap bitmapAvataUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        presenter.attachView(this);

        Intent intentData = getIntent();
        idFriend = intentData.getCharSequenceArrayListExtra(StaticConfig.INTENT_KEY_CHAT_ID);
        roomId = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID);
        String nameFriend = intentData.getStringExtra(StaticConfig.INTENT_KEY_CHAT_FRIEND);

        //Avatar
        String base64AvataUser = SharedPreferenceHelper.getInstance(this).getUserInfo().avata;
        if (!base64AvataUser.equals(StaticConfig.STR_DEFAULT_BASE64)) {
            byte[] decodedString = Base64.decode(base64AvataUser, Base64.DEFAULT);
            bitmapAvataUser = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } else {
            bitmapAvataUser = null;
        }

        if (idFriend != null && nameFriend != null) {
            getSupportActionBar().setTitle(nameFriend);
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerChat = (RecyclerView) findViewById(R.id.recyclerChat);
            recyclerChat.setLayoutManager(linearLayoutManager);
            adapter = new ListMessageAdapter(this, consersation, bitmapAvataFriend, bitmapAvataUser);
            presenter.getListMesseage(roomId);
            recyclerChat.setAdapter(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent result = new Intent();
            result.putExtra("idFriend", idFriend.get(0));
            setResult(RESULT_OK, result);
            this.finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        result.putExtra("idFriend", idFriend.get(0));
        setResult(RESULT_OK, result);
        this.finish();
    }

    @OnClick(R.id.btnSend)
    public void onViewClicked() {
        String content = editWriteMessage.getText().toString().trim();
        if (content.length() > 0) {
            editWriteMessage.setText("");
            Message newMessage = new Message();
            newMessage.text = content;
            newMessage.idSender = StaticConfig.UID;
            newMessage.idReceiver = roomId;
            newMessage.timestamp = System.currentTimeMillis();
            presenter.sendMessage(roomId, newMessage);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void showMessage(DataSnapshot dataSnapshot) {
        HashMap mapMessage = (HashMap) dataSnapshot.getValue();

        Message newMessage = new Message();
        newMessage.idSender = (String) mapMessage.get("idSender");
        newMessage.idReceiver = (String) mapMessage.get("idReceiver");
        newMessage.text = (String) mapMessage.get("text");
        newMessage.timestamp = (long) mapMessage.get("timestamp");

        consersation.getListMessageData().add(newMessage);
        adapter.notifyDataSetChanged();
        linearLayoutManager.scrollToPosition(consersation.getListMessageData().size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}

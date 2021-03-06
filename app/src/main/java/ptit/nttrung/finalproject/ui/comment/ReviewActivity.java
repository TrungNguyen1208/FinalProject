package ptit.nttrung.finalproject.ui.comment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.base.BaseActivity;

public class ReviewActivity extends BaseActivity implements View.OnClickListener {

    LinearLayout back_button_gallery;
    TextView text_view_done;
    @BindView(R.id.vitri)
    DiscreteSeekBar vitri;
    @BindView(R.id.giaca)
    DiscreteSeekBar giaca;
    @BindView(R.id.chatluong)
    DiscreteSeekBar chatluong;
    @BindView(R.id.dichvu)
    DiscreteSeekBar dichvu;
    @BindView(R.id.khonggian)
    DiscreteSeekBar khonggian;
    @BindView(R.id.point_vitri)
    TextView pointVitri;
    @BindView(R.id.point_giaca)
    TextView pointGiaca;
    @BindView(R.id.point_chatluong)
    TextView pointChatluong;
    @BindView(R.id.point_dichvu)
    TextView pointDichvu;
    @BindView(R.id.point_khonggian)
    TextView pointKhonggian;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_rest);
        ButterKnife.bind(this);

        back_button_gallery = (LinearLayout) findViewById(R.id.back_button_gallery);
        text_view_done = (TextView) findViewById(R.id.text_view_done);

        back_button_gallery.setOnClickListener(this);
        text_view_done.setOnClickListener(this);

        setupChangeProgressSeekBar(pointGiaca, giaca);
        setupChangeProgressSeekBar(pointChatluong, chatluong);
        setupChangeProgressSeekBar(pointDichvu, dichvu);
        setupChangeProgressSeekBar(pointKhonggian, khonggian);
        setupChangeProgressSeekBar(pointVitri, vitri);
    }

    public void sendData() {
        Intent intent = new Intent();
        intent.putExtra("vitri", vitri.getProgress());
        intent.putExtra("giaca", giaca.getProgress());
        intent.putExtra("chatluong", chatluong.getProgress());
        intent.putExtra("khonggian", khonggian.getProgress());
        intent.putExtra("dichvu", dichvu.getProgress());

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_view_done:
                sendData();
                break;
            case R.id.back_button_gallery:
                finish();
                break;
        }
    }

    private void setupChangeProgressSeekBar(final TextView textView, DiscreteSeekBar seekBar1) {
        seekBar1.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                textView.setText(String.valueOf(value));
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
    }
}

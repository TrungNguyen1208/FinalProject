package ptit.nttrung.finalproject.ui.gallery.file;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.ui.gallery.IOnClickImage;


public class GalleryFileActivity extends AppCompatActivity implements View.OnClickListener, IOnClickImage {

    TextView text_view_name_folder_image_item;
    LinearLayout back_button_gallery;
    TextView text_view_done;
    RecyclerView grid_view_file;

    int mode;
    ArrayList<ImageGalleryBean> data;
    GalleryFileAdapter adapter;
    String namefolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_file);

        text_view_name_folder_image_item = (TextView) findViewById(R.id.text_view_name_folder_image_item);
        back_button_gallery = (LinearLayout) findViewById(R.id.back_button_gallery);
        grid_view_file = (RecyclerView) findViewById(R.id.recycle_view);
        text_view_done = (TextView) findViewById(R.id.text_view_done);

        back_button_gallery.setOnClickListener(this);
        text_view_done.setOnClickListener(this);

        this.namefolder = getIntent().getStringExtra("namefolder");
        this.mode = getIntent().getIntExtra("mode", 1);

        this.data = getIntent().getParcelableArrayListExtra("data");
        text_view_name_folder_image_item.setText(namefolder);

        adapter = new GalleryFileAdapter(getApplicationContext(), data);
        adapter.setiOnClickImage(this);

        grid_view_file.setLayoutManager(new GridLayoutManager(this, 3));
        grid_view_file.setAdapter(adapter);
    }

    private int lastPosition = -1;

    private int selectedPosition(ImageGalleryBean item) {
        for (int i = 0; i < this.adapter.imageSelected.size(); i++) {
            ImageGalleryBean im = this.adapter.imageSelected.get(i);
            if (item.getPath().equals(im.getPath())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onClickImage(View v, int index) {
        int selectedItemPosition = selectedPosition(this.data.get(index));
        if (this.mode == 1) {
            if (selectedItemPosition != -1) {
                this.adapter.removeSelectedPosition(selectedItemPosition, index);
            } else {
                this.adapter.addSelected(this.data.get(index));
            }
        } else if (this.mode == 0) {
            if (selectedItemPosition != -1) {
                this.adapter.removeSelectedPosition(selectedItemPosition, index);
            } else {
                if (this.adapter.imageSelected.size() > 0) {
                    this.adapter.removeAllSelectedSingleClick();
                }
                this.adapter.addSelected(this.data.get(index));

            }
        }
    }

    private void sendData() {
        ArrayList<ImageGalleryBean> selectedImage = this.adapter.imageSelected;
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("images", selectedImage);
        setResult(Activity.RESULT_OK, intent);
        Toasty.success(this, "Send Image Succes", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button_gallery:
                finish();
                break;
            case R.id.text_view_done:
                sendData();
                break;
            default:
                break;
        }
    }
}

package ptit.nttrung.finalproject.ui.gallery.file;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import ptit.nttrung.finalproject.R;
import ptit.nttrung.finalproject.ui.gallery.IOnClickImage;


public class GalleryFileAdapter extends RecyclerView.Adapter<GalleryFileAdapter.ViewHolder> {
    Context context;
    ArrayList<ImageGalleryBean> data;
    ArrayList<ImageGalleryBean> imageSelected = new ArrayList<>();
    IOnClickImage iOnClickImage;

    public void setiOnClickImage(IOnClickImage iOnClickImage) {
        this.iOnClickImage = iOnClickImage;
    }

    public GalleryFileAdapter(Context context, ArrayList<ImageGalleryBean> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.gallery_image_item, parent, false), iOnClickImage);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ImageGalleryBean item = (ImageGalleryBean) this.data.get(position);

        Glide.with(context).load("file://" + item.getPath())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.image_view);

        if (isSelected(item)) {
            holder.image_view_check.setChecked(true);
        } else {
            holder.image_view_check.setChecked(false);
        }

        return;
    }

    private boolean isSelected(ImageGalleryBean image) {
        for (ImageGalleryBean selectedImage : this.imageSelected) {
            if (selectedImage.getPath().equals(image.getPath())) {
                return true;
            }
        }
        return false;
    }

    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<ImageGalleryBean> images) {
        this.data.clear();
        this.data.addAll(images);
    }

    public void addAll(List<ImageGalleryBean> images) {
        int startIndex = this.data.size();
        this.data.addAll(startIndex, images);
        notifyItemRangeInserted(startIndex, images.size());
    }

    public void addSelected(ImageGalleryBean image) {
        for (int i = 0; i < imageSelected.size(); i++) {
            ImageGalleryBean item = imageSelected.get(i);
            if (item.getPath().equals(image.getPath())) {
                return;
            }
        }
        this.imageSelected.add(image);
        notifyItemChanged(this.data.indexOf(image));
    }

    public void removeSelectedImage(ImageGalleryBean image) {
        for (int i = 0; i < imageSelected.size(); i++) {
            ImageGalleryBean item = imageSelected.get(i);
            if (item.getPath().equals(image.getPath())) {
                this.imageSelected.remove(i);
                break;
            }
        }
        notifyItemChanged(this.data.indexOf(image));
    }

    public void removeSelectedPosition(int position, int clickPosition) {
        this.imageSelected.remove(position);
        notifyItemChanged(clickPosition);
    }

    public void removeSelectedPosition(ImageGalleryBean item, int clickPosition) {
        this.imageSelected.remove(item);
        notifyItemChanged(clickPosition);
    }

    public void removeAllSelectedSingleClick() {
        this.imageSelected.clear();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View v;
        ImageView image_view;
        CheckedTextView image_view_check;
        public int position;
        IOnClickImage iOnClickImage;

        public ViewHolder(View v, IOnClickImage iOnClickImage) {
            super(v);
            this.v = v;
            image_view = (ImageView) v.findViewById(R.id.image_view_file);
            image_view_check = (CheckedTextView) v.findViewById(R.id.check_text_view);
            image_view_check.bringToFront();
            this.iOnClickImage = iOnClickImage;
            this.v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iOnClickImage.onClickImage(v, getAdapterPosition());
        }
    }
}
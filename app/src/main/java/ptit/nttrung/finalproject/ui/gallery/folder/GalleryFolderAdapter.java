package ptit.nttrung.finalproject.ui.gallery.folder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import ptit.nttrung.finalproject.R;


public class GalleryFolderAdapter extends BaseAdapter {
    Context context;
    List<FolderGalleryBean> data;

    public GalleryFolderAdapter(Context context, List<FolderGalleryBean> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount() == 0 ? 1 : getCount();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.gallery_folder_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FolderGalleryBean item = this.data.get(position);

        holder.text_view_name_folder.setText(item.getFolder());

        Glide.with(context).load("file://" + item.getImageInFolder().get(0).getPath())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(holder.image_view_sample);

        return convertView;
    }

    class ViewHolder {
        ImageView image_view_sample;
        TextView text_view_name_folder;

        public ViewHolder(View v) {
            image_view_sample = (ImageView) v.findViewById(R.id.image_view_sample);
            text_view_name_folder = (TextView) v.findViewById(R.id.text_view_name_folder);
        }
    }
}
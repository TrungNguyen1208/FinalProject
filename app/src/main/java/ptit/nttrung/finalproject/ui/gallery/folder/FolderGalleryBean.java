package ptit.nttrung.finalproject.ui.gallery.folder;

import java.util.ArrayList;

import ptit.nttrung.finalproject.ui.gallery.file.ImageGalleryBean;


public class FolderGalleryBean {
    String folder;
    ArrayList<ImageGalleryBean> imageInFolder;

    public FolderGalleryBean() {
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public ArrayList<ImageGalleryBean> getImageInFolder() {
        return imageInFolder;
    }

    public void setImageInFolder(ArrayList<ImageGalleryBean> imageInFolder) {
        this.imageInFolder = imageInFolder;
    }
}

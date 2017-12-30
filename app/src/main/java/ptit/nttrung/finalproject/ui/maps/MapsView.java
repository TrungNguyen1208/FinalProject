package ptit.nttrung.finalproject.ui.maps;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import ptit.nttrung.finalproject.base.BaseView;

public interface MapsView extends BaseView {
    public void drawDirectionMap(List<LatLng> list);
}


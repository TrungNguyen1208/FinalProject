package ptit.nttrung.finalproject.model.pojo;

import java.util.List;

/**
 * Created by TrungNguyen on 9/13/2017.
 */

public class DirectionRoot {
    private List<Route> routes;
    private String status;

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

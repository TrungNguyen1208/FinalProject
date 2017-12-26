package ptit.nttrung.finalproject.model.entity;

import java.util.ArrayList;


public class Consersation {
    private ArrayList<Message> listMessageData;

    public Consersation() {
        listMessageData = new ArrayList<>();
    }

    public ArrayList<Message> getListMessageData() {
        return listMessageData;
    }
}

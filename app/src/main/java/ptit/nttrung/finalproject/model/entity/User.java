package ptit.nttrung.finalproject.model.entity;

public class User {
    public String uid;
    public String name;
    public String email;
    public String avata;
    public String phone;
    public Status status;
    public Message message;

    public User(){
        status = new Status();
        message = new Message();
        status.isOnline = false;
        status.timestamp = 0;
        message.idReceiver = "0";
        message.idSender = "0";
        message.text = "";
        message.timestamp = 0;
    }

    public User(String uid, String name, String email, String avata) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.avata = avata;
    }
}

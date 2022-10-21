package kashyap.anurag.whatsappclone.Model;

public class ModelGroupMessage {
    String message, date, time, senderUid;

    public ModelGroupMessage() {
    }

    public ModelGroupMessage(String message, String date, String time, String senderUid) {
        this.message = message;
        this.date = date;
        this.time = time;
        this.senderUid = senderUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }
}

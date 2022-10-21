package kashyap.anurag.whatsappclone.Model;

public class ModelMessage {
    String message, type, from, to, messageId, time, date;

    public ModelMessage() {
    }

    public ModelMessage(String message, String type, String from, String to, String messageId, String time, String date) {
        this.message = message;
        this.type = type;
        this.from = from;
        this.to = to;
        this.messageId = messageId;
        this.time = time;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

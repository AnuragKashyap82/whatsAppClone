package kashyap.anurag.whatsappclone.Model;

public class ModelGroup {

    String groupName, timestamp, groupIcon, adminId;

    public ModelGroup() {
    }

    public ModelGroup(String groupName, String timestamp, String groupIcon, String adminId) {
        this.groupName = groupName;
        this.timestamp = timestamp;
        this.groupIcon = groupIcon;
        this.adminId = adminId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
}


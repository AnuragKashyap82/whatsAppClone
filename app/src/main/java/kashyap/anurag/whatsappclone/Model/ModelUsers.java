package kashyap.anurag.whatsappclone.Model;

public class ModelUsers {
    String uid, userName, userStatus, profileImage;

    public ModelUsers() {
    }

    public ModelUsers(String uid, String userName, String userStatus, String profileImage, String requestType) {
        this.uid = uid;
        this.userName = userName;
        this.userStatus = userStatus;
        this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

}
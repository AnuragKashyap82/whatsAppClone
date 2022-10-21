package kashyap.anurag.whatsappclone.Model;

public class ModelFeeds {
    String uid, caption, postId, postImage;

    public ModelFeeds() {
    }

    public ModelFeeds(String uid, String caption, String postId, String postImage) {
        this.uid = uid;
        this.caption = caption;
        this.postId = postId;
        this.postImage = postImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }
}

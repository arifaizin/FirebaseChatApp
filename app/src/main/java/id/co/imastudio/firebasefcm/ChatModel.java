package id.co.imastudio.firebasefcm;

/**
 * Created by idn on 4/14/2017.
 */

public class ChatModel {
    private String id;
    private String text;
    private String name;
    private String photoUrl;
    private Long timestamp;

    //alt+insert > generate > constructor >kosongan
    public ChatModel() {
        //Needed for Firebase
    }

    //alt+insert > generate > constructor >pilih semua
    public ChatModel(String text, String name, String photoUrl, Long timestamp ) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.timestamp = timestamp;
    }

    //alt+insert > generate > getter and setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}

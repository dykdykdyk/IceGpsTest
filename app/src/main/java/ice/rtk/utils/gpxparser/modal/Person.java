package ice.rtk.Utils.gpxparser.modal;

/**
 * Created by Himanshu on 7/5/2015.
 */
public class Person {
    private String name;
    private Email email;
    private ice.rtk.Utils.gpxparser.modal.Link link;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public ice.rtk.Utils.gpxparser.modal.Link getLink() {
        return link;
    }

    public void setLink(ice.rtk.Utils.gpxparser.modal.Link link) {
        this.link = link;
    }
}

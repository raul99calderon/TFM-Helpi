package es.upm.miw.helpi.models;

import android.graphics.Bitmap;

public class OrganizationItem {
    private String email;
    private String name;
    private Bitmap photo;

    public OrganizationItem() {
    }

    public OrganizationItem(String email, String name, Bitmap photo) {
        this.email = email;
        this.name = name;
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}

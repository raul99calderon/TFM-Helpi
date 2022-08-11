package es.upm.miw.helpi.models;

import android.graphics.Bitmap;

public class RankingUserItem extends RankingUser {
    private Bitmap photo;

    public RankingUserItem() {
        super();
    }

    public RankingUserItem(String email, String name, String role, Integer numAttendedEvents, Bitmap photo) {
        super(email, name, role, numAttendedEvents);
        this.photo = photo;
    }

    public RankingUserItem(RankingUser rankingUser, Bitmap photo) {
        super(rankingUser.getEmail(), rankingUser.getName(), rankingUser.getRole(), rankingUser.getNumAttendedEvents());
        this.photo = photo;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}

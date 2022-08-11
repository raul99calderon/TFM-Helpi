package es.upm.miw.helpifororganizations.models;

public class Notice {
    private String title, body, email;
    private Long dateTime;

    public Notice() {
    }

    public Notice(String title, String body, Long dateTime, String email) {
        this.title = title;
        this.body = body;
        this.dateTime = dateTime;
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

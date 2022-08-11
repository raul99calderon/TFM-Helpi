package es.upm.miw.helpifororganizations.models;

public class NoticeDto {

    private String title;
    private Long dateTime;
    private String email;

    public NoticeDto() {
    }

    public NoticeDto(String title, Long dateTime, String email) {
        this.title = title;
        this.dateTime = dateTime;
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

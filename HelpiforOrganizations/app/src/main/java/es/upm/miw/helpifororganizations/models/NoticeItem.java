package es.upm.miw.helpifororganizations.models;

public class NoticeItem extends NoticeDto {

    private String key;

    public NoticeItem() {
        super();
    }

    public NoticeItem(NoticeDto noticeDto, String key) {
        super(noticeDto.getTitle(), noticeDto.getDateTime(), noticeDto.getEmail());
        this.key = key;
    }

    public NoticeItem(String title, Long dateTime, String email, String key) {
        super(title, dateTime, email);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

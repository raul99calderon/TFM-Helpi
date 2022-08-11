package es.upm.miw.helpi.models;

public class NoticeItem extends NoticeDto {

    private String key;
    private String organizationName;

    public NoticeItem() {
        super();
    }

    public NoticeItem(NoticeDto noticeDto, String key, String organizationName) {
        super(noticeDto.getTitle(), noticeDto.getDateTime(), noticeDto.getEmail());
        this.key = key;
        this.organizationName = organizationName;
    }

    public NoticeItem(String title, Long dateTime, String email, String key, String organizationName) {
        super(title, dateTime, email);
        this.key = key;
        this.organizationName = organizationName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}

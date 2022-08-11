package es.upm.miw.helpiforstaff.models;

public class EventItem extends EventDto {
    private String organizationName;

    public EventItem() {
        super();
    }

    public EventItem(EventDto eventDto, String organizationName) {
        super(eventDto);
        this.organizationName = organizationName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}

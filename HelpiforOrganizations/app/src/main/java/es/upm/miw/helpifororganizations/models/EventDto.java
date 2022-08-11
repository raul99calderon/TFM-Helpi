package es.upm.miw.helpifororganizations.models;

public class EventDto extends Event {
    private String key;

    public EventDto() {
        super();
    }

    public EventDto(String name, String description, Location location, String organizationEmail, Long dateTime, Integer maxParticipants, String key) {
        super(name, description, location, organizationEmail, dateTime, maxParticipants);
        this.key = key;
    }

    public EventDto(Event event, String key) {
        super(event.getName(),event.getDescription(),event.getLocation(),event.getOrganizationEmail(),event.getDateTime(), event.getMaxParticipants());
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

package es.upm.miw.helpifororganizations.models;

public class Event {
    private String name, description, organizationEmail;
    private Long dateTime;
    private Integer maxParticipants;
    private Location location;

    public Event() {
        // empty for queries
    }

    public Event(String name, String description, Location location, String organizationEmail, Long dateTime, Integer maxParticipants) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.dateTime = dateTime;
        this.organizationEmail = organizationEmail;
        this.maxParticipants = maxParticipants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public String getOrganizationEmail() {
        return organizationEmail;
    }

    public void setOrganizationEmail(String organizationEmail) {
        this.organizationEmail = organizationEmail;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
}

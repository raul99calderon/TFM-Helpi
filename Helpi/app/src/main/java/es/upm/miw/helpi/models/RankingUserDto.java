package es.upm.miw.helpi.models;

public class RankingUserDto {
    private String email;
    private Integer numAttendedEvents;

    public RankingUserDto() {
    }

    public RankingUserDto(String email, Integer numAttendedEvents) {
        this.email = email;
        this.numAttendedEvents = numAttendedEvents;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getNumAttendedEvents() {
        return numAttendedEvents;
    }

    public void setNumAttendedEvents(Integer numAttendedEvents) {
        this.numAttendedEvents = numAttendedEvents;
    }
}

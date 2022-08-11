package es.upm.miw.helpi.models;

public class RankingUser extends UserDto {

    private Integer numAttendedEvents;

    public RankingUser() {
        super();
    }

    public RankingUser(String email, String name, String role, Integer numAttendedEvents) {
        super(email, name, role);
        this.numAttendedEvents = numAttendedEvents;
    }

    public RankingUser(RankingUserDto userDto) {
        super(userDto.getEmail(),null,Role.USER.name());
        this.numAttendedEvents = userDto.getNumAttendedEvents();
    }

    public Integer getNumAttendedEvents() {
        return numAttendedEvents;
    }

    public void setNumAttendedEvents(Integer numAttendedEvents) {
        this.numAttendedEvents = numAttendedEvents;
    }
}

package es.upm.miw.helpifororganizations.models;

public class EventJoinRequest {
    private String eventKey;
    private String userEmail;
    private Long dateTime;
    private JoinRequestState state;

    public EventJoinRequest() {
    }

    public EventJoinRequest(String eventKey, String userEmail, Long dateTime, JoinRequestState state) {
        this.eventKey = eventKey;
        this.userEmail = userEmail;
        this.dateTime = dateTime;
        this.state = state;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public JoinRequestState getState() {
        return state;
    }

    public void setState(JoinRequestState state) {
        this.state = state;
    }
}

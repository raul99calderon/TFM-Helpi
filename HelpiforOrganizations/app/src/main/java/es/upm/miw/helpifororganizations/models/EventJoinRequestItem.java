package es.upm.miw.helpifororganizations.models;

public class EventJoinRequestItem extends EventJoinRequest{
    private String key;

    public EventJoinRequestItem() {
        super();
    }

    public EventJoinRequestItem(EventJoinRequest request, String key) {
        super(request.getEventKey(), request.getUserEmail(), request.getDateTime(), request.getState());
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public EventJoinRequest toEventJoinRequest() {
        EventJoinRequest eventJoinRequest = new EventJoinRequest();
        eventJoinRequest.setEventKey(this.getEventKey());
        eventJoinRequest.setDateTime(this.getDateTime());
        eventJoinRequest.setState(this.getState());
        eventJoinRequest.setUserEmail(this.getUserEmail());
        return eventJoinRequest;
    }
}

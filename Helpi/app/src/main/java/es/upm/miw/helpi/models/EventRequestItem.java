package es.upm.miw.helpi.models;

public class EventRequestItem extends EventRequestExtended {

    private String key;

    public EventRequestItem() {
        super();
    }

    public EventRequestItem(EventRequest request, EventItem event, String key) {
        super(request.getEventKey(), request.getUserEmail(), request.getDateTime(), request.getState(), event);
        this.key = key;
    }

    public EventRequestItem(String eventKey, String userEmail, Long dateTime, JoinRequestState state, EventItem event, String key) {
        super(eventKey, userEmail, dateTime, state, event);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

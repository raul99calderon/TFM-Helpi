package es.upm.miw.helpi.models;

public class EventRequestExtended extends EventRequest {

    private EventItem event;

    public EventRequestExtended() {
        super();
    }


    public EventRequestExtended(String eventKey, String userEmail, Long dateTime, JoinRequestState state, EventItem event) {
        super(eventKey, userEmail, dateTime, state);
        this.event = event;
    }

    public EventItem getEvent() {
        return event;
    }

    public void setEvent(EventItem event) {
        this.event = event;
    }
}

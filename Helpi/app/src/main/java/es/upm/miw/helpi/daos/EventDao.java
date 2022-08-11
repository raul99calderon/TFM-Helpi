package es.upm.miw.helpi.daos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import es.upm.miw.helpi.models.Event;
import es.upm.miw.helpi.models.EventDto;
import es.upm.miw.helpi.models.EventItem;
import es.upm.miw.helpi.models.EventRequest;
import es.upm.miw.helpi.models.EventRequestItem;
import es.upm.miw.helpi.models.JoinRequestState;

public final class EventDao {

    private static final String EVENTS = "events";
    private static final String ORGANIZATION_EMAIL = "organizationEmail";

    private static DatabaseReference eventsRef;
    private static EventDao instance;
    private static UserDao userDao;
    private static EventRequestDao eventRequestDao;

    public static EventDao getInstance() {
        if (instance == null) {
            instance = new EventDao();
            eventsRef = FirebaseDatabase.getInstance().getReference(EVENTS);
            userDao = UserDao.getInstance();
            eventRequestDao = EventRequestDao.getInstance();
        }
        return instance;
    }

    public interface OnResultGetAttendedEvents {
        void onChildAttended(EventItem eventItem);
        void onChildRemoved(String key);
    }

    public void getAttendedEvents(OnResultGetAttendedEvents onResultGetAttendedEvents) {
        eventRequestDao.getEventRequests(new EventRequestDao.OnEventRequests() {
            @Override
            public void onChildAdded(EventRequestItem eventRequestItem) {
                if (eventRequestItem.getState().equals(JoinRequestState.ATTENDED))
                    getEvent(eventRequestItem.getEventKey(), onResultGetAttendedEvents::onChildAttended);
            }

            @Override
            public void onChildChanged(EventRequest eventRequest, String key) {
                if (eventRequest.getState().equals(JoinRequestState.ATTENDED))
                    getEvent(eventRequest.getEventKey(), onResultGetAttendedEvents::onChildAttended);
            }

            @Override
            public void onChildRemoved(String key) {
                onResultGetAttendedEvents.onChildRemoved(key);
            }
        });
    }

    public interface OnEventGet {
        void onEventGet(EventItem eventItem);
    }

    public void getEvent(String key, OnEventGet onEventGet) {
        eventsRef.orderByKey()
                .equalTo(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Event event = null;
                        String key = null;
                        for (DataSnapshot k : snapshot.getChildren()) {
                            event = k.getValue(Event.class);
                            key = k.getKey();
                        }

                        Event finalEvent = event;
                        String finalKey = key;

                        userDao.getUser(Objects.requireNonNull(event).getOrganizationEmail(), user -> {
                            EventItem eventItem = new EventItem(
                                    new EventDto(finalEvent, finalKey),
                                    user.getName()
                            );
                            onEventGet.onEventGet(eventItem);
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public interface OnGetEvent {
        void onEventGet(EventItem eventItem);
        void onError();
    }

    public void getEvents(OnGetEvent onGetEvent) {
        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot k: snapshot.getChildren()) {
                    EventDto eventDto = new EventDto(Objects.requireNonNull(k.getValue(Event.class)), k.getKey());

                    userDao.getUser(eventDto.getOrganizationEmail(), user -> {
                        EventItem eventItem = new EventItem(eventDto, user.getName());
                        onGetEvent.onEventGet(eventItem);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onGetEvent.onError();
            }
        });
    }

    public interface OnGetOrganizationEvent {
        void onGetEvent(EventDto eventDto);
        void onRemoveEvent(String key);
    }

    public void getEvents(String organizationEmail, OnGetOrganizationEvent onGetOrganizationEvent) {
        eventsRef.orderByChild(ORGANIZATION_EMAIL)
                .equalTo(organizationEmail)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        EventDto eventDto = new EventDto(Objects.requireNonNull(snapshot.getValue(Event.class)),snapshot.getKey());
                        onGetOrganizationEvent.onGetEvent(eventDto);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        onGetOrganizationEvent.onRemoveEvent(snapshot.getKey());
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}

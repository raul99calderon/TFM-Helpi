package es.upm.miw.helpifororganizations.daos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import es.upm.miw.helpifororganizations.models.Event;
import es.upm.miw.helpifororganizations.models.EventDto;

public final class EventDao {
    private static final String EVENTS = "events";

    private static DatabaseReference eventsRef;
    private static EventDao instance;
    private static EventRequestDao eventRequestDao;
    private static final String ORGANIZATION_EMAIL = "organizationEmail";

    public static EventDao getInstance() {
        if (instance == null) {
            instance = new EventDao();
            eventsRef = FirebaseDatabase.getInstance().getReference(EVENTS);
            eventRequestDao = EventRequestDao.getInstance();
        }
        return instance;
    }

    public interface OnGetOrganizationEvent {
        void onGetEvent(EventDto eventDto);
        void onRemoveEvent(String key);
        void onUpdatedEvent(EventDto eventDto);
    }

    public void getEvents(String email, OnGetOrganizationEvent onGetOrganizationEvent) {
        eventsRef.orderByChild(ORGANIZATION_EMAIL)
                .equalTo(email)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        EventDto eventDto = new EventDto(Objects.requireNonNull(snapshot.getValue(Event.class)),snapshot.getKey());
                        onGetOrganizationEvent.onGetEvent(eventDto);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        EventDto eventDto = new EventDto(Objects.requireNonNull(snapshot.getValue(Event.class)),snapshot.getKey());
                        onGetOrganizationEvent.onUpdatedEvent(eventDto);
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

    public interface OnEventGet {
        void onEventGet(Event event);
    }

    public void getEvent(String key, OnEventGet onEventGet) {
        eventsRef.orderByKey()
                .equalTo(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Event event = null;
                        for (DataSnapshot k : snapshot.getChildren()) {
                            event = k.getValue(Event.class);
                        }
                        onEventGet.onEventGet(event);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        onEventGet.onEventGet(null);
                    }
                });
    }

    public interface OnCreateEvent {
        void onCreatedEvent();
        void onError();
    }

    public void createEvent(Event event, OnCreateEvent onCreateEvent) {
        eventsRef.push()
                .setValue(event)
                .addOnSuccessListener(unused -> onCreateEvent.onCreatedEvent())
                .addOnFailureListener(e -> onCreateEvent.onError());
    }

    public interface OnUpdatedEvent {
        void onUpdatedEvent();
        void onError();
    }

    public void updateEvent(String key, Event event, OnUpdatedEvent onUpdatedEvent) {
        eventsRef.child(key)
                .setValue(event)
                .addOnSuccessListener(unused -> onUpdatedEvent.onUpdatedEvent())
                .addOnFailureListener(e -> onUpdatedEvent.onError());
    }

    public interface OnEventDeleted {
        void onDeleted();
        void onError();
    }

    public void deleteEvent(String key, OnEventDeleted onEventDeleted) {
        eventsRef.child(key)
                .removeValue()
                .addOnSuccessListener(unused -> eventRequestDao.deleteEventRequests(key, onEventDeleted::onDeleted))
                .addOnFailureListener(e -> onEventDeleted.onError());
    }

    public void deleteEvents(String organizationEmail, OnEventDeleted onEventDeleted) {
        eventsRef.orderByChild(ORGANIZATION_EMAIL)
                .equalTo(organizationEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot k : snapshot.getChildren()) {
                            String eventKey = k.getKey();
                            k.getRef().removeValue();
                            eventRequestDao.deleteEventRequests(eventKey, () -> { });
                        }

                        onEventDeleted.onDeleted();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}

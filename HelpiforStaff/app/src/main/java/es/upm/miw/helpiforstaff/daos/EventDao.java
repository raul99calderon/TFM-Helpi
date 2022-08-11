package es.upm.miw.helpiforstaff.daos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import es.upm.miw.helpiforstaff.models.Event;
import es.upm.miw.helpiforstaff.models.EventDto;
import es.upm.miw.helpiforstaff.models.EventItem;

public final class EventDao {

    private static final String EVENTS = "events";
    private static final String ORGANIZATION_EMAIL = "organizationEmail";

    private static DatabaseReference eventsRef;
    private static EventDao instance;
    private static UserDao userDao;

    public static EventDao getInstance() {
        if (instance == null) {
            instance = new EventDao();
            eventsRef = FirebaseDatabase.getInstance().getReference(EVENTS);
            userDao = UserDao.getInstance();
        }
        return instance;
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

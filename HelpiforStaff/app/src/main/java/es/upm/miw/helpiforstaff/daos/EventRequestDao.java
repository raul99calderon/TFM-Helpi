package es.upm.miw.helpiforstaff.daos;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import es.upm.miw.helpiforstaff.models.EventRequest;

public final class EventRequestDao {

    private static final String USER_EMAIL = "userEmail";

    private static EventRequestDao instance;
    private static DatabaseReference requestsRef;
    private static final String EVENT_USER_REQUESTS = "event_user_requests";

    public static EventRequestDao getInstance() {
        if (instance == null) {
            instance = new EventRequestDao();
            requestsRef = FirebaseDatabase.getInstance().getReference(EVENT_USER_REQUESTS);
        }
        return instance;
    }

    public interface OnEventRequest {
        void onEventRequest(EventRequest eventRequest, String requestKey);

        void onNotExists();
    }

    public void getEventRequest(String userEmail, String eventKey, OnEventRequest onEventRequest) {
        requestsRef.orderByChild(USER_EMAIL)
                .equalTo(userEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            boolean exists = false;
                            for (DataSnapshot k : snapshot.getChildren()) {
                                EventRequest actual = k.getValue(EventRequest.class);
                                if (Objects.requireNonNull(actual).getEventKey().equals(eventKey)) {
                                    exists = true;
                                    onEventRequest.onEventRequest(actual, k.getKey());
                                }
                            }
                            if (!exists)
                                onEventRequest.onNotExists();
                        } else onEventRequest.onNotExists();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    public interface OnSetEventRequest {
        void onSetEventRequest();
    }

    public void setEventRequest(EventRequest request, String requestKey, OnSetEventRequest onSetEventRequest) {
        requestsRef.child(requestKey)
                .setValue(request)
                .addOnSuccessListener(unused -> onSetEventRequest.onSetEventRequest());
    }
}

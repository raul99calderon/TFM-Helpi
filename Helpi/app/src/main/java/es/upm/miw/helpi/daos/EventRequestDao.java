package es.upm.miw.helpi.daos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import es.upm.miw.helpi.models.EventRequest;
import es.upm.miw.helpi.models.EventRequestItem;

public final class EventRequestDao {

    private static final String USER_EMAIL = "userEmail";

    private static EventRequestDao instance;
    private static DatabaseReference requestsRef;
    private static final String EVENT_USER_REQUESTS = "event_user_requests";
    private static EventDao eventDao;
    private static UserDao userDao;

    public static EventRequestDao getInstance() {
        if (instance == null) {
            instance = new EventRequestDao();
            eventDao = EventDao.getInstance();
            userDao = UserDao.getInstance();
            requestsRef = FirebaseDatabase.getInstance().getReference(EVENT_USER_REQUESTS);
        }
        return instance;
    }

    public interface OnEventRequests {
        void onChildAdded(EventRequestItem eventRequestItem);
        void onChildChanged(EventRequest eventRequest, String key);
        void onChildRemoved(String key);
    }

    public void getEventRequests(OnEventRequests onEventRequests) {
        requestsRef.orderByChild(USER_EMAIL)
                .equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        EventRequest eventRequest = snapshot.getValue(EventRequest.class);
                        eventDao.getEvent(Objects.requireNonNull(eventRequest).getEventKey(), eventItem -> userDao.getUser(eventItem.getOrganizationEmail(), user -> {
                            EventRequestItem item = new EventRequestItem(
                                    eventRequest,
                                    eventItem,
                                    snapshot.getKey()
                            );
                            onEventRequests.onChildAdded(item);
                        }));
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        EventRequest eventRequest = snapshot.getValue(EventRequest.class);
                        onEventRequests.onChildChanged(eventRequest, snapshot.getKey());
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        onEventRequests.onChildRemoved(snapshot.getKey());
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    public interface OnEventRequest {
        void onEventRequest(EventRequest eventRequest, String requestKey);
        void onNotExists();
    }

    public void getEventRequest(String eventKey, OnEventRequest onEventRequest) {
        requestsRef.orderByChild(USER_EMAIL)
                .equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())
                .addValueEventListener(new ValueEventListener() {
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
                            if (!exists) {
                                onEventRequest.onNotExists();
                            }
                        }
                        else {
                            onEventRequest.onNotExists();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    public interface OnRequestAdded {
        void onRequestAdded();
        void onError();
    }

    public void addRequest(EventRequest eventRequest, OnRequestAdded onRequestAdded) {
        requestsRef.push()
                .setValue(eventRequest)
                .addOnSuccessListener(unused -> onRequestAdded.onRequestAdded())
                .addOnFailureListener(e -> onRequestAdded.onError());
    }

    public interface OnDeleteRequest {
        void onDeletedRequest();
        void onError();
    }

    public void deleteRequest(String requestKey, OnDeleteRequest onDeleteRequest) {
        requestsRef.child(requestKey)
                .removeValue()
                .addOnSuccessListener(unused -> onDeleteRequest.onDeletedRequest())
                .addOnFailureListener(e -> onDeleteRequest.onError());
    }

    public interface OnDeleteUserRequests {
        void onDeleted();
    }

    public void deleteUserRequests(String email, OnDeleteUserRequests onDeleteUserRequests) {
        requestsRef.orderByChild(USER_EMAIL)
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot k : snapshot.getChildren()) {
                                k.getRef().removeValue();
                            }
                        } // a
                        onDeleteUserRequests.onDeleted();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}

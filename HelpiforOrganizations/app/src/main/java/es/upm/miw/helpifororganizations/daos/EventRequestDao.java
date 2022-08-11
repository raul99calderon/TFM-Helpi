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

import es.upm.miw.helpifororganizations.models.EventJoinRequest;
import es.upm.miw.helpifororganizations.models.EventJoinRequestItem;

public final class EventRequestDao {
    private static EventRequestDao instance;
    private static DatabaseReference requestsRef;
    private static final String EVENT_USER_REQUESTS = "event_user_requests";
    private static final String EVENT_KEY = "eventKey";

    public static EventRequestDao getInstance() {
        if (instance == null) {
            instance = new EventRequestDao();
            requestsRef = FirebaseDatabase.getInstance().getReference(EVENT_USER_REQUESTS);
        }
        return instance;
    }

    public interface OnGetEventRequest {
        void onGetEventRequest(EventJoinRequestItem eventJoinRequestItem);
        void onUpdateEventRequest(EventJoinRequestItem eventJoinRequestItem);
        void onRemoveEventRequest(String key);
    }

    public void getEventRequests(String key, OnGetEventRequest onGetEventRequest) {
        requestsRef.orderByChild(EVENT_KEY)
                .equalTo(key)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        EventJoinRequestItem item = new EventJoinRequestItem(
                                Objects.requireNonNull(snapshot.getValue(EventJoinRequest.class)),
                                snapshot.getKey()
                        );
                        onGetEventRequest.onGetEventRequest(item);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        EventJoinRequestItem item = new EventJoinRequestItem(
                                Objects.requireNonNull(snapshot.getValue(EventJoinRequest.class)),
                                snapshot.getKey()
                        );
                        onGetEventRequest.onUpdateEventRequest(item);
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        onGetEventRequest.onRemoveEventRequest(snapshot.getKey());
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public interface OnSetRequestResponse {
        void onSetRequestResponse();
        void onError();
    }

    public void setRequestResponse(EventJoinRequestItem eventJoinRequestItem, OnSetRequestResponse onSetRequestResponse) {
        requestsRef.child(eventJoinRequestItem.getKey())
                .setValue(eventJoinRequestItem.toEventJoinRequest())
                .addOnSuccessListener(unused -> onSetRequestResponse.onSetRequestResponse())
                .addOnFailureListener(e -> onSetRequestResponse.onError());
    }

    public interface OnDeleteEventRequests {
        void onDeleted();
    }

    public void deleteEventRequests(String eventKey, OnDeleteEventRequests onDeleteEventRequests) {
        requestsRef.orderByChild(EVENT_KEY)
                .equalTo(eventKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot k : snapshot.getChildren()) {
                                k.getRef().removeValue();
                            }
                        }
                        onDeleteEventRequests.onDeleted();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}

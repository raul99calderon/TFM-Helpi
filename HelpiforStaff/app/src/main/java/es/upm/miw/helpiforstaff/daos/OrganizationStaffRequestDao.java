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

import es.upm.miw.helpiforstaff.models.OrganizationStaffRequestDto;

public final class OrganizationStaffRequestDao {

    private static final String ORGANIZATION_STAFF_REQUESTS = "organization_staff_requests";
    private static final String STAFF_EMAIL = "staffEmail";
    private static final String CONFIRMED = "confirmed";

    private static DatabaseReference orgStaffReqRef;

    private static OrganizationStaffRequestDao instance;

    public static OrganizationStaffRequestDao getInstance() {
        if (instance == null) {
            instance = new OrganizationStaffRequestDao();
            orgStaffReqRef = FirebaseDatabase.getInstance().getReference(ORGANIZATION_STAFF_REQUESTS);
        }

        return instance;
    }

    public interface OnGetOrganizationStaffRequests {
        void onGetOrganizationStaffRequest(OrganizationStaffRequestDto organizationStaffRequestDto);
        void onUpdateOrganizationStaffRequest(OrganizationStaffRequestDto organizationStaffRequestDto);
        void onRemoveOrganizationStaffRequest(String organizationEmail);
    }

    public void getOrganizationStaffRequests(String email, OnGetOrganizationStaffRequests onGetOrganizationStaffRequests) {
        orgStaffReqRef.orderByChild(STAFF_EMAIL)
                .equalTo(email)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        OrganizationStaffRequestDto requestDto = snapshot.getValue(OrganizationStaffRequestDto.class);
                        Objects.requireNonNull(requestDto).setKey(snapshot.getKey());
                        onGetOrganizationStaffRequests.onGetOrganizationStaffRequest(requestDto);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        OrganizationStaffRequestDto requestDto = snapshot.getValue(OrganizationStaffRequestDto.class);
                        onGetOrganizationStaffRequests.onUpdateOrganizationStaffRequest(requestDto);
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        OrganizationStaffRequestDto requestDto = snapshot.getValue(OrganizationStaffRequestDto.class);
                        onGetOrganizationStaffRequests.onRemoveOrganizationStaffRequest(Objects.requireNonNull(requestDto).getOrganizationEmail());
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public interface OnSetOrganizationStaffRequest {
        void onSetOrganizationStaffRequest();
    }

    public void updateOrganizationStaffRequest(String requestKey, OnSetOrganizationStaffRequest organizationStaffRequest) {
        orgStaffReqRef.child(requestKey)
                .child(CONFIRMED)
                .setValue(true)
                .addOnSuccessListener(unused -> organizationStaffRequest.onSetOrganizationStaffRequest());
    }

    public interface OnDeleteOrganizationStaffRequests {
        void onDeleted();
    }

    public void deleteOrganizationStaffRequests(String staffEmail, OnDeleteOrganizationStaffRequests onDeleteOrganizationStaffRequests) {
        orgStaffReqRef.orderByChild(STAFF_EMAIL)
                .equalTo(staffEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot k : snapshot.getChildren()) {
                                k.getRef().removeValue();
                            }
                        }
                        onDeleteOrganizationStaffRequests.onDeleted();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}

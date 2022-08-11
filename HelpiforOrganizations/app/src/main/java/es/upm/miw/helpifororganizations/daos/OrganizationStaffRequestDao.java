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

import es.upm.miw.helpifororganizations.models.OrganizationStaff;
import es.upm.miw.helpifororganizations.models.OrganizationStaffRequest;

public final class OrganizationStaffRequestDao {
    private static final String ORGANIZATION_STAFF_REQUESTS = "organization_staff_requests";
    private static final String ORGANIZATION_EMAIL = "organizationEmail";
    private static DatabaseReference orgStaffReqRef;

    private static OrganizationStaffRequestDao instance;

    public static OrganizationStaffRequestDao getInstance() {
        if (instance == null) {
            instance = new OrganizationStaffRequestDao();
            orgStaffReqRef = FirebaseDatabase.getInstance()
                    .getReference(ORGANIZATION_STAFF_REQUESTS);
        }

        return instance;
    }

    public interface OnGetOrganizationStaffRequest {
        void onGetOrganizationStaffRequest(OrganizationStaffRequest organizationStaffRequest);
        void onUpdateOrganizationStaffRequest(OrganizationStaffRequest organizationStaffRequest);
        void onRemoveOrganizationStaffRequest(String staffEmail);
    }

    public void getOrganizationStaffRequests(String email, OnGetOrganizationStaffRequest onGetOrganizationStaffRequest) {
        orgStaffReqRef.orderByChild(ORGANIZATION_EMAIL)
                .equalTo(email)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        OrganizationStaffRequest organizationStaffRequest = snapshot.getValue(OrganizationStaffRequest.class);
                        onGetOrganizationStaffRequest.onGetOrganizationStaffRequest(organizationStaffRequest);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        OrganizationStaffRequest organizationStaffRequest = snapshot.getValue(OrganizationStaffRequest.class);
                        onGetOrganizationStaffRequest.onUpdateOrganizationStaffRequest(organizationStaffRequest);
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        OrganizationStaffRequest organizationStaffRequest = snapshot.getValue(OrganizationStaffRequest.class);
                        onGetOrganizationStaffRequest.onRemoveOrganizationStaffRequest(Objects.requireNonNull(organizationStaffRequest).getStaffEmail());
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public interface OnCheckIfOrganizationStaffRequestExists {
        void onExists();
        void onNotExists();
    }

    public void checkOrganizationStaffRequest(OrganizationStaff organizationStaff, OnCheckIfOrganizationStaffRequestExists onCheckIfOrganizationStaffRequestExists) {
        orgStaffReqRef.orderByChild(ORGANIZATION_EMAIL)
                .equalTo(organizationStaff.getOrganizationEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            boolean exists = false;
                            for (DataSnapshot k : snapshot.getChildren()) {
                                OrganizationStaffRequest osr = k.getValue(OrganizationStaffRequest.class);
                                if (Objects.requireNonNull(osr).getStaffEmail().equals(organizationStaff.getStaffEmail())) {
                                    exists = true;
                                    break;
                                }
                            }
                            if (exists)
                                onCheckIfOrganizationStaffRequestExists.onExists();
                            else onCheckIfOrganizationStaffRequestExists.onNotExists();
                        }
                        else onCheckIfOrganizationStaffRequestExists.onNotExists();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public interface OnCreateOrganizationStaffRequest {
        void onCreated();
        void onError();
    }

    public void createRequest(OrganizationStaffRequest organizationStaffRequest, OnCreateOrganizationStaffRequest onCreateOrganizationStaffRequest) {
        orgStaffReqRef.push()
                .setValue(organizationStaffRequest)
                .addOnSuccessListener(unused -> onCreateOrganizationStaffRequest.onCreated())
                .addOnFailureListener(e -> onCreateOrganizationStaffRequest.onError());
    }

    public interface OnDeleteRequest {
        void onDeletedRequest();
    }

    public void deleteRequest(OrganizationStaff organizationStaff, OnDeleteRequest onDeleteRequest) {
        orgStaffReqRef.orderByChild(ORGANIZATION_EMAIL)
                .equalTo(organizationStaff.getOrganizationEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot k : snapshot.getChildren()) {
                            OrganizationStaffRequest actual = k.getValue(OrganizationStaffRequest.class);
                            if (Objects.requireNonNull(actual).getStaffEmail().equals(organizationStaff.getStaffEmail())) {
                                k.getRef().removeValue().addOnSuccessListener(unused -> onDeleteRequest.onDeletedRequest());
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public interface OnDeleteRequests {
        void onDeletedRequests();
    }

    public void deleteRequests(String organizationEmail, OnDeleteRequests onDeleteRequests) {
        orgStaffReqRef.orderByChild(ORGANIZATION_EMAIL)
                .equalTo(organizationEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot k : snapshot.getChildren()) {
                            k.getRef().removeValue();
                        }

                        onDeleteRequests.onDeletedRequests();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}

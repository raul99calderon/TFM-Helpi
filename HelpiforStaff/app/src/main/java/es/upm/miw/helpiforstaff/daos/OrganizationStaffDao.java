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

import es.upm.miw.helpiforstaff.models.OrganizationStaff;

public final class OrganizationStaffDao {

    private static final String ORGANIZATION_STAFF = "organization_staff";
    private static final String STAFF_EMAIL = "staffEmail";

    private static DatabaseReference organizationStaffRef;
    private static OrganizationStaffDao instance;

    public static OrganizationStaffDao getInstance() {
        if (instance == null) {
            instance = new OrganizationStaffDao();
            organizationStaffRef = FirebaseDatabase.getInstance().getReference(ORGANIZATION_STAFF);
        }

        return instance;
    }

    public interface OnGetOrganizationStaff {
        void onGetOrganizationStaff(OrganizationStaff organizationStaff);
        void onRemoveOrganizationStaff(String organizationEmail);
    }

    public void getOrganizationStaff(String staffEmail, OnGetOrganizationStaff onGetOrganizationStaff) {
        organizationStaffRef.orderByChild(STAFF_EMAIL)
                .equalTo(staffEmail)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        OrganizationStaff organizationStaff = snapshot.getValue(OrganizationStaff.class);
                        onGetOrganizationStaff.onGetOrganizationStaff(organizationStaff);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        OrganizationStaff organizationStaff = snapshot.getValue(OrganizationStaff.class);
                        onGetOrganizationStaff.onRemoveOrganizationStaff(Objects.requireNonNull(organizationStaff).getOrganizationEmail());
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public interface OnAddToOrganization {
        void onAdded();
    }

    public void addToOrganization(OrganizationStaff organizationStaff, OnAddToOrganization onAddToOrganization) {
        organizationStaffRef.push()
                .setValue(organizationStaff)
                .addOnSuccessListener(unused -> onAddToOrganization.onAdded());
    }

    public interface OnDeleteOrganizationStaff {
        void onDeleted();
    }

    public void deleteOrganizationStaff(String staffEmail, OnDeleteOrganizationStaff onDeleteOrganizationStaff) {
        organizationStaffRef.orderByChild(STAFF_EMAIL)
                .equalTo(staffEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot k : snapshot.getChildren()) {
                                k.getRef().removeValue();
                            }
                        }
                        onDeleteOrganizationStaff.onDeleted();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}

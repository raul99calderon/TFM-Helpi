package es.upm.miw.helpifororganizations.daos;

import android.graphics.Bitmap;

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

public final class OrganizationStaffDao {
    private static final String ORGANIZATION_STAFF = "organization_staff";
    private static final String ORGANIZATION_EMAIL = "organizationEmail";
    private static DatabaseReference organizationStaffRef;
    private static OrganizationStaffDao instance;
    private static ImageDao imageDao;
    private static OrganizationStaffRequestDao organizationStaffRequestDao;

    public static OrganizationStaffDao getInstance() {
        if (instance == null) {
            instance = new OrganizationStaffDao();
            organizationStaffRef = FirebaseDatabase.getInstance().getReference(ORGANIZATION_STAFF);
            imageDao = ImageDao.getInstance();
            organizationStaffRequestDao = OrganizationStaffRequestDao.getInstance();
        }

        return instance;
    }

    public interface OnGetOrganizationStaff {
        void onGetOrganizationStaff(OrganizationStaff organizationStaff);
        void onRemoveOrganizationStaff(String staffEmail);
        void onGetStaffPhoto(String staffEmail, Bitmap bitmap);
    }

    public void getOrganizationStaff(String organizationEmail, OnGetOrganizationStaff onGetOrganizationStaff) {
        organizationStaffRef.orderByChild(ORGANIZATION_EMAIL)
                .equalTo(organizationEmail)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        OrganizationStaff organizationStaff = snapshot.getValue(OrganizationStaff.class);
                        onGetOrganizationStaff.onGetOrganizationStaff(organizationStaff);
                        imageDao.getProfilePhoto(Objects.requireNonNull(organizationStaff).getStaffEmail(), image ->
                                onGetOrganizationStaff.onGetStaffPhoto(organizationStaff.getStaffEmail(), image));
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        OrganizationStaff organizationStaff = snapshot.getValue(OrganizationStaff.class);
                        onGetOrganizationStaff.onRemoveOrganizationStaff(Objects.requireNonNull(organizationStaff).getStaffEmail());
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

    public void checkIfOrganizationStaffExists(OrganizationStaff organizationStaff, OnCheckIfOrganizationStaffRequestExists onCheckIfOrganizationStaffRequestExists) {
        organizationStaffRef.orderByChild(ORGANIZATION_EMAIL)
                .equalTo(organizationStaff.getOrganizationEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            boolean exists = false;
                            for (DataSnapshot k : snapshot.getChildren()) {
                                OrganizationStaff os = k.getValue(OrganizationStaff.class);
                                if (Objects.requireNonNull(os).getStaffEmail().equals(organizationStaff.getStaffEmail())) {
                                    exists = true;
                                    break;
                                }
                            }

                            if (exists) onCheckIfOrganizationStaffRequestExists.onExists();
                            else onCheckIfOrganizationStaffRequestExists.onNotExists();
                        }
                        else onCheckIfOrganizationStaffRequestExists.onNotExists();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public interface OnDeleteStaff {
        void onDeleteStaff();
    }

    public void deleteStaff(OrganizationStaff organizationStaff, OnDeleteStaff onDeleteStaff) {
        organizationStaffRef.orderByChild(ORGANIZATION_EMAIL)
                .equalTo(organizationStaff.getOrganizationEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot k : snapshot.getChildren()) {
                            OrganizationStaff organizationStaff = k.getValue(OrganizationStaff.class);
                            if (Objects.requireNonNull(organizationStaff).getStaffEmail().equals(organizationStaff.getStaffEmail())) {
                                k.getRef().removeValue().addOnSuccessListener(unused ->
                                        organizationStaffRequestDao.deleteRequest(organizationStaff, onDeleteStaff::onDeleteStaff));
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public interface OnDeleteOrganizationStaffs {
        void onDeleted();
    }

    public void deleteOrganizationStaff(String organizationEmail, OnDeleteOrganizationStaffs onDeleteOrganizationStaffs) {
        organizationStaffRef.orderByChild(ORGANIZATION_EMAIL)
                .equalTo(organizationEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot k : snapshot.getChildren()) {
                            k.getRef().removeValue();
                        }
                        onDeleteOrganizationStaffs.onDeleted();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}

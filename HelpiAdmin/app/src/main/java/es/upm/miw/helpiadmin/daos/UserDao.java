package es.upm.miw.helpiadmin.daos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import es.upm.miw.helpiadmin.models.ConfirmationUserDto;
import es.upm.miw.helpiadmin.models.Role;
import es.upm.miw.helpiadmin.models.User;
import es.upm.miw.helpiadmin.models.UserDto;

public class UserDao {

    private static DatabaseReference usersRef;
    private static FirebaseAuth mAuth;

    private static final String USERS = "users";
    private static final String EMAIL = "email";
    private static final String ROLE = "role";
    private static final String CONFIRMED = "confirmed";

    private static UserDao instance;

    public static UserDao getInstance() {
        if (instance == null) {
            instance = new UserDao();
            usersRef = FirebaseDatabase.getInstance().getReference(USERS);
            mAuth = FirebaseAuth.getInstance();
        }
        return instance;
    }

    public interface OnResultGetUser {
        void resultGetUser(User user);
    }

    public void getUser(String email, OnResultGetUser onResultGetUser) {
        usersRef.orderByChild(EMAIL)
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserDto userDto = null;
                        for (DataSnapshot k : snapshot.getChildren())
                            userDto = k.getValue(UserDto.class);
                        User user = new User();
                        if (userDto != null) {
                            user.fromUserDto(userDto);
                            onResultGetUser.resultGetUser(user);
                        }
                        else {
                            onResultGetUser.resultGetUser(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    public interface OnLogin {
        void logged();
        void errorLogin();
    }

    public void login(String email, String password, OnLogin onLogin) {
        getUser(email, user -> {
            if (user == null)
                onLogin.errorLogin();
            else if (!user.getRole().equals(Role.ADMINISTRATOR.name()))
                onLogin.errorLogin();
            else mAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> onLogin.logged())
                        .addOnFailureListener(e -> onLogin.errorLogin());
        });
    }

    public interface OnGetOrganization {
        void onGetOrganization(ConfirmationUserDto confirmationUserDto);
        void onChangedOrganization(ConfirmationUserDto confirmationUserDto);
        void onRemovedOrganization(String organizationEmail);
    }

    public void getOrganizations(OnGetOrganization onGetOrganization) {
        usersRef.orderByChild(ROLE)
                .equalTo(Role.ORGANIZATION.name())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        ConfirmationUserDto orgDto = snapshot.getValue(ConfirmationUserDto.class);
                        onGetOrganization.onGetOrganization(orgDto);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        ConfirmationUserDto orgDto = snapshot.getValue(ConfirmationUserDto.class);
                        onGetOrganization.onChangedOrganization(orgDto);
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        ConfirmationUserDto orgDto = snapshot.getValue(ConfirmationUserDto.class);
                        onGetOrganization.onRemovedOrganization(Objects.requireNonNull(orgDto).getEmail());
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public interface OnUpdateOrganization {
        void onUpdatedOrganization();
        void onError();
    }

    public void confirmOrganization(String email, OnUpdateOrganization onUpdateOrganization) {
        usersRef.orderByChild(EMAIL)
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            DataSnapshot user = null;
                            for (DataSnapshot k : snapshot.getChildren())
                                user = k;
                            Objects.requireNonNull(user)
                                    .getRef()
                                    .child(CONFIRMED)
                                    .setValue(true)
                                    .addOnSuccessListener(unused -> onUpdateOrganization.onUpdatedOrganization())
                                    .addOnFailureListener(e -> onUpdateOrganization.onError());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        onUpdateOrganization.onError();
                    }
                });
    }
}

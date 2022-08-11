package es.upm.miw.helpifororganizations.daos;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import es.upm.miw.helpifororganizations.models.ConfirmationUserDto;
import es.upm.miw.helpifororganizations.models.Role;
import es.upm.miw.helpifororganizations.models.User;
import es.upm.miw.helpifororganizations.models.UserDto;

public final class UserDao {

    private static DatabaseReference usersRef;
    private static FirebaseAuth mAuth;
    private static final String USERS = "users";
    private static final String EMAIL = "email";
    private static final String NAME = "name";

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
                        if (snapshot.exists()) {
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
                        else {
                            onResultGetUser.resultGetUser(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    public interface OnSavedProfile {
        void savedProfile();
        void errorSaving();
    }

    public void saveProfile(User user, String uid, OnSavedProfile onSavedProfile) {
        usersRef.child(uid)
                .child(NAME)
                .setValue(user.getName())
                .addOnSuccessListener(unused -> onSavedProfile.savedProfile())
                .addOnFailureListener(e -> onSavedProfile.errorSaving());
    }

    public interface OnGetOrganizationName {
        void onGetOrganizationName(String name);
    }

    public void getOrganizationName(OnGetOrganizationName onGetOrganizationName) {
        getUser(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail(), user -> {
            if (user != null) {
                onGetOrganizationName.onGetOrganizationName(user.getName());
            }
        });
    }

    public interface OnGetConfirmationUser {
        void onGetConfirmationUser(ConfirmationUserDto confirmationUserDto);
    }

    public void getConfirmationUser(String email, OnGetConfirmationUser onGetConfirmationUser) {
        usersRef.orderByChild(EMAIL)
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ConfirmationUserDto userDto = null;
                        for (DataSnapshot k : snapshot.getChildren())
                            userDto = k.getValue(ConfirmationUserDto.class);
                        User user = new User();
                        if (userDto != null) {
                            user.fromUserDto(userDto);
                            onGetConfirmationUser.onGetConfirmationUser(userDto);
                        }
                        else {
                            onGetConfirmationUser.onGetConfirmationUser(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
    }

    public interface OnLogin {
        void logged();
        void errorLogin();
        void onNotConfirmed();
    }

    public void loginWithEmailAndPassword(String email, String password, OnLogin onLogin) {
        getConfirmationUser(email, user -> {
            if (user == null)
                onLogin.errorLogin();
            else {
                if (!user.getRole().equals(Role.ORGANIZATION.name())) {
                    onLogin.errorLogin();
                }
                else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener(authResult -> {
                                if (!user.isConfirmed()) {
                                    onLogin.onNotConfirmed();
                                }
                                else {
                                    onLogin.logged();
                                }
                            })
                            .addOnFailureListener(e -> onLogin.errorLogin());
                }
            }
        });
    }

    public interface OnCheckUser {
        void onSuccess();
        void onError();
        void onNotConfirmed();
    }

    public void checkUser(String email, OnCheckUser onCheckUser) {
        getConfirmationUser(email, user -> {
            if (user == null) {
                onCheckUser.onError();
            }
            else {
                if (!user.getRole().equals(Role.ORGANIZATION.name())) {
                    onCheckUser.onError();
                }
                else {
                    if (!user.isConfirmed()) {
                        onCheckUser.onNotConfirmed();
                    }
                    else {
                        onCheckUser.onSuccess();
                    }
                }
            }
        });
    }

    public interface OnRegister {
        void registered();
        void errorRegister();
    }

    public void registerWithEmailAndPassword(User user, String password, OnRegister onRegister) {
        getUser(user.getEmail(), user1 -> {
            if (user1 == null) {
                mAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                        .addOnSuccessListener(authResult -> register(user, onRegister))
                        .addOnFailureListener(e -> onRegister.errorRegister());
            }
            else {
                onRegister.errorRegister();
            }
        });

    }

    public void register(User user, OnRegister onRegister) {
        getUser(user.getEmail(), user1 -> {
            if (user1 == null) {
                ConfirmationUserDto confirmationUserDto = new ConfirmationUserDto(user.getEmail(), user.getName(), Role.ORGANIZATION.name(), false);
                usersRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                        .setValue(confirmationUserDto)
                        .addOnSuccessListener(unused -> onRegister.registered())
                        .addOnFailureListener(e -> onRegister.errorRegister());
            }
            else {
                onRegister.errorRegister();
            }
        });
    }

    public interface OnDeleteAccount {
        void onDeleted();
        void onError(String error);
    }

    public void deleteAccount(OnDeleteAccount onDeleteAccount) {
        Objects.requireNonNull(mAuth.getCurrentUser())
                .delete()
                .addOnSuccessListener(unused -> onDeleteAccount.onDeleted())
                .addOnFailureListener(e -> onDeleteAccount.onError(e.toString()));
    }

    public void deleteUser(String email, OnDeleteAccount onDeleteAccount) {
        usersRef.orderByChild(EMAIL)
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot k : snapshot.getChildren()) {
                            k.getRef().removeValue();
                        }
                        onDeleteAccount.onDeleted();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        onDeleteAccount.onError(error.toString());
                    }
                });
    }
}

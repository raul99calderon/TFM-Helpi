package es.upm.miw.helpiforstaff.daos;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import es.upm.miw.helpiforstaff.models.Role;
import es.upm.miw.helpiforstaff.models.User;
import es.upm.miw.helpiforstaff.models.UserDto;

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

    public interface OnLogin {
        void logged();
        void errorLogin();
    }

    public void loginWithEmailAndPassword(String email, String password, OnLogin onLogin) {
        checkUser(email, new OnCheckUser() {
            @Override
            public void onSuccess() {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> onLogin.logged())
                        .addOnFailureListener(e -> onLogin.errorLogin());
            }

            @Override
            public void onError() {
                onLogin.errorLogin();
            }
        });
    }

    public interface OnCheckUser {
        void onSuccess();
        void onError();
    }

    public void checkUser(String email, OnCheckUser onCheckUser) {
        getUser(email, user -> {
            if (user == null) {
                onCheckUser.onError();
            }
            else {
                if (!user.getRole().equals(Role.STAFF.name())) {
                    onCheckUser.onError();
                }
                else {
                    onCheckUser.onSuccess();
                }
            }
        });
    }

    public interface OnRegister {
        void registered();
        void errorRegister();
    }

    public void registerWithEmail(User user, String password, OnRegister onRegister) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnSuccessListener(authResult -> register(user, onRegister))
                .addOnFailureListener(e -> onRegister.errorRegister());
    }

    public void register(User user, OnRegister onRegister) {
        getUser(user.getEmail(), user1 -> {
            if (user1 == null) {
                usersRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                        .setValue(user)
                        .addOnSuccessListener(unused -> onRegister.registered())
                        .addOnFailureListener(e -> onRegister.errorRegister());
            }
            else onRegister.errorRegister();
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

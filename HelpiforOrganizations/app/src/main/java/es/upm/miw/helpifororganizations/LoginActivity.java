package es.upm.miw.helpifororganizations;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import es.upm.miw.helpifororganizations.daos.UserDao;
import es.upm.miw.helpifororganizations.models.Role;
import es.upm.miw.helpifororganizations.models.User;

public class LoginActivity extends AppCompatActivity {

    private SignInClient oneTapClient;
    private FirebaseAuth mAuth;
    private Button btnLoginGoogle, btnLoginEmail;
    private ProgressBar progressBar;
    private static final int REQ_ONE_TAP = 2;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();

        progressBar = findViewById(R.id.progressBar);
        btnLoginGoogle = findViewById(R.id.btn_login_google);
        btnLoginGoogle.setOnClickListener(view -> launchGoogleSignInActivity());
        btnLoginEmail = findViewById(R.id.btn_open_email_login);
        btnLoginEmail.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, EmailLoginActivity.class)));

        userDao = UserDao.getInstance();
        mAuth = FirebaseAuth.getInstance();
        oneTapClient = Identity.getSignInClient(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            showProgressBar();
            this.login();
        } else showOptions();
    }

    private void login() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            userDao.getConfirmationUser(firebaseUser.getEmail(), confirmationUserDto -> {
                if (confirmationUserDto.getRole().equals(Role.ORGANIZATION.name())) {
                    if (confirmationUserDto.isConfirmed()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.wait_to_confirmation, Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                        showOptions();
                    }
                } else loginFailed();
            });
        } else loginFailed();
    }

    private void launchGoogleSignInActivity() {
        BeginSignInRequest signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        startIntentSenderForResult(
                                result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        loginFailed();
                    }
                })
                .addOnFailureListener(this, e -> loginFailed());
    }

    private void createGoogleUser() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        User user = new User(Objects.requireNonNull(firebaseUser).getEmail(), firebaseUser.getDisplayName());
        userDao.register(user, new UserDao.OnRegister() {
            @Override
            public void registered() {
                mAuth.signOut();
                Toast.makeText(LoginActivity.this, R.string.wait_to_confirmation, Toast.LENGTH_LONG).show();
            }

            @Override
            public void errorRegister() {
                loginFailed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    mAuth.signInWithCredential(firebaseCredential)
                            .addOnSuccessListener(authResult -> userDao.getUser(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail(),
                                    user -> {
                                        if (user != null)
                                            loginWithGoogle();
                                        else createGoogleUser();
                                    })
                            ).addOnFailureListener(e -> loginFailed());
                }
            } catch (ApiException e) {
                loginFailed();
            }
        }
    }

    private void loginWithGoogle() {
        String email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        userDao.checkUser(email, new UserDao.OnCheckUser() {
            @Override
            public void onSuccess() {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }

            @Override
            public void onError() {
                loginFailed();
            }

            @Override
            public void onNotConfirmed() {
                mAuth.signOut();
                Toast.makeText(LoginActivity.this, R.string.wait_to_confirmation, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loginFailed() {
        this.showOptions();
        this.mAuth.signOut();
        Toast.makeText(LoginActivity.this, getString(R.string.login_error),
                Toast.LENGTH_SHORT).show();
    }

    private void showOptions() {
        progressBar.setVisibility(View.INVISIBLE);
        btnLoginEmail.setVisibility(View.VISIBLE);
        btnLoginGoogle.setVisibility(View.VISIBLE);
    }

    private void showProgressBar() {
        btnLoginGoogle.setVisibility(View.INVISIBLE);
        btnLoginEmail.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }
}
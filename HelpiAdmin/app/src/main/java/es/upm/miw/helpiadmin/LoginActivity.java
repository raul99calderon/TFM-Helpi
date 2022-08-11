package es.upm.miw.helpiadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Button btnLoginEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();

        btnLoginEmail = findViewById(R.id.btn_open_email_login);
        btnLoginEmail.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, EmailLoginActivity.class)));
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
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
        if (firebaseUser != null)
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        else
            loginFailed();
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
    }

    private void showProgressBar() {
        btnLoginEmail.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }
}
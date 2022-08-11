package es.upm.miw.helpiforstaff;

import static es.upm.miw.helpiforstaff.utils.Validators.validateEmail;
import static es.upm.miw.helpiforstaff.utils.Validators.validatePassword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import es.upm.miw.helpiforstaff.daos.UserDao;

public class EmailLoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private TextInputLayout emailContainer, passwordContainer;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnSignUp = findViewById(R.id.btn_open_signup);
        emailContainer = findViewById(R.id.emailContainer);
        passwordContainer = findViewById(R.id.passwordContainer);

        userDao = UserDao.getInstance();

        btnLogin.setOnClickListener(view -> login());
        btnSignUp.setOnClickListener(view ->
                startActivity(new Intent(EmailLoginActivity.this, RegisterActivity.class)));

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                emailContainer.setHelperText(validateEmail(editable.toString()));
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                passwordContainer.setHelperText(validatePassword(editable.toString()));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        etEmail.setText(null);
        etPassword.setText(null);
    }

    private void login() {
        String email, password;
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        if (areValidEmailAndPassword(email, password)) {
            userDao.loginWithEmailAndPassword(email, password, new UserDao.OnLogin() {
                @Override
                public void logged() {
                    startActivity(new Intent(EmailLoginActivity.this, MainActivity.class));
                }

                @Override
                public void errorLogin() {
                    Toast.makeText(EmailLoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean areValidEmailAndPassword(String email, String password) {
        String validEmail = validateEmail(email);
        String validPassword = validatePassword(password);

        if (validEmail != null)
            this.emailContainer.setHelperText(validEmail);
        if (validPassword != null)
            this.passwordContainer.setHelperText(validPassword);

        return validEmail == null && validPassword == null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
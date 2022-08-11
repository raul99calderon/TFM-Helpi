package es.upm.miw.helpiforstaff;

import static es.upm.miw.helpiforstaff.utils.Validators.validateEmail;
import static es.upm.miw.helpiforstaff.utils.Validators.validateName;
import static es.upm.miw.helpiforstaff.utils.Validators.validatePassword;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import es.upm.miw.helpiforstaff.daos.UserDao;
import es.upm.miw.helpiforstaff.models.User;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etEmail, etName, etPassword;
    private TextInputLayout emailContainer, nameContainer, passwordContainer;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.etName);

        emailContainer = findViewById(R.id.emailContainer);
        passwordContainer = findViewById(R.id.passwordContainer);
        nameContainer = findViewById(R.id.nameContainer);

        mAuth = FirebaseAuth.getInstance();

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
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                nameContainer.setHelperText(validateName(editable.toString()));
            }
        });

        userDao = UserDao.getInstance();

        Button btnSignUp = findViewById(R.id.btn_signup);
        btnSignUp.setOnClickListener(view -> signup());
    }

    @Override
    protected void onResume() {
        super.onResume();
        etEmail.setText(null);
        etName.setText(null);
        etPassword.setText(null);
    }

    private void signup() {
        String email, name, password;
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        name = etName.getText().toString();

        if (areValidEmailNameAndPassword(email, name, password)) {
            User user = new User(email, name);
            userDao.registerWithEmail(user, password, new UserDao.OnRegister() {
                @Override
                public void registered() {
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                }

                @Override
                public void errorRegister() {
                    signUpFailed();
                }
            });
        }
    }

    private boolean areValidEmailNameAndPassword(String email, String name, String password) {
        String validEmail = validateEmail(email);
        String validPassword = validatePassword(password);
        String validName = validateName(name);

        if (validEmail != null)
            this.emailContainer.setHelperText(validEmail);
        if (validPassword != null)
            this.passwordContainer.setHelperText(validPassword);
        if (validName != null)
            this.nameContainer.setHelperText(validName);

        return validEmail == null && validPassword == null && validName == null;
    }

    private void signUpFailed() {
        mAuth.signOut();
        Toast.makeText(RegisterActivity.this, getString(R.string.register_error),
                Toast.LENGTH_SHORT).show();
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
package es.upm.miw.helpifororganizations;

import static es.upm.miw.helpifororganizations.utils.Validators.validateEmail;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import es.upm.miw.helpifororganizations.daos.OrganizationStaffDao;
import es.upm.miw.helpifororganizations.daos.OrganizationStaffRequestDao;
import es.upm.miw.helpifororganizations.daos.UserDao;
import es.upm.miw.helpifororganizations.models.OrganizationStaff;
import es.upm.miw.helpifororganizations.models.OrganizationStaffRequest;
import es.upm.miw.helpifororganizations.models.Role;

public class AddOrganizationStaffRequestActivity extends AppCompatActivity {

    private EditText etEmail;
    private TextInputLayout emailContainer;
    private UserDao userDao;
    private OrganizationStaffDao organizationStaffDao;
    private OrganizationStaffRequestDao organizationStaffRequestDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);

        etEmail = findViewById(R.id.etEmail);
        emailContainer = findViewById(R.id.emailContainer);

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                emailContainer.setHelperText(validateEmail(editable.toString()));
            }
        });

        userDao = UserDao.getInstance();
        organizationStaffDao = OrganizationStaffDao.getInstance();
        organizationStaffRequestDao = OrganizationStaffRequestDao.getInstance();

        Button sendRequestBtn = findViewById(R.id.btn_send_request);
        sendRequestBtn.setOnClickListener(view -> sendRequestToStaff());
    }

    private void showText(int resId) {
        Toast.makeText(AddOrganizationStaffRequestActivity.this,resId,Toast.LENGTH_SHORT).show();
    }

    private void sendRequestToStaff() {
        String staffEmail = etEmail.getText().toString();
        if (validateEmail(staffEmail) == null) {
            checkIfStaffExists(staffEmail);
        }
        else {
            showText(R.string.not_valid);
        }
    }

    private void checkIfStaffExists(String staffEmail) {
        userDao.getUser(staffEmail, user -> {
            if (user == null)
                showText(R.string.user_not_exists);
            else if (!user.getRole().equals(Role.STAFF.name()))
                showText(R.string.staff_not_exists);
            else
                checkIfStaffAlreadyBelongsToOrganization(staffEmail);
        });
    }

    private void checkIfStaffAlreadyBelongsToOrganization(String staffEmail) {
        String organizationEmail = Objects.requireNonNull(FirebaseAuth
                .getInstance()
                .getCurrentUser())
                .getEmail();
        OrganizationStaff organizationStaff = new OrganizationStaff(organizationEmail, staffEmail);
        organizationStaffDao.checkIfOrganizationStaffExists(organizationStaff, new OrganizationStaffDao.OnCheckIfOrganizationStaffRequestExists() {
            @Override
            public void onExists() {
                showText(R.string.staff_already_in_organization);
            }

            @Override
            public void onNotExists() {
                checkIfRequestAlreadyExists(staffEmail);
            }
        });
    }

    private void checkIfRequestAlreadyExists(String staffEmail) {
        String organizationEmail = Objects.requireNonNull(FirebaseAuth
                .getInstance()
                .getCurrentUser())
                .getEmail();
        OrganizationStaff organizationStaff = new OrganizationStaff(organizationEmail, staffEmail);
        organizationStaffRequestDao.checkOrganizationStaffRequest(organizationStaff, new OrganizationStaffRequestDao.OnCheckIfOrganizationStaffRequestExists() {
            @Override
            public void onExists() {
                showText(R.string.request_already_exists);
            }

            @Override
            public void onNotExists() {
                sendRequest(staffEmail);
            }
        });
    }

    private void sendRequest(String staffEmail) {
        String email = Objects.requireNonNull(FirebaseAuth
                .getInstance()
                .getCurrentUser())
                .getEmail();
        OrganizationStaffRequest organizationStaffRequest = new OrganizationStaffRequest(email, staffEmail, false);
        organizationStaffRequestDao.createRequest(organizationStaffRequest, new OrganizationStaffRequestDao.OnCreateOrganizationStaffRequest() {
            @Override
            public void onCreated() {
                showText(R.string.organization_staff_request_created);
                finish();
            }

            @Override
            public void onError() {
                showText(R.string.error_try_again);
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
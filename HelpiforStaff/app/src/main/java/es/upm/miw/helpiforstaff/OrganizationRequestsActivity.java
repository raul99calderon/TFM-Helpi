package es.upm.miw.helpiforstaff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

import es.upm.miw.helpiforstaff.adapters.OrganizationStaffRequestDtoAdapter;
import es.upm.miw.helpiforstaff.daos.OrganizationStaffDao;
import es.upm.miw.helpiforstaff.daos.OrganizationStaffRequestDao;
import es.upm.miw.helpiforstaff.models.OrganizationStaff;
import es.upm.miw.helpiforstaff.models.OrganizationStaffRequestDto;

public class OrganizationRequestsActivity extends AppCompatActivity {

    private OrganizationStaffRequestDtoAdapter adapter;
    private OrganizationStaffRequestDao organizationStaffRequestDao;
    private OrganizationStaffDao organizationStaffDao;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_requests);
        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new OrganizationStaffRequestDtoAdapter(this, this::updateOrganizationStaffRequest);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        organizationStaffRequestDao = OrganizationStaffRequestDao.getInstance();
        organizationStaffDao = OrganizationStaffDao.getInstance();
        this.loadRequests();
    }

    private void loadRequests() {
        adapter.setOrganizationStaffRequestDtos(new ArrayList<>());
        organizationStaffRequestDao.getOrganizationStaffRequests(email, new OrganizationStaffRequestDao.OnGetOrganizationStaffRequests() {
            @Override
            public void onGetOrganizationStaffRequest(OrganizationStaffRequestDto organizationStaffRequestDto) {
                adapter.addItem(organizationStaffRequestDto);
            }

            @Override
            public void onUpdateOrganizationStaffRequest(OrganizationStaffRequestDto organizationStaffRequestDto) {
                adapter.updateItem(organizationStaffRequestDto);
            }

            @Override
            public void onRemoveOrganizationStaffRequest(String organizationEmail) {
                adapter.removeItem(organizationEmail);
            }
        });
    }

    private void updateOrganizationStaffRequest(OrganizationStaffRequestDto organizationStaffRequestDto) {
        organizationStaffRequestDao.updateOrganizationStaffRequest(
                organizationStaffRequestDto.getKey(),
                () -> addToOrganization(organizationStaffRequestDto)
        );
    }

    private void addToOrganization(OrganizationStaffRequestDto organizationStaffRequestDto) {
        OrganizationStaff organizationStaff = new OrganizationStaff(
                organizationStaffRequestDto.getOrganizationEmail(),
                organizationStaffRequestDto.getStaffEmail()
        );
        organizationStaffDao.addToOrganization(organizationStaff, () ->
                Toast.makeText(
                        OrganizationRequestsActivity.this,
                        "Accepted successfully",
                        Toast.LENGTH_SHORT
                ).show()
        );
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
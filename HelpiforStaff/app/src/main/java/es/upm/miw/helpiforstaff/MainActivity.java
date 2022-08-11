package es.upm.miw.helpiforstaff;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.Objects;

import es.upm.miw.helpiforstaff.adapters.OrganizationAdapter;
import es.upm.miw.helpiforstaff.daos.OrganizationDao;
import es.upm.miw.helpiforstaff.models.OrganizationItem;

public class MainActivity extends AppCompatActivity {

    private OrganizationAdapter adapter;
    private FirebaseAuth mAuth;
    private OrganizationDao organizationDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new OrganizationAdapter(this, this::openOrganization);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        organizationDao = OrganizationDao.getInstance();
    }

    private void loadOrganizations() {
        this.adapter.setOrganizationItems(new ArrayList<>());
        organizationDao.getOrganizations(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail(), new OrganizationDao.OnGetOrganizations() {
            @Override
            public void onGetOrganization(OrganizationItem item) {
                adapter.addItem(item);
            }

            @Override
            public void onRemoveOrganization(String organizationEmail) {
                adapter.removeItem(organizationEmail);
            }

            @Override
            public void onGetOrganizationPhoto(String email, Bitmap bitmap) {
                adapter.setOrganizationPhoto(email, bitmap);
            }
        });
    }

    private void openOrganization(OrganizationItem organizationItem) {
        Intent intent = new Intent(this, OrganizationDetailsActivity.class);
        intent.putExtra("email",organizationItem.getEmail());
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() == null)
            finish();
        else
            this.loadOrganizations();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_signout:
                this.signOut();
                break;
            case R.id.btn_profile:
                startActivity(new Intent(this,ProfileActivity.class));
                break;
            case R.id.btn_org_requests:
                startActivity(new Intent(this,OrganizationRequestsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    private void signOut() {
        this.mAuth.signOut();
        Toast.makeText(this,getString(R.string.signed_out),Toast.LENGTH_SHORT).show();
        finish();
    }
}
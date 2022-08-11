package es.upm.miw.helpiadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


import es.upm.miw.helpiadmin.adapters.OrganizationUsersAdapter;
import es.upm.miw.helpiadmin.daos.UserDao;
import es.upm.miw.helpiadmin.models.ConfirmationUserDto;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private OrganizationUsersAdapter adapter;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new OrganizationUsersAdapter(this, this::updateOrganizationRequest);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        userDao = UserDao.getInstance();

        this.loadRequests();
    }

    private void loadRequests() {
        adapter.setConfirmationUsers(new ArrayList<>());

        userDao.getOrganizations(new UserDao.OnGetOrganization() {
            @Override
            public void onGetOrganization(ConfirmationUserDto confirmationUserDto) {
                adapter.addItem(confirmationUserDto);
            }

            @Override
            public void onChangedOrganization(ConfirmationUserDto confirmationUserDto) {
                adapter.updateItem(confirmationUserDto);
            }

            @Override
            public void onRemovedOrganization(String organizationEmail) {
                adapter.removeItem(organizationEmail);
            }
        });
    }

    private void updateOrganizationRequest(ConfirmationUserDto confirmationUserDto) {
        userDao.confirmOrganization(confirmationUserDto.getEmail(), new UserDao.OnUpdateOrganization() {
            @Override
            public void onUpdatedOrganization() {
                Toast.makeText(MainActivity.this, R.string.organization_confirmed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError() {
                Toast.makeText(MainActivity.this, R.string.error_confirming, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
            Toast.makeText(MainActivity.this, getString(R.string.login_welcome) + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(MainActivity.this, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn_signout) {
            this.signOut();
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
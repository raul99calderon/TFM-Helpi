package es.upm.miw.helpi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import es.upm.miw.helpi.adapters.OrganizationEventAdapter;
import es.upm.miw.helpi.daos.EventDao;
import es.upm.miw.helpi.daos.OrganizationDao;
import es.upm.miw.helpi.models.EventDto;
import es.upm.miw.helpi.models.User;

public class OrganizationDetailsActivity extends AppCompatActivity {

    private String organizationEmail;
    private TextView tvEmail, tvName;
    private OrganizationEventAdapter adapter;
    private ImageView ivPhoto;
    private OrganizationDao organizationDao;
    private EventDao eventDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_details);

        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        recyclerViewEvents.setNestedScrollingEnabled(false);
        adapter = new OrganizationEventAdapter(this, this::openEventDetails);
        recyclerViewEvents.setAdapter(adapter);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));

        this.tvEmail = findViewById(R.id.tvEmail);
        this.tvName = findViewById(R.id.tvName);
        this.ivPhoto = findViewById(R.id.ivPhoto);

        this.organizationEmail = getIntent().getStringExtra("email");
        organizationDao = OrganizationDao.getInstance();
        eventDao = EventDao.getInstance();

        this.loadOrganizationData();
        this.loadEvents();
    }

    private void openEventDetails(EventDto eventDto) {
        Intent intent = new Intent(this, EventDetailsActivity.class);
        intent.putExtra("key", eventDto.getKey());
        startActivity(intent);
    }

    private void loadEvents() {
        adapter.setEvents(new ArrayList<>());
        eventDao.getEvents(organizationEmail, new EventDao.OnGetOrganizationEvent() {
            @Override
            public void onGetEvent(EventDto eventDto) {
                adapter.addItem(eventDto);
            }

            @Override
            public void onRemoveEvent(String key) {
                adapter.removeItem(key);
            }
        });
    }

    private void loadOrganizationData() {
        organizationDao.getOrganization(organizationEmail, new OrganizationDao.OnGetUserOrganization() {
            @Override
            public void onGetOrganization(User user) {
                tvEmail.setText(organizationEmail);
                tvName.setText(user.getName());
            }

            @Override
            public void onGetOrganizationPhoto(Bitmap bitmap) {
                ivPhoto.setImageBitmap(bitmap);
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
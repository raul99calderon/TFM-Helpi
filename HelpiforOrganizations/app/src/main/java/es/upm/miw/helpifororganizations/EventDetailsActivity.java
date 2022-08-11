package es.upm.miw.helpifororganizations;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import es.upm.miw.helpifororganizations.adapters.JoinRequestAdapter;
import es.upm.miw.helpifororganizations.daos.EventDao;
import es.upm.miw.helpifororganizations.daos.EventRequestDao;
import es.upm.miw.helpifororganizations.daos.ImageDao;
import es.upm.miw.helpifororganizations.daos.UserDao;
import es.upm.miw.helpifororganizations.models.Event;
import es.upm.miw.helpifororganizations.models.EventJoinRequestItem;
import es.upm.miw.helpifororganizations.models.JoinRequestState;
import es.upm.miw.helpifororganizations.models.Location;

public class EventDetailsActivity extends AppCompatActivity {

    private TextView tvEventName, tvDescription, tvLocation, tvDateTime, tvMaxParticipants, tvOrganizationName;
    private JoinRequestAdapter adapter;
    private String key;
    private Location location;
    private ImageView ivPhoto;
    private Event event;
    private ImageButton btnMap;
    private EventDao eventDao;
    private UserDao userDao;
    private EventRequestDao eventRequestDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        key = intent.getStringExtra("key");

        tvEventName = findViewById(R.id.tvEventName);
        tvDescription = findViewById(R.id.tvDescription);
        tvLocation = findViewById(R.id.tvLocation);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvMaxParticipants = findViewById(R.id.tvMaxParticipants);
        ivPhoto = findViewById(R.id.ivPhoto);
        tvOrganizationName = findViewById(R.id.tvOrganizationName);

        btnMap = findViewById(R.id.btnMap);
        btnMap.setOnClickListener(view -> openWithMaps());


        RecyclerView recyclerViewRequests = findViewById(R.id.recyclerViewRequests);
        adapter = new JoinRequestAdapter(this, new JoinRequestAdapter.OnButtonClickListener() {
            @Override
            public void onAcceptButtonClick(EventJoinRequestItem eventJoinRequest) {
                setRequestResponse(eventJoinRequest, JoinRequestState.ACCEPTED);
            }

            @Override
            public void onDenyButtonClick(EventJoinRequestItem eventJoinRequest) {
                setRequestResponse(eventJoinRequest, JoinRequestState.DENIED);
            }
        });
        recyclerViewRequests.setAdapter(adapter);
        recyclerViewRequests.setLayoutManager(new LinearLayoutManager(this));

        eventDao = EventDao.getInstance();
        userDao = UserDao.getInstance();
        eventRequestDao = EventRequestDao.getInstance();

        this.loadEventRequests();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.loadEventDetails();
    }

    private void setAllResponsesPendingTo(JoinRequestState state) {
        List<EventJoinRequestItem> items = adapter.getItems();
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).getState().equals(JoinRequestState.PENDING))
                setRequestResponse(items.get(i),state);
    }

    private void setRequestResponse(EventJoinRequestItem eventJoinRequestItem, JoinRequestState responseState) {
        eventJoinRequestItem.setState(responseState);
        eventRequestDao.setRequestResponse(eventJoinRequestItem, new EventRequestDao.OnSetRequestResponse() {
            @Override
            public void onSetRequestResponse() {
                Toast.makeText(
                        EventDetailsActivity.this,
                        getString(R.string.response) + responseState + getString(R.string.correct),
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onError() {
                Toast.makeText(
                        EventDetailsActivity.this,
                        getString(R.string.response) + responseState + getString(R.string.incorrect),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void loadEventRequests() {
        adapter.setRequests(new ArrayList<>());
        eventRequestDao.getEventRequests(key, new EventRequestDao.OnGetEventRequest() {
            @Override
            public void onGetEventRequest(EventJoinRequestItem item) {
                adapter.addItem(item);
            }

            @Override
            public void onUpdateEventRequest(EventJoinRequestItem item) {
                adapter.updateItem(item);
            }

            @Override
            public void onRemoveEventRequest(String key) {
                adapter.removeItem(key);
            }
        });
    }

    private void loadEventDetails() {
        eventDao.getEvent(key, event -> {
            this.event = event;
            tvEventName.setText(event.getName());
            tvDescription.setText(event.getDescription());
            location = event.getLocation();
            btnMap.setVisibility(View.VISIBLE);
            tvLocation.setText(event.getLocation().getPlace());
            @SuppressLint("SimpleDateFormat")
            String formattedDateTime = new SimpleDateFormat(getString(R.string.date_time_format))
                    .format(new Timestamp(event.getDateTime()));
            tvDateTime.setText(formattedDateTime);
            tvMaxParticipants.setText(String.valueOf(event.getMaxParticipants()));
            userDao.getOrganizationName(name -> tvOrganizationName.setText(name));

            ImageDao imageDao = ImageDao.getInstance();
            imageDao.getProfilePhoto(event.getOrganizationEmail(), image -> ivPhoto.setImageBitmap(image));
        });
    }

    private void openWithMaps() {
        new Handler().postDelayed(() -> {
            Uri gmmIntentUri =
                    Uri.parse("geo:" + location.getLatitude() + "," + location.getLongitude() + "?q=" + Uri.encode(location.getPlace()));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage(getString(R.string.package_maps));
            startActivity(mapIntent);
        }, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void deleteEvent() {
        eventDao.deleteEvent(key, new EventDao.OnEventDeleted() {
            @Override
            public void onDeleted() {
                finish();
            }

            @Override
            public void onError() {
                Toast.makeText(EventDetailsActivity.this, R.string.error_deleting_event, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_baseline_delete_24)
                .setTitle(R.string.delete_event)
                .setMessage(R.string.delete_event_question)
                .setPositiveButton(R.string.yes, (dialog, which) -> deleteEvent())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void showAcceptAllDialog() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_baseline_check_24)
                .setTitle(R.string.accept_pending_requests)
                .setMessage(R.string.accept_pending_requests_questions)
                .setPositiveButton(R.string.yes, (dialog, which) -> setAllResponsesPendingTo(JoinRequestState.ACCEPTED))
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void showDenyAllDialog() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_baseline_close_24)
                .setTitle(R.string.deny_all_pending)
                .setMessage(R.string.deny_all_pending_question)
                .setPositiveButton(R.string.yes, (dialog, which) -> setAllResponsesPendingTo(JoinRequestState.DENIED))
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_edit:
                if (new Date().getTime() < event.getDateTime()) {
                    Intent intent = new Intent(this, CreateUpdateEventActivity.class);
                    intent.putExtra("key", key);
                    startActivity(intent);
                } else Toast.makeText(this, R.string.do_not_edit_event_started, Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_accept_all:
                showAcceptAllDialog();
                break;
            case R.id.btn_deny_all:
                showDenyAllDialog();
                break;
            case R.id.btn_delete:
                this.showDeleteDialog();
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
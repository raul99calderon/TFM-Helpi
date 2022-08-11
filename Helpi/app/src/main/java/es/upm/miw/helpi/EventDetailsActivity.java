package es.upm.miw.helpi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.firebase.auth.FirebaseAuth;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import es.upm.miw.helpi.daos.EventDao;
import es.upm.miw.helpi.daos.EventRequestDao;
import es.upm.miw.helpi.daos.ImageDao;
import es.upm.miw.helpi.models.EventRequest;
import es.upm.miw.helpi.models.JoinRequestState;
import es.upm.miw.helpi.models.Location;

public class EventDetailsActivity extends AppCompatActivity {

    private TextView tvEventName, tvDescription, tvLocation, tvDateTime, tvOrganizationName, tvMaxParticipants;
    private ConstraintLayout clAccepted, clPending, clDenied, clAttended;
    private Location location;
    private ImageView ivPhoto;
    private Button btnJoin;
    private Button btnCancelRequest;
    private ImageButton btnMap;
    private FirebaseAuth mAuth;
    private String key;
    private String requestKey;
    private EventDao eventDao;
    private EventRequestDao eventRequestDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);

        clAccepted = findViewById(R.id.clAccepted);
        clPending = findViewById(R.id.clPending);
        clDenied = findViewById(R.id.clDenied);
        clAttended = findViewById(R.id.clAttended);
        tvEventName = findViewById(R.id.tvEventName);
        tvDescription = findViewById(R.id.tvDescription);
        tvLocation = findViewById(R.id.tvLocation);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvOrganizationName = findViewById(R.id.tvOrganizationName);
        ivPhoto = findViewById(R.id.ivPhoto);

        btnJoin = findViewById(R.id.btnJoinEvent);
        btnJoin.setOnClickListener(view -> addRequest());
        btnCancelRequest = findViewById(R.id.btnCancelRequest);
        btnCancelRequest.setOnClickListener(view -> removeRequest());
        Button btnCancelParticipation = findViewById(R.id.btnCancelParticipation);
        btnCancelParticipation.setOnClickListener(view -> removeRequest());

        tvMaxParticipants = findViewById(R.id.tvMaxParticipants);
        btnMap = findViewById(R.id.btnMap);
        btnMap.setOnClickListener(view -> openWithMaps());
        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        mAuth = FirebaseAuth.getInstance();
        eventDao = EventDao.getInstance();
        eventRequestDao = EventRequestDao.getInstance();
        this.loadEventDetails();
        this.loadRequestState();
    }

    private void loadEventDetails() {
        eventDao.getEvent(key, eventItem -> {
            tvEventName.setText(eventItem.getName());
            tvDescription.setText(eventItem.getDescription());
            location = eventItem.getLocation();
            btnMap.setVisibility(View.VISIBLE);
            tvLocation.setText(eventItem.getLocation().getPlace());
            @SuppressLint("SimpleDateFormat")
            String formattedDateTime = new SimpleDateFormat(getString(R.string.date_time_format))
                    .format(new Timestamp(eventItem.getDateTime()));
            tvDateTime.setText(formattedDateTime);
            tvMaxParticipants.setText(String.valueOf(eventItem.getMaxParticipants()));
            tvOrganizationName.setText(eventItem.getOrganizationName());

            ImageDao imageDao = ImageDao.getInstance();
            imageDao.getProfilePhoto(eventItem.getOrganizationEmail(), image -> ivPhoto.setImageBitmap(image));
        });
    }

    private void loadRequestState() {
        eventRequestDao.getEventRequest(key, new EventRequestDao.OnEventRequest() {
            @Override
            public void onEventRequest(EventRequest eventRequest, String requestKey) {
                EventDetailsActivity.this.requestKey = requestKey;
                clAccepted.setVisibility(View.GONE);
                clPending.setVisibility(View.GONE);
                clDenied.setVisibility(View.GONE);
                btnJoin.setVisibility(View.GONE);

                switch (eventRequest.getState()) {
                    case ACCEPTED:
                        clAccepted.setVisibility(View.VISIBLE);
                        break;
                    case PENDING:
                        clPending.setVisibility(View.VISIBLE);
                        break;
                    case DENIED:
                        clDenied.setVisibility(View.VISIBLE);
                        break;
                    case ATTENDED:
                        clAttended.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNotExists() {
                clAccepted.setVisibility(View.GONE);
                clPending.setVisibility(View.GONE);
                clDenied.setVisibility(View.GONE);
                btnJoin.setVisibility(View.VISIBLE);
            }
        });
    }

    private void addRequest() {
        btnJoin.setVisibility(View.GONE);
        EventRequest eventRequest = new EventRequest(
                key,
                Objects.requireNonNull(mAuth.getCurrentUser()).getEmail(),
                new Date().getTime(),
                JoinRequestState.PENDING
        );

        eventRequestDao.addRequest(eventRequest, new EventRequestDao.OnRequestAdded() {
            @Override
            public void onRequestAdded() {
                Toast.makeText(EventDetailsActivity.this, R.string.request_sent_correctly, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError() {
                Toast.makeText(EventDetailsActivity.this, R.string.error_sending_request, Toast.LENGTH_SHORT).show();
                btnJoin.setVisibility(View.VISIBLE);
            }
        });
    }

    private void removeRequest() {
        btnCancelRequest.setVisibility(View.GONE);
        eventRequestDao.deleteRequest(requestKey, new EventRequestDao.OnDeleteRequest() {
            @Override
            public void onDeletedRequest() {
                Toast.makeText(EventDetailsActivity.this, R.string.request_removed_correctly, Toast.LENGTH_SHORT).show();
                btnCancelRequest.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
                Toast.makeText(EventDetailsActivity.this, R.string.error_removing_request, Toast.LENGTH_SHORT).show();
                btnCancelRequest.setVisibility(View.VISIBLE);
            }
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
package es.upm.miw.helpiforstaff;

import static es.upm.miw.helpiforstaff.utils.Validators.validateEmail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import es.upm.miw.helpiforstaff.daos.EventDao;
import es.upm.miw.helpiforstaff.daos.EventRequestDao;
import es.upm.miw.helpiforstaff.daos.ImageDao;
import es.upm.miw.helpiforstaff.daos.RankingDao;
import es.upm.miw.helpiforstaff.daos.UserDao;
import es.upm.miw.helpiforstaff.models.EventRequest;
import es.upm.miw.helpiforstaff.models.JoinRequestState;
import es.upm.miw.helpiforstaff.models.Location;
import es.upm.miw.helpiforstaff.models.Role;

public class EventDetailsActivity extends AppCompatActivity {

    private TextView tvEventName, tvDescription, tvLocation, tvDateTime, tvOrganizationName, tvMaxParticipants;
    private Location location;
    private ImageView ivPhoto;
    private TextInputLayout emailContainer;
    private EditText etEmail;
    private ConstraintLayout clConfirmAssist, clInfo;
    private String key;
    private EventDao eventDao;
    private UserDao userDao;
    private EventRequestDao eventRequestDao;
    private RankingDao rankingDao;
    private ImageButton btnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);

        tvEventName = findViewById(R.id.tvEventName);
        tvDescription = findViewById(R.id.tvDescription);
        tvLocation = findViewById(R.id.tvLocation);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvOrganizationName = findViewById(R.id.tvOrganizationName);
        ivPhoto = findViewById(R.id.ivPhoto);
        tvMaxParticipants = findViewById(R.id.tvMaxParticipants);
        Button btnConfirmAssist = findViewById(R.id.btnConfirmAssist);
        etEmail = findViewById(R.id.etEmail);
        emailContainer = findViewById(R.id.emailContainer);
        clConfirmAssist = findViewById(R.id.clConfirmAssist);
        clInfo = findViewById(R.id.clInfo);
        btnConfirmAssist.setOnClickListener(view -> confirmAssist());

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

        btnMap = findViewById(R.id.btnMap);
        btnMap.setOnClickListener(view -> openWithMaps());
        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        eventDao = EventDao.getInstance();
        userDao = UserDao.getInstance();
        eventRequestDao = EventRequestDao.getInstance();
        rankingDao = RankingDao.getInstance();

        this.loadEventDetails();
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

            if (eventItem.getDateTime() <= new Date().getTime())
                clConfirmAssist.setVisibility(View.VISIBLE);
            else
                clInfo.setVisibility(View.VISIBLE);

            ImageDao imageDao = ImageDao.getInstance();
            imageDao.getProfilePhoto(eventItem.getOrganizationEmail(), image -> ivPhoto.setImageBitmap(image));
        });
    }

    private void confirmAssist() {
        String email = etEmail.getText().toString();
        if (this.isValidEmail(email)) {
            userDao.getUser(email, user -> {
                if (user.getRole().equals(Role.USER.name())) {
                    checkRequestIsAlreadyAcceptedOrAttended(email);
                } else
                    Toast.makeText(EventDetailsActivity.this, R.string.error_not_user_role, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void checkRequestIsAlreadyAcceptedOrAttended(String email) {
        eventRequestDao.getEventRequest(email, key, new EventRequestDao.OnEventRequest() {
            @Override
            public void onEventRequest(EventRequest eventRequest, String requestKey) {
                switch (eventRequest.getState()) {
                    case ACCEPTED:
                        eventRequest.setState(JoinRequestState.ATTENDED);
                        setJoinRequest(eventRequest, requestKey);
                        break;
                    case ATTENDED:
                        Toast.makeText(EventDetailsActivity.this, R.string.attendance_already_confirmed, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(EventDetailsActivity.this, R.string.user_was_not_accepted, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNotExists() {
                Toast.makeText(EventDetailsActivity.this, R.string.user_not_requested_join, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setJoinRequest(EventRequest request, String snapshotKey) {
        eventRequestDao.setEventRequest(request, snapshotKey, () -> rankingDao.getRankingUser(request.getUserEmail(), (rankingUserDto, rankingUserKey) -> {
            Objects.requireNonNull(rankingUserDto).incrementNumEventsAttended();
            rankingDao.setRankingUser(rankingUserDto, rankingUserKey, new RankingDao.OnSetRankingUser() {
                @Override
                public void onSetRankingUser() {
                    Toast.makeText(
                            EventDetailsActivity.this,
                            request.getUserEmail() + getString(R.string.attendance_confirmed_correctly),
                            Toast.LENGTH_SHORT
                    ).show();
                }

                @Override
                public void onError() {
                    Toast.makeText(
                            EventDetailsActivity.this,
                            getString(R.string.error_confirming_attendance) + request.getUserEmail() + getString(R.string.attend),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        }));
    }

    private boolean isValidEmail(String email) {
        String validEmail = validateEmail(email);
        if (validEmail != null)
            this.emailContainer.setHelperText(validEmail);
        return validEmail == null;
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
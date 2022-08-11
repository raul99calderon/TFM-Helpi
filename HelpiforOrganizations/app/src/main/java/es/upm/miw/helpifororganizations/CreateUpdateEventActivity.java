package es.upm.miw.helpifororganizations;

import static es.upm.miw.helpifororganizations.utils.Validators.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import es.upm.miw.helpifororganizations.api.APIService;
import es.upm.miw.helpifororganizations.daos.EventDao;
import es.upm.miw.helpifororganizations.models.Event;
import es.upm.miw.helpifororganizations.models.Location;
import es.upm.miw.helpifororganizations.models.position_stack.Datum;
import es.upm.miw.helpifororganizations.models.position_stack.PositionStack;
import es.upm.miw.helpifororganizations.utils.AppExecutors;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateUpdateEventActivity extends AppCompatActivity {

    private EditText etDateTime, etName, etDescription, etLocation, etMaxParticipants;
    private TextInputLayout nameContainer, descriptionContainer, dateTimeContainer, locationContainer, maxParticipantsContainer;
    private TextView tvLocation;
    private FirebaseAuth mAuth;
    private RadioGroup radioGroup;
    private APIService apiService;
    private PositionStack positionStack;
    private Location location;
    private boolean isUpdate = false;
    private String key;
    private EventDao eventDao;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_update_event);
        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        eventDao = EventDao.getInstance();

        Button btnCreate = findViewById(R.id.btnCreateEvent);
        key = getIntent().getStringExtra("key");
        if (key != null) {
            isUpdate = true;
            btnCreate.setText(R.string.update_event);
            this.loadData();
        }

        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        etMaxParticipants = findViewById(R.id.etMaxParticipants);
        etDateTime = findViewById(R.id.dateTimeInput);
        etDateTime.setInputType(InputType.TYPE_NULL);
        tvLocation = findViewById(R.id.tvLocation);

        nameContainer = findViewById(R.id.nameContainer);
        descriptionContainer = findViewById(R.id.descriptionContainer);
        dateTimeContainer = findViewById(R.id.dateTimeContainer);
        locationContainer = findViewById(R.id.locationContainer);
        maxParticipantsContainer = findViewById(R.id.maxParticipantsContainer);

        this.setTextWatchers();

        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (radioGroup.getChildCount() > 0) {
                Datum datum = positionStack.getData().get(i);
                location = new Location(
                        datum.getLabel(),
                        datum.getLatitude(),
                        datum.getLongitude()
                );
                tvLocation.setVisibility(View.VISIBLE);
                tvLocation.setText(location.getPlace());
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.url_api_positionstack))
                .client(getUnsafeOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(APIService.class);

        etLocation.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP)
                if (event.getRawX() >= (etLocation.getRight() - etLocation.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    searchLocation();
                    return true;
                }
            return false;
        });


        etDateTime.setOnClickListener(view -> showDateTimeDialog());

        btnCreate.setOnClickListener(view -> {
            if (allFieldsAreValid()) {
                if (isUpdate)
                    updateEvent();
                else
                    createEvent();
            } else
                Toast.makeText(CreateUpdateEventActivity.this, R.string.wrong_fields, Toast.LENGTH_SHORT).show();
        });
    }

    private boolean allFieldsAreValid() {
        return validateName(etName.getText().toString()) == null
                && validateDescription(etDescription.getText().toString()) == null
                && validateDateTime(etDateTime.getText().toString()) == null
                && validateLocation(location) == null
                && validateMaxParticipants(etMaxParticipants.getText().toString()) == null;
    }

    private void loadData() {
        eventDao.getEvent(key, event -> {
            if (new Date().getTime() >= Objects.requireNonNull(event).getDateTime()) {
                Toast.makeText(CreateUpdateEventActivity.this, R.string.do_not_edit_event_started, Toast.LENGTH_SHORT).show();
                finish();
            }
            etName.setText(event.getName());
            etDescription.setText(event.getDescription());
            location = event.getLocation();
            etLocation.setText(location.getPlace());
            tvLocation.setText(location.getPlace());
            tvLocation.setVisibility(View.VISIBLE);
            @SuppressLint("SimpleDateFormat")
            String formattedDateTime = new SimpleDateFormat(getString(R.string.date_time_format))
                    .format(new Timestamp(event.getDateTime()));
            etDateTime.setText(formattedDateTime);
            etMaxParticipants.setText(String.valueOf(event.getMaxParticipants()));
        });
    }

    private void searchLocation() {
        if (TextUtils.isEmpty(etLocation.getText().toString()))
            return;

        Call<PositionStack> call = apiService.getGeocode(
                getString(R.string.key_positionstack),
                etLocation.getText().toString()
        );

        AppExecutors.getInstance().networkIO().execute(() -> call.enqueue(new Callback<PositionStack>() {
            @Override
            public void onResponse(@NonNull Call<PositionStack> call, @NonNull Response<PositionStack> response) {
                AppExecutors.getInstance().mainThread().execute(() -> {
                    if (response.isSuccessful())
                        setRadioGroup(response.body());
                    else
                        Toast.makeText(CreateUpdateEventActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(@NonNull Call<PositionStack> call, @NonNull Throwable t) {
            }
        }));
    }

    private void setRadioGroup(PositionStack positionStack) {
        this.positionStack = positionStack;
        tvLocation.setVisibility(View.GONE);
        radioGroup.removeAllViews();
        radioGroup.clearCheck();
        int i = 0;
        for (Datum datum : Objects.requireNonNull(positionStack).getData()) {
            RadioButton radioButton = new RadioButton(CreateUpdateEventActivity.this);
            radioButton.setText(datum.getLabel());
            radioButton.setId(i);
            radioGroup.addView(radioButton);
            i++;
        }
    }

    private void createEvent() {
        Event event = this.buildEvent();
        eventDao.createEvent(event, new EventDao.OnCreateEvent() {
            @Override
            public void onCreatedEvent() {
                Toast.makeText(
                        CreateUpdateEventActivity.this,
                        R.string.event_created_correctly,
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            }

            @Override
            public void onError() {
                Toast.makeText(
                        CreateUpdateEventActivity.this,
                        R.string.error_create_event,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void updateEvent() {
        Event event = this.buildEvent();
        eventDao.updateEvent(key, event, new EventDao.OnUpdatedEvent() {
            @Override
            public void onUpdatedEvent() {
                Toast.makeText(
                        CreateUpdateEventActivity.this,
                        R.string.event_updated_correctly,
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            }

            @Override
            public void onError() {
                Toast.makeText(
                        CreateUpdateEventActivity.this,
                        R.string.error_update_event,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private Event buildEvent() {
        String name = etName.getText().toString();
        String description = etDescription.getText().toString();
        String dateTimeText = etDateTime.getText().toString() + ":00";
        Integer maxParticipants = Integer.parseInt(etMaxParticipants.getText().toString());
        Timestamp timestamp = Timestamp.valueOf(dateTimeText);
        Long dateTime = timestamp.getTime();

        return new Event(
                name,
                description,
                location,
                Objects.requireNonNull(mAuth.getCurrentUser()).getEmail(),
                dateTime,
                maxParticipants
        );
    }

    private void showDateTimeDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog.OnTimeSetListener timeSetListener = (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getString(R.string.no_seconds_format), Locale.getDefault());

                etDateTime.setText(simpleDateFormat.format(calendar.getTime()));
            };

            new TimePickerDialog(
                    CreateUpdateEventActivity.this,
                    timeSetListener,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
            ).show();
        };

        new DatePickerDialog(
                CreateUpdateEventActivity.this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void setTextWatchers() {
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
        tvLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                locationContainer.setHelperText(validateLocation(location));
            }
        });
        etDateTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                dateTimeContainer.setHelperText(validateDateTime(editable.toString()));
            }
        });
        etDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                descriptionContainer.setHelperText(validateDescription(editable.toString()));
            }
        });
        etMaxParticipants.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                maxParticipantsContainer.setHelperText(validateMaxParticipants(editable.toString()));
            }
        });
    }

    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            final SSLContext sslContext = SSLContext.getInstance(getString(R.string.ssl));
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
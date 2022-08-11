package es.upm.miw.helpifororganizations;

import static es.upm.miw.helpifororganizations.utils.Validators.validateName;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import es.upm.miw.helpifororganizations.daos.EventDao;
import es.upm.miw.helpifororganizations.daos.ImageDao;
import es.upm.miw.helpifororganizations.daos.NoticeDao;
import es.upm.miw.helpifororganizations.daos.OrganizationStaffDao;
import es.upm.miw.helpifororganizations.daos.OrganizationStaffRequestDao;
import es.upm.miw.helpifororganizations.daos.UserDao;
import es.upm.miw.helpifororganizations.models.User;

public class ProfileActivity extends AppCompatActivity {

    private EditText etEmail, etName;
    private ImageView ivPhoto;
    private TextInputLayout nameContainer;
    private FirebaseAuth mAuth;
    private boolean imageViewChanged = false;
    private String previousName;
    private String email;
    private UserDao userDao;
    private ImageDao imageDao;
    private EventDao eventDao;
    private OrganizationStaffRequestDao organizationStaffRequestDao;
    private OrganizationStaffDao organizationStaffDao;
    private NoticeDao noticeDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.loadActionBar();

        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        ivPhoto = findViewById(R.id.ivPhoto);
        nameContainer = findViewById(R.id.nameContainer);

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

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imageViewChanged = true;
                        Uri selectedImage = Objects.requireNonNull(result.getData()).getData();
                        ivPhoto.setImageURI(selectedImage);
                    }
                }
        );

        ivPhoto.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);
        });

        mAuth = FirebaseAuth.getInstance();
        email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        Button btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnDeleteAccount.setOnClickListener(view -> deleteAccount());

        userDao = UserDao.getInstance();
        imageDao = ImageDao.getInstance();
        eventDao = EventDao.getInstance();
        organizationStaffRequestDao = OrganizationStaffRequestDao.getInstance();
        organizationStaffDao = OrganizationStaffDao.getInstance();
        noticeDao = NoticeDao.getInstance();

        this.loadUserDetails();
    }

    private void loadUserDetails() {
        etEmail.setText(email);
        userDao.getUser(email, user -> {
            etName.setText(user.getName());
            previousName = user.getName();
        });
        imageDao.getProfilePhoto(email, image -> ivPhoto.setImageBitmap(image));
    }

    private void saveProfile() {
        if (previousName.equals(etName.getText().toString()) && !imageViewChanged) {
            Toast.makeText(ProfileActivity.this, R.string.nothing_changed, Toast.LENGTH_SHORT).show();
            return;
        }
        String name = etName.getText().toString();
        if (!previousName.equals(name)) {
            String validName = validateName(name);
            if (validName == null) {
                User user = new User(email, name);
                userDao.saveProfile(user, Objects.requireNonNull(mAuth.getCurrentUser()).getUid(), new UserDao.OnSavedProfile() {
                    @Override
                    public void savedProfile() {
                        previousName = name;
                        if (imageViewChanged)
                            updatePhotoProfile();
                        else
                            Toast.makeText(ProfileActivity.this, R.string.profile_updated_correctly, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void errorSaving() {
                        Toast.makeText(ProfileActivity.this, R.string.error_updating_profile, Toast.LENGTH_SHORT).show();
                    }
                });
            } else this.nameContainer.setHelperText(validName);
        } else if (imageViewChanged) updatePhotoProfile();
    }

    private void updatePhotoProfile() {
        ivPhoto.setDrawingCacheEnabled(true);
        ivPhoto.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) ivPhoto.getDrawable()).getBitmap();
        imageDao.uploadProfilePhoto(email, bitmap, new ImageDao.OnUploadedImage() {
            @Override
            public void onUploaded() {
                Toast.makeText(ProfileActivity.this, R.string.profile_updated_correctly, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError() {
                Toast.makeText(ProfileActivity.this, R.string.error_updating_profile, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImageSizeError() {
                Toast.makeText(ProfileActivity.this, R.string.larger_size, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteAccount() {
        userDao.deleteAccount(new UserDao.OnDeleteAccount() {
            @Override
            public void onDeleted() {
                deleteUser();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteUser() {
        userDao.deleteUser(email, new UserDao.OnDeleteAccount() {
            @Override
            public void onDeleted() {
                imageDao.deleteProfilePhoto(email, () -> deleteEvents());
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteEvents() {
        eventDao.deleteEvents(email, new EventDao.OnEventDeleted() {
            @Override
            public void onDeleted() {
                deleteOrganizationStaffRequests();
            }

            @Override
            public void onError() {
                Toast.makeText(ProfileActivity.this, R.string.error_general, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteOrganizationStaffRequests() {
        organizationStaffRequestDao.deleteRequests(email, this::deleteOrganizationStaff);
    }

    private void deleteOrganizationStaff() {
        organizationStaffDao.deleteOrganizationStaff(email, this::deleteNotices);
    }

    private void deleteNotices() {
        noticeDao.deleteNotices(email, () -> {
            Toast.makeText(ProfileActivity.this, R.string.account_deleted_correctly, Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void loadActionBar() {
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.profile_actionbar, null);
        TextView textView = view.findViewById(R.id.tvTitle);
        textView.setText(R.string.profile);
        actionBar.setCustomView(view);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.profile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnSave:
                saveProfile();
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
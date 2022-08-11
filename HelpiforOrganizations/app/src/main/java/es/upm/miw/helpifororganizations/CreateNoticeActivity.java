package es.upm.miw.helpifororganizations;

import static es.upm.miw.helpifororganizations.utils.Validators.validateBody;
import static es.upm.miw.helpifororganizations.utils.Validators.validateName;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;
import java.util.Objects;

import es.upm.miw.helpifororganizations.daos.ImageDao;
import es.upm.miw.helpifororganizations.daos.NoticeDao;
import es.upm.miw.helpifororganizations.models.Notice;

public class CreateNoticeActivity extends AppCompatActivity {

    private EditText etTitle, etBody;
    private TextInputLayout titleContainer, bodyContainer;
    private FirebaseAuth mAuth;
    private ImageView ivNoticeImage;
    private ImageDao imageDao;
    private NoticeDao noticeDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notice);

        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.create_notice);

        mAuth = FirebaseAuth.getInstance();
        imageDao = ImageDao.getInstance();
        noticeDao = NoticeDao.getInstance();

        etTitle = findViewById(R.id.etTitle);
        etBody = findViewById(R.id.etBody);
        titleContainer = findViewById(R.id.titleContainer);
        bodyContainer = findViewById(R.id.bodyContainer);
        ivNoticeImage = findViewById(R.id.ivNoticeImage);
        Button btnAddImage = findViewById(R.id.btnAddImage);
        Button btnCreateNotice = findViewById(R.id.btnCreateNotice);
        this.setTextWatchers();

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri selectedImage = Objects.requireNonNull(result.getData()).getData();
                        ivNoticeImage.setImageURI(selectedImage);
                        ivNoticeImage.setVisibility(View.VISIBLE);
                    }
                }
        );

        btnAddImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);
        });

        btnCreateNotice.setOnClickListener(view -> {
            if (allFieldsAreValid())
                createNotice();
            else
                Toast.makeText(CreateNoticeActivity.this, R.string.wrong_fields, Toast.LENGTH_SHORT).show();
        });
    }

    private boolean allFieldsAreValid() {
        return validateName(etTitle.getText().toString()) == null
                && validateBody(etBody.getText().toString()) == null
                && ivNoticeImage.getDrawable() != null;
    }

    private void createNotice() {
        Notice notice = new Notice();
        String title = etTitle.getText().toString();
        String body = etBody.getText().toString();
        Long dateTime = new Date().getTime();
        notice.setTitle(title);
        notice.setBody(body);
        notice.setDateTime(dateTime);
        notice.setEmail(Objects.requireNonNull(mAuth.getCurrentUser())
                .getEmail());

        noticeDao.addNotice(notice, new NoticeDao.OnAddNotice() {
            @Override
            public void onAddedNotice(String key) {
                uploadImage(key);
            }

            @Override
            public void onError() {
                Toast.makeText(CreateNoticeActivity.this, R.string.error_publishing_notice, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage(String key) {
        ivNoticeImage.setDrawingCacheEnabled(true);
        ivNoticeImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) ivNoticeImage.getDrawable()).getBitmap();
        imageDao.uploadNoticeImage(key, bitmap, new ImageDao.OnUploadedImage() {
            @Override
            public void onUploaded() {
                Toast.makeText(CreateNoticeActivity.this, R.string.notice_published, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError() {
                Toast.makeText(CreateNoticeActivity.this, R.string.error_uploading_image, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImageSizeError() {
                Toast.makeText(CreateNoticeActivity.this, R.string.larger_image_2MB, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setTextWatchers() {
        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                titleContainer.setHelperText(validateName(editable.toString()));
            }
        });
        etBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                bodyContainer.setHelperText(validateBody(editable.toString()));
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
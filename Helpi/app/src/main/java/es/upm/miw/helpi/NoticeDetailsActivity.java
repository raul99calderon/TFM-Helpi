package es.upm.miw.helpi;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;
import es.upm.miw.helpi.daos.NoticeDao;
import es.upm.miw.helpi.models.Notice;

public class NoticeDetailsActivity extends AppCompatActivity {

    private String key;
    private TextView tvTitle, tvBody, tvDateTime, tvOrganizationName;
    private ImageView ivNoticeImage;
    private NoticeDao noticeDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_details);

        Objects.requireNonNull(getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);

        tvTitle = findViewById(R.id.tvTitle);
        tvBody = findViewById(R.id.tvBody);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvOrganizationName = findViewById(R.id.tvOrganizationName);
        ivNoticeImage = findViewById(R.id.ivImageNotice);

        key = getIntent().getStringExtra("key");
        noticeDao = NoticeDao.getInstance();

        this.loadData();
    }

    private void loadData() {
        noticeDao.getNotice(key, new NoticeDao.OnGetNotice() {
            @Override
            public void onGetNotice(Notice notice) {
                tvTitle.setText(Objects.requireNonNull(notice)
                        .getTitle());
                @SuppressLint("SimpleDateFormat")
                String formattedDateTime = new SimpleDateFormat(
                        getString(R.string.date_time_format))
                        .format(new Timestamp(notice.getDateTime()));
                tvDateTime.setText(formattedDateTime);
                tvBody.setText(notice.getBody());
            }

            @Override
            public void onGetNoticeImage(Bitmap bitmap) {
                ivNoticeImage.setImageBitmap(bitmap);
                ivNoticeImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onGetOrganizationName(String name) {
                tvOrganizationName.setText(name);
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
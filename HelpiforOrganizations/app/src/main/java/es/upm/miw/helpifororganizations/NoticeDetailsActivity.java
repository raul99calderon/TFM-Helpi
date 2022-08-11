package es.upm.miw.helpifororganizations;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;

import es.upm.miw.helpifororganizations.daos.NoticeDao;
import es.upm.miw.helpifororganizations.models.Notice;

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
                ivNoticeImage.setVisibility(View.VISIBLE);
                ivNoticeImage.setImageBitmap(bitmap);
            }

            @Override
            public void onGetOrganizationName(String name) {
                tvOrganizationName.setText(name);
            }
        });
    }

    private void deleteNotice() {
        noticeDao.deleteNotice(key, new NoticeDao.OnDeleteNotice() {
            @Override
            public void onDeletedNotice() {
                finish();
            }

            @Override
            public void onError() {
                Toast.makeText(NoticeDetailsActivity.this, R.string.error_deleting_notice, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_baseline_delete_24)
                .setTitle(R.string.delete_notice)
                .setMessage(R.string.delete_notice_question)
                .setPositiveButton(R.string.yes, (dialog, which) -> deleteNotice())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notice_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btnDelete:
                showDeleteDialog();
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
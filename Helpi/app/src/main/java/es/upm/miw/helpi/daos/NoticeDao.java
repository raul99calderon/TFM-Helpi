package es.upm.miw.helpi.daos;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.upm.miw.helpi.models.Notice;
import es.upm.miw.helpi.models.NoticeDto;
import es.upm.miw.helpi.models.NoticeItem;
import es.upm.miw.helpi.models.User;


public final class NoticeDao {
    private static NoticeDao instance;
    private static DatabaseReference noticesRef;

    private static final String NOTICES = "notices";
    private static final String DATE_TIME = "dateTime";
    private static final Integer LIMIT = 10;
    private static UserDao userDao;
    private static ImageDao imageDao;

    public static NoticeDao getInstance() {
        if (instance == null) {
            instance = new NoticeDao();
            userDao = UserDao.getInstance();
            imageDao = ImageDao.getInstance();
            noticesRef = FirebaseDatabase.getInstance().getReference(NOTICES);
        }

        return instance;
    }

    public interface OnGetNotices {
        void onGetNotices(List<NoticeItem> noticeItems);
        void onGetOrganizationName(User user);
        void onError();
    }

    public void getNotices(OnGetNotices onGetNotices) {
        noticesRef.orderByChild(DATE_TIME)
                .limitToLast(LIMIT)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<NoticeItem> noticeItems = new ArrayList<>();

                        for (DataSnapshot k : snapshot.getChildren()) {
                            NoticeDto noticeDto = k.getValue(NoticeDto.class);
                            NoticeItem noticeItem = new NoticeItem(
                                    Objects.requireNonNull(noticeDto),
                                    k.getKey(),
                                    null
                            );
                            noticeItems.add(noticeItem);

                            userDao.getUser(noticeDto.getEmail(), onGetNotices::onGetOrganizationName);
                        }
                        onGetNotices.onGetNotices(noticeItems);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        onGetNotices.onError();
                    }
                });
    }

    public interface OnGetNotice {
        void onGetNotice(Notice notice);
        void onGetNoticeImage(Bitmap bitmap);
        void onGetOrganizationName(String name);
    }

    public void getNotice(String noticeKey, OnGetNotice onGetNotice) {
        noticesRef.child(noticeKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Notice notice = snapshot.getValue(Notice.class);
                        String noticeKey = snapshot.getKey();
                        onGetNotice.onGetNotice(notice);

                        userDao.getUser(Objects.requireNonNull(notice).getEmail(), user ->
                                onGetNotice.onGetOrganizationName(user.getName()));

                        imageDao.getNoticeImage(noticeKey, onGetNotice::onGetNoticeImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}

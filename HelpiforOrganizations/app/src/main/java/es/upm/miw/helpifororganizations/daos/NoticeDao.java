package es.upm.miw.helpifororganizations.daos;

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

import es.upm.miw.helpifororganizations.models.Notice;
import es.upm.miw.helpifororganizations.models.NoticeDto;
import es.upm.miw.helpifororganizations.models.NoticeItem;

public final class NoticeDao {
    private static NoticeDao instance;
    private static DatabaseReference noticesRef;

    private static final String NOTICES = "notices";
    private static final String EMAIL = "email";
    private static ImageDao imageDao;
    private static UserDao userDao;

    public static NoticeDao getInstance() {
        if (instance == null) {
            instance = new NoticeDao();
            imageDao = ImageDao.getInstance();
            userDao = UserDao.getInstance();
            noticesRef = FirebaseDatabase.getInstance().getReference(NOTICES);
        }

        return instance;
    }

    public interface OnLoadNotices {
        void OnLoadNotice(List<NoticeItem> noticeItems);
    }

    public void loadNotices(String email, OnLoadNotices onLoadNotices) {
        noticesRef.orderByChild(EMAIL)
                .equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<NoticeItem> noticeItems = new ArrayList<>();
                        for (DataSnapshot k : snapshot.getChildren()) {
                            NoticeDto noticeDto = k.getValue(NoticeDto.class);
                            NoticeItem noticeItem = new NoticeItem(
                                    Objects.requireNonNull(noticeDto),
                                    k.getKey()
                            );
                            noticeItems.add(noticeItem);
                        }
                        onLoadNotices.OnLoadNotice(noticeItems);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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

    public interface OnAddNotice {
        void onAddedNotice(String key);
        void onError();
    }

    public void addNotice(Notice notice, OnAddNotice onAddNotice) {
        String key = noticesRef.push()
                .getKey();

        noticesRef.child(Objects.requireNonNull(key))
                .setValue(notice)
                .addOnSuccessListener(unused -> onAddNotice.onAddedNotice(key))
                .addOnFailureListener(e -> onAddNotice.onError());
    }

    public interface OnDeleteNotice {
        void onDeletedNotice();
        void onError();
    }

    public void deleteNotice(String key, OnDeleteNotice onDeleteNotice) {
        noticesRef.child(key)
                .removeValue()
                .addOnSuccessListener(unused -> imageDao.deleteNoticeImage(key, onDeleteNotice::onDeletedNotice))
                .addOnFailureListener(e -> onDeleteNotice.onError());
    }

    public interface OnDeleteNotices {
        void onDeleted();
    }

    public void deleteNotices(String organizationEmail, OnDeleteNotices onDeleteNotices) {
        noticesRef.orderByChild(EMAIL)
                .equalTo(organizationEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot k : snapshot.getChildren()) {
                            k.getRef().removeValue();

                            imageDao.deleteNoticeImage(k.getKey(), () -> { });
                        }
                        onDeleteNotices.onDeleted();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}

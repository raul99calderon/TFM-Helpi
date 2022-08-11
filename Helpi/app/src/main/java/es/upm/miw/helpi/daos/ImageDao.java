package es.upm.miw.helpi.daos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;

public final class ImageDao {

    public static ImageDao instance;
    private static FirebaseStorage storage;
    private static final long ONE_MEGABYTE = 1024 * 1024;
    private static final long TWO_MEGABYTES = 2 * ONE_MEGABYTE;
    private static final String PROFILE_PHOTOS = "profile_photos";
    private static final String NOTICE_IMAGES = "notice_images";

    public interface OnLoadedImage {
        void loaded(Bitmap image);
    }

    public static ImageDao getInstance() {
        if (instance == null) {
            instance = new ImageDao();
            storage = FirebaseStorage.getInstance();
        }
        return instance;
    }

    public void getProfilePhoto(String email, OnLoadedImage onLoadedImage) {
        storage.getReference(PROFILE_PHOTOS + "/" + email)
                .getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(bytes -> onLoadedImage.loaded(decodeBytes(bytes)));
    }

    public void getNoticeImage(String key, OnLoadedImage onLoadedImage) {
        storage.getReference(NOTICE_IMAGES + "/" + key)
                .getBytes(TWO_MEGABYTES)
                .addOnSuccessListener(bytes -> onLoadedImage.loaded(decodeBytes(bytes)));
    }

    private Bitmap decodeBytes(byte[] bytes) {
        return BitmapFactory.decodeByteArray(
                bytes,
                0,
                bytes.length
        );
    }

    public interface OnUploadedImage {
        void onUploaded();
        void onError();
        void onImageSizeError();
    }

    public void uploadProfilePhoto(String email, Bitmap bitmap, OnUploadedImage onUploadedImage) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        if (data.length < ONE_MEGABYTE) {
            storage.getReference(PROFILE_PHOTOS + "/" + email)
                    .putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> onUploadedImage.onUploaded())
                    .addOnFailureListener(e -> onUploadedImage.onError());
        }
        else onUploadedImage.onImageSizeError();
    }

    public interface OnDeleteImage {
        void onDeleteImage();
        void onError();
    }

    public void deleteProfilePhoto(String email, OnDeleteImage onDeleteImage) {
        storage.getReference(PROFILE_PHOTOS + "/" + email)
                .delete()
                .addOnSuccessListener(unused -> onDeleteImage.onDeleteImage())
                .addOnFailureListener(e -> onDeleteImage.onError());
    }
}

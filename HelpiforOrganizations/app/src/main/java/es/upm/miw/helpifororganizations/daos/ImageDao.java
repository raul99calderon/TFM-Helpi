package es.upm.miw.helpifororganizations.daos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        uploadImage(bitmap, email, PROFILE_PHOTOS, onUploadedImage, ONE_MEGABYTE);
    }

    public void uploadNoticeImage(String key, Bitmap bitmap, OnUploadedImage onUploadedImage) {
        uploadImage(bitmap, key, NOTICE_IMAGES, onUploadedImage, TWO_MEGABYTES);
    }

    private void uploadImage(Bitmap bitmap, String keyOrEmail, String folder, OnUploadedImage onUploadedImage, long limit) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        if (data.length < limit) {
            storage.getReference(folder + "/" + keyOrEmail)
                    .putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> onUploadedImage.onUploaded())
                    .addOnFailureListener(e -> onUploadedImage.onError());
        } else onUploadedImage.onImageSizeError();
    }

    public interface OnDeleteImage {
        void onDeleteImage();
    }

    public void deleteNoticeImage(String key, OnDeleteImage onDeleteImage) {
        StorageReference reference = storage.getReference(NOTICE_IMAGES + "/" + key);
        deleteImage(reference, onDeleteImage);
    }

    public void deleteProfilePhoto(String email, OnDeleteImage onDeleteImage) {
        StorageReference reference = storage.getReference(PROFILE_PHOTOS + "/" + email);
        deleteImage(reference, onDeleteImage);
    }

    private void deleteImage(StorageReference reference, OnDeleteImage onDeleteImage) {
        reference.delete()
                .addOnSuccessListener(unused -> onDeleteImage.onDeleteImage())
                .addOnFailureListener(e -> { });
    }
}

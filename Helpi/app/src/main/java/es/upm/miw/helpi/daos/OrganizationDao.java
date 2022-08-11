package es.upm.miw.helpi.daos;

import android.graphics.Bitmap;

import es.upm.miw.helpi.models.OrganizationItem;
import es.upm.miw.helpi.models.User;

public final class OrganizationDao {

    private static OrganizationDao instance;
    private static UserDao userDao;
    private static ImageDao imageDao;

    public static OrganizationDao getInstance() {
        if (instance == null) {
            instance = new OrganizationDao();
            userDao = UserDao.getInstance();
            imageDao = ImageDao.getInstance();
        }

        return instance;
    }

    public interface OnGetOrganization {
        void onGetOrganization(OrganizationItem organizationItem);
        void onGetOrganizationPhoto(String email, Bitmap bitmap);
    }

    public void getOrganizations(OnGetOrganization onGetOrganization) {
        userDao.getOrganizations(confirmationUserDto -> {
            OrganizationItem organizationItem = new OrganizationItem(
                    confirmationUserDto.getEmail(),
                    confirmationUserDto.getName(),
                    null
            );
            onGetOrganization.onGetOrganization(organizationItem);

            imageDao.getProfilePhoto(organizationItem.getEmail(), image ->
                    onGetOrganization.onGetOrganizationPhoto(organizationItem.getEmail(), image));
        });
    }

    public interface OnGetUserOrganization {
        void onGetOrganization(User user);
        void onGetOrganizationPhoto(Bitmap bitmap);
    }

    public void getOrganization(String email, OnGetUserOrganization onGetUserOrganization) {
        userDao.getUser(email, user -> {
            onGetUserOrganization.onGetOrganization(user);
            imageDao.getProfilePhoto(email, onGetUserOrganization::onGetOrganizationPhoto);
        });
    }
}

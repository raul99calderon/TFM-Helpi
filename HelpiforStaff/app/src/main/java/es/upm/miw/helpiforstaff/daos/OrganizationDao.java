package es.upm.miw.helpiforstaff.daos;

import android.graphics.Bitmap;

import es.upm.miw.helpiforstaff.models.OrganizationItem;
import es.upm.miw.helpiforstaff.models.OrganizationStaff;
import es.upm.miw.helpiforstaff.models.User;

public final class OrganizationDao {

    private static OrganizationDao instance;
    private static UserDao userDao;
    private static ImageDao imageDao;
    private static OrganizationStaffDao organizationStaffDao;

    public static OrganizationDao getInstance() {
        if (instance == null) {
            instance = new OrganizationDao();
            userDao = UserDao.getInstance();
            imageDao = ImageDao.getInstance();
            organizationStaffDao = OrganizationStaffDao.getInstance();
        }

        return instance;
    }

    public interface OnGetOrganizations {
        void onGetOrganization(OrganizationItem item);
        void onRemoveOrganization(String organizationEmail);
        void onGetOrganizationPhoto(String email, Bitmap bitmap);
    }

    public void getOrganizations(String email, OnGetOrganizations onGetOrganizations) {
        organizationStaffDao.getOrganizationStaff(email, new OrganizationStaffDao.OnGetOrganizationStaff() {
            @Override
            public void onGetOrganizationStaff(OrganizationStaff organizationStaff) {
                userDao.getUser(organizationStaff.getOrganizationEmail(), user -> {
                    OrganizationItem organizationItem = new OrganizationItem(
                            organizationStaff.getOrganizationEmail(),
                            user.getName(),
                            null
                    );
                    onGetOrganizations.onGetOrganization(organizationItem);
                });

                imageDao.getProfilePhoto(organizationStaff.getOrganizationEmail(), image ->
                        onGetOrganizations.onGetOrganizationPhoto(organizationStaff.getOrganizationEmail(), image));
            }

            @Override
            public void onRemoveOrganizationStaff(String organizationEmail) {
                onGetOrganizations.onRemoveOrganization(organizationEmail);
            }
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

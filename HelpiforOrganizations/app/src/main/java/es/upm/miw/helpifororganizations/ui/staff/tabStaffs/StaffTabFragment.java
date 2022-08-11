package es.upm.miw.helpifororganizations.ui.staff.tabStaffs;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

import es.upm.miw.helpifororganizations.R;
import es.upm.miw.helpifororganizations.adapters.StaffAdapter;

import es.upm.miw.helpifororganizations.daos.OrganizationStaffDao;
import es.upm.miw.helpifororganizations.daos.UserDao;
import es.upm.miw.helpifororganizations.databinding.FragmentStaffTabBinding;
import es.upm.miw.helpifororganizations.models.OrganizationStaff;
import es.upm.miw.helpifororganizations.models.StaffItem;

public class StaffTabFragment extends Fragment {

    private FragmentStaffTabBinding binding;
    private StaffAdapter adapter;
    private OrganizationStaffDao organizationStaffDao;
    private UserDao userDao;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStaffTabBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewStaff);
        adapter = new StaffAdapter(getContext(), this::deleteStaff);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        organizationStaffDao = OrganizationStaffDao.getInstance();
        userDao = UserDao.getInstance();

        this.loadStaff();
        return root;
    }

    private void loadStaff() {
        adapter.setStaffItems(new ArrayList<>());
        String email = Objects.requireNonNull(FirebaseAuth
                .getInstance()
                .getCurrentUser())
                .getEmail();
        organizationStaffDao.getOrganizationStaff(email, new OrganizationStaffDao.OnGetOrganizationStaff() {
            @Override
            public void onGetOrganizationStaff(OrganizationStaff organizationStaff) {
                userDao.getUser(organizationStaff.getStaffEmail(), user -> {
                    StaffItem staffItem = new StaffItem(
                            organizationStaff.getStaffEmail(),
                            user.getName(),
                            null
                    );
                    adapter.addItem(staffItem);
                });
            }

            @Override
            public void onRemoveOrganizationStaff(String staffEmail) {
                adapter.removeItem(staffEmail);
            }

            @Override
            public void onGetStaffPhoto(String staffEmail, Bitmap bitmap) {
                adapter.setStaffPhoto(staffEmail, bitmap);
            }
        });
    }

    private void deleteStaff(StaffItem item) {
        String organizationEmail = Objects.requireNonNull(FirebaseAuth
                .getInstance()
                .getCurrentUser())
                .getEmail();
        OrganizationStaff organizationStaff = new OrganizationStaff(organizationEmail, item.getEmail());
        organizationStaffDao.deleteStaff(organizationStaff, () ->
                Toast.makeText(
                        getContext(),
                        getString(R.string.staff_deleted),
                        Toast.LENGTH_SHORT
                ).show()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
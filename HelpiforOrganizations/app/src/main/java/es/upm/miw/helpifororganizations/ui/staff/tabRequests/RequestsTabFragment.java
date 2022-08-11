package es.upm.miw.helpifororganizations.ui.staff.tabRequests;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

import es.upm.miw.helpifororganizations.R;
import es.upm.miw.helpifororganizations.adapters.OrganizationStaffRequestAdapter;
import es.upm.miw.helpifororganizations.daos.OrganizationStaffRequestDao;
import es.upm.miw.helpifororganizations.databinding.FragmentRequestsTabBinding;
import es.upm.miw.helpifororganizations.models.OrganizationStaffRequest;

public class RequestsTabFragment extends Fragment {

    private FragmentRequestsTabBinding binding;
    private OrganizationStaffRequestAdapter adapter;
    private OrganizationStaffRequestDao organizationStaffRequestDao;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRequestsTabBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewRequests);
        adapter = new OrganizationStaffRequestAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        organizationStaffRequestDao = OrganizationStaffRequestDao.getInstance();
        loadRequests();

        return root;
    }

    private void loadRequests() {
        adapter.setOrganizationStaffRequests(new ArrayList<>());
        String email = Objects.requireNonNull(FirebaseAuth
                .getInstance()
                .getCurrentUser())
                .getEmail();
        organizationStaffRequestDao.getOrganizationStaffRequests(email, new OrganizationStaffRequestDao.OnGetOrganizationStaffRequest() {
            @Override
            public void onGetOrganizationStaffRequest(OrganizationStaffRequest organizationStaffRequest) {
                adapter.addItem(organizationStaffRequest);
            }

            @Override
            public void onUpdateOrganizationStaffRequest(OrganizationStaffRequest organizationStaffRequest) {
                adapter.updateItem(organizationStaffRequest);
            }

            @Override
            public void onRemoveOrganizationStaffRequest(String staffEmail) {
                adapter.removeItem(staffEmail);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
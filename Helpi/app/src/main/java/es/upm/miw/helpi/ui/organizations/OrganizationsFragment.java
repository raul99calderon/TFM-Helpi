package es.upm.miw.helpi.ui.organizations;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import es.upm.miw.helpi.OrganizationDetailsActivity;
import es.upm.miw.helpi.R;
import es.upm.miw.helpi.adapters.OrganizationAdapter;
import es.upm.miw.helpi.daos.OrganizationDao;
import es.upm.miw.helpi.databinding.FragmentOrganizationsBinding;
import es.upm.miw.helpi.models.OrganizationItem;

public class OrganizationsFragment extends Fragment {

    private FragmentOrganizationsBinding binding;
    private OrganizationAdapter adapter;
    private TextView tvReload;
    private SwipeRefreshLayout swipeOrganizations;
    private OrganizationDao organizationDao;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentOrganizationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tvReload = root.findViewById(R.id.tvReload);
        tvReload.postDelayed(() -> {
            tvReload.setVisibility(View.GONE);
            tvReload.animate()
                    .translationY(-tvReload.getHeight())
                    .alpha(0.0f)
                    .setDuration(300);
        }, 3000);
        swipeOrganizations = root.findViewById(R.id.swipeOrganizations);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewOrganizations);
        adapter = new OrganizationAdapter(getContext(), this::openOrganization);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        organizationDao = OrganizationDao.getInstance();

        this.loadOrganizations();
        swipeOrganizations.setOnRefreshListener(this::loadOrganizations);
        return root;
    }

    private void loadOrganizations() {
        adapter.setOrganizationItems(new ArrayList<>());

        organizationDao.getOrganizations(new OrganizationDao.OnGetOrganization() {
            @Override
            public void onGetOrganization(OrganizationItem organizationItem) {
                adapter.addItem(organizationItem);
            }

            @Override
            public void onGetOrganizationPhoto(String email, Bitmap bitmap) {
                adapter.setPhoto(email, bitmap);
            }
        });
        swipeOrganizations.setRefreshing(false);
    }

    private void openOrganization(OrganizationItem organizationItem) {
        Intent intent = new Intent(getContext(), OrganizationDetailsActivity.class);
        intent.putExtra("email",organizationItem.getEmail());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

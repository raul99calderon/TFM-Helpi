package es.upm.miw.helpi.ui.notices;

import android.content.Intent;
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
import java.util.Collections;
import java.util.List;

import es.upm.miw.helpi.NoticeDetailsActivity;
import es.upm.miw.helpi.R;
import es.upm.miw.helpi.adapters.NoticeAdapter;
import es.upm.miw.helpi.daos.NoticeDao;
import es.upm.miw.helpi.databinding.FragmentNoticesBinding;
import es.upm.miw.helpi.models.NoticeItem;
import es.upm.miw.helpi.models.User;

public class NoticesFragment extends Fragment {

    private FragmentNoticesBinding binding;
    private NoticeAdapter adapter;
    private TextView tvReload;
    private SwipeRefreshLayout swipeNotices;
    private NoticeDao noticeDao;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNoticesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tvReload = root.findViewById(R.id.tvReload);
        tvReload.postDelayed(() -> {
            tvReload.setVisibility(View.GONE);
            tvReload.animate()
                    .translationY(-tvReload.getHeight())
                    .alpha(0.0f)
                    .setDuration(300);
        }, 3000);
        swipeNotices = root.findViewById(R.id.swipeNotices);

        RecyclerView recyclerViewNotices = root.findViewById(R.id.recyclerViewNotices);
        adapter = new NoticeAdapter(getContext(), this::openNotice);
        recyclerViewNotices.setAdapter(adapter);
        recyclerViewNotices.setLayoutManager(new LinearLayoutManager(getContext()));

        noticeDao = NoticeDao.getInstance();
        this.loadNotices();
        swipeNotices.setOnRefreshListener(this::loadNotices);
        return root;
    }

    private void loadNotices() {
        adapter.setItems(new ArrayList<>());
        noticeDao.getNotices(new NoticeDao.OnGetNotices() {
            @Override
            public void onGetNotices(List<NoticeItem> noticeItems) {
                Collections.sort(noticeItems, (t2, t1) -> t1.getDateTime().compareTo(t2.getDateTime()));
                adapter.setItems(noticeItems);
                swipeNotices.setRefreshing(false);
            }

            @Override
            public void onGetOrganizationName(User user) {
                adapter.setOrganizationName(user);
            }

            @Override
            public void onError() {
                swipeNotices.setRefreshing(false);
            }
        });
    }

    private void openNotice(NoticeItem noticeItem) {
        Intent intent = new Intent(getContext(), NoticeDetailsActivity.class);
        intent.putExtra("key", noticeItem.getKey());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
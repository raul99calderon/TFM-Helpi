package es.upm.miw.helpifororganizations.ui.notices;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import es.upm.miw.helpifororganizations.CreateNoticeActivity;
import es.upm.miw.helpifororganizations.NoticeDetailsActivity;
import es.upm.miw.helpifororganizations.R;
import es.upm.miw.helpifororganizations.adapters.NoticeAdapter;
import es.upm.miw.helpifororganizations.daos.NoticeDao;
import es.upm.miw.helpifororganizations.databinding.FragmentNoticesBinding;
import es.upm.miw.helpifororganizations.models.NoticeDto;
import es.upm.miw.helpifororganizations.models.NoticeItem;

public class NoticesFragment extends Fragment {

    private FragmentNoticesBinding binding;
    private NoticeAdapter adapter;
    private NoticeDao noticeDao;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNoticesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerViewNotices = root.findViewById(R.id.recyclerViewNotices);
        adapter = new NoticeAdapter(getContext(), this::openNotice);
        recyclerViewNotices.setAdapter(adapter);
        recyclerViewNotices.setLayoutManager(new LinearLayoutManager(getContext()));

        noticeDao = NoticeDao.getInstance();

        FloatingActionButton fabNew = root.findViewById(R.id.fabNew);
        fabNew.setOnClickListener(view -> startActivity(new Intent(getContext(), CreateNoticeActivity.class)));

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotices();
    }

    private void loadNotices() {
        adapter.setItems(new ArrayList<>());
        String email = Objects.requireNonNull(FirebaseAuth
                .getInstance()
                .getCurrentUser())
                .getEmail();
        noticeDao.loadNotices(email, noticeItems -> {
            Collections.sort(noticeItems, (t2, t1) -> t1.getDateTime().compareTo(t2.getDateTime()));
            adapter.setItems(noticeItems);
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
package es.upm.miw.helpi.ui.ranking;

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

import es.upm.miw.helpi.R;
import es.upm.miw.helpi.adapters.RankingAdapter;
import es.upm.miw.helpi.daos.ImageDao;
import es.upm.miw.helpi.daos.RankingDao;
import es.upm.miw.helpi.daos.UserDao;
import es.upm.miw.helpi.databinding.FragmentRankingBinding;
import es.upm.miw.helpi.models.RankingUser;
import es.upm.miw.helpi.models.RankingUserItem;

public class RankingFragment extends Fragment {

    private FragmentRankingBinding binding;
    private RankingAdapter adapter;
    private TextView tvReload;
    private SwipeRefreshLayout swipeRanking;
    private RankingDao rankingDao;
    private UserDao userDao;
    private ImageDao imageDao;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRankingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tvReload = root.findViewById(R.id.tvReload);
        tvReload.postDelayed(() -> {
            tvReload.setVisibility(View.GONE);
            tvReload.animate()
                    .translationY(-tvReload.getHeight())
                    .alpha(0.0f)
                    .setDuration(300);
        }, 3000);
        swipeRanking = root.findViewById(R.id.swipeRanking);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewRanking);
        adapter = new RankingAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        rankingDao = RankingDao.getInstance();
        userDao = UserDao.getInstance();
        imageDao = ImageDao.getInstance();

        this.loadRanking();
        swipeRanking.setOnRefreshListener(this::loadRanking);
        return root;
    }

    private void loadRanking() {
        adapter.setItems(new ArrayList<>());
        rankingDao.getRanking(rankingUsers -> {
            Collections.sort(rankingUsers, (t2, t1) -> t1.getNumAttendedEvents().compareTo(t2.getNumAttendedEvents()));
            for (RankingUser rankingUser : rankingUsers) {
                RankingUserItem rankingUserItem = new RankingUserItem(
                        rankingUser,
                        null
                );
                adapter.addItem(rankingUserItem);
            }
            swipeRanking.setRefreshing(false);

            for (RankingUser rankingUser : rankingUsers) {
                userDao.getUser(rankingUser.getEmail(), user ->
                        adapter.setName(rankingUser.getEmail(), user.getName()));
                imageDao.getProfilePhoto(rankingUser.getEmail(), image ->
                        adapter.setPhoto(rankingUser.getEmail(), image));
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
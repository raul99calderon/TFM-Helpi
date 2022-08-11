package es.upm.miw.helpi.ui.events.tabEvents;

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

import es.upm.miw.helpi.EventDetailsActivity;
import es.upm.miw.helpi.R;
import es.upm.miw.helpi.adapters.EventAdapter;
import es.upm.miw.helpi.daos.EventDao;
import es.upm.miw.helpi.databinding.FragmentTabEventsBinding;
import es.upm.miw.helpi.models.EventDto;
import es.upm.miw.helpi.models.EventItem;

public class EventsTabFragment extends Fragment {

    private FragmentTabEventsBinding binding;
    private EventAdapter adapter;
    private TextView tvReload;
    private SwipeRefreshLayout swipeEvents;
    private EventDao eventDao;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTabEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tvReload = root.findViewById(R.id.tvReload);
        tvReload.postDelayed(() -> {
            tvReload.setVisibility(View.GONE);
            tvReload.animate()
                    .translationY(-tvReload.getHeight())
                    .alpha(0.0f)
                    .setDuration(300);
        }, 3000);
        swipeEvents = root.findViewById(R.id.swipeEvents);

        RecyclerView recyclerViewEvents = root.findViewById(R.id.recyclerViewEvents);
        adapter = new EventAdapter(getContext(), this::openEventDetails);
        recyclerViewEvents.setAdapter(adapter);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        eventDao = EventDao.getInstance();
        this.loadEvents();
        swipeEvents.setOnRefreshListener(this::loadEvents);
        return root;
    }

    private void loadEvents() {
        adapter.setEvents(new ArrayList<>());
        eventDao.getEvents(new EventDao.OnGetEvent() {
            @Override
            public void onEventGet(EventItem eventItem) {
                adapter.addItem(eventItem);
            }

            @Override
            public void onError() {
                swipeEvents.setRefreshing(false);
            }
        });
        swipeEvents.setRefreshing(false);
    }

    private void openEventDetails(EventDto eventDto) {
        Intent intent = new Intent(getContext(), EventDetailsActivity.class);
        intent.putExtra("key", eventDto.getKey());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package es.upm.miw.helpi.ui.events.tabAttended;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import es.upm.miw.helpi.EventDetailsActivity;
import es.upm.miw.helpi.R;
import es.upm.miw.helpi.adapters.EventAdapter;
import es.upm.miw.helpi.daos.EventDao;
import es.upm.miw.helpi.databinding.FragmentTabAttendedBinding;
import es.upm.miw.helpi.models.EventDto;
import es.upm.miw.helpi.models.EventItem;

public class AttendedTabFragment extends Fragment {

    private FragmentTabAttendedBinding binding;
    private EventAdapter adapter;
    private EventDao eventDao;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTabAttendedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerViewEvents = root.findViewById(R.id.recyclerViewAttended);
        adapter = new EventAdapter(getContext(), this::openEventDetails);
        recyclerViewEvents.setAdapter(adapter);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        eventDao = EventDao.getInstance();

        this.loadRequests();

        return root;
    }

    private void loadRequests() {
        adapter.setEvents(new ArrayList<>());
        eventDao.getAttendedEvents(new EventDao.OnResultGetAttendedEvents() {
            @Override
            public void onChildAttended(EventItem eventItem) {
                adapter.addItem(eventItem);
            }

            @Override
            public void onChildRemoved(String key) {
                adapter.removeItem(key);
            }
        });
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

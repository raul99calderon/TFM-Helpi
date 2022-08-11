package es.upm.miw.helpifororganizations.ui.events;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

import es.upm.miw.helpifororganizations.CreateUpdateEventActivity;
import es.upm.miw.helpifororganizations.EventDetailsActivity;
import es.upm.miw.helpifororganizations.R;
import es.upm.miw.helpifororganizations.adapters.EventAdapter;
import es.upm.miw.helpifororganizations.daos.EventDao;
import es.upm.miw.helpifororganizations.databinding.FragmentEventsBinding;
import es.upm.miw.helpifororganizations.models.EventDto;

public class EventsFragment extends Fragment {

    private FragmentEventsBinding binding;
    private EventAdapter adapter;
    private EventDao eventDao;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerViewEvents = root.findViewById(R.id.recyclerViewEvents);
        adapter = new EventAdapter(getContext(), this::openEventDetails);
        recyclerViewEvents.setAdapter(adapter);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton fab = root.findViewById(R.id.fabEvent);
        fab.setOnClickListener(view -> startActivity(new Intent(getContext(), CreateUpdateEventActivity.class)));

        eventDao = EventDao.getInstance();
        this.loadEvents();
        return root;
    }

    private void openEventDetails(EventDto eventDto) {
        Intent intent = new Intent(getContext(), EventDetailsActivity.class);
        intent.putExtra("key", eventDto.getKey());
        startActivity(intent);
    }

    private void loadEvents() {
        adapter.setEvents(new ArrayList<>());
        String email = Objects.requireNonNull(FirebaseAuth
                .getInstance()
                .getCurrentUser())
                .getEmail();
        eventDao.getEvents(email, new EventDao.OnGetOrganizationEvent() {
            @Override
            public void onGetEvent(EventDto eventDto) {
                adapter.addItem(eventDto);
            }

            @Override
            public void onRemoveEvent(String key) {
                adapter.removeItem(key);
            }

            @Override
            public void onUpdatedEvent(EventDto eventDto) {
                adapter.updateItem(eventDto);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
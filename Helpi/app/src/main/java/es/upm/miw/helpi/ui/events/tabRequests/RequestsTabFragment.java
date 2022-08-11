package es.upm.miw.helpi.ui.events.tabRequests;

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
import es.upm.miw.helpi.adapters.EventRequestAdapter;
import es.upm.miw.helpi.daos.EventRequestDao;
import es.upm.miw.helpi.databinding.FragmentTabRequestsBinding;
import es.upm.miw.helpi.models.EventRequest;
import es.upm.miw.helpi.models.EventRequestItem;
import es.upm.miw.helpi.models.JoinRequestState;

public class RequestsTabFragment extends Fragment {

    private FragmentTabRequestsBinding binding;
    private EventRequestAdapter adapter;
    private EventRequestDao eventRequestDao;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTabRequestsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerViewEvents = root.findViewById(R.id.recyclerViewRequests);
        adapter = new EventRequestAdapter(getContext(), this::openEventDetails);
        recyclerViewEvents.setAdapter(adapter);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        eventRequestDao = EventRequestDao.getInstance();
        this.loadRequests();

        return root;
    }

    private void loadRequests() {
        adapter.setEvents(new ArrayList<>());
        eventRequestDao.getEventRequests(new EventRequestDao.OnEventRequests() {
            @Override
            public void onChildAdded(EventRequestItem eventRequestItem) {
                if (!eventRequestItem.getState().equals(JoinRequestState.ATTENDED))
                    adapter.addItem(eventRequestItem);
            }

            @Override
            public void onChildChanged(EventRequest eventRequest, String key) {
                if (eventRequest.getState().equals(JoinRequestState.ATTENDED))
                    adapter.removeItem(key);
                else
                    adapter.updateState(eventRequest.getState(), key);
            }

            @Override
            public void onChildRemoved(String key) {
                adapter.removeItem(key);
            }
        });
    }

    private void openEventDetails(EventRequestItem item) {
        Intent intent = new Intent(getContext(), EventDetailsActivity.class);
        intent.putExtra("key", item.getEventKey());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

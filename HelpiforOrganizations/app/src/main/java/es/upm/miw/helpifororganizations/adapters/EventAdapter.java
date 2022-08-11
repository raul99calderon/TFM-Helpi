package es.upm.miw.helpifororganizations.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import es.upm.miw.helpifororganizations.R;
import es.upm.miw.helpifororganizations.models.EventDto;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(EventDto eventDto);
    }

    private List<EventDto> items;
    private final LayoutInflater mInflater;
    private final OnItemClickListener listener;

    public EventAdapter(Context context, OnItemClickListener listener) {
        mInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setEvents(List<EventDto> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addItem(EventDto event) {
        this.items.add(event);
        notifyItemInserted(this.items.size() - 1);
    }

    public void removeItem(String key) {
        for (int i = 0; i < items.size() ; i++) {
            if (this.items.get(i).getKey().equals(key)) {
                this.items.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void updateItem(EventDto eventDto) {
        for (int i = 0; i < items.size() ; i++) {
            if (this.items.get(i).getKey().equals(eventDto.getKey())) {
                this.items.set(i, eventDto);
                notifyItemChanged(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_event, parent, false);
        return new EventAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        holder.bindListener(items.get(position), this.listener);
    }

    @Override
    public int getItemCount() {
        return (items == null) ? 0 : items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvEventName, tvLocation, tvDateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
        }

        public void bindListener(EventDto item, OnItemClickListener listener) {
            tvEventName.setText(item.getName());
            tvLocation.setText(item.getLocation().getPlace());
            @SuppressLint("SimpleDateFormat")
            String formattedDateTime = new SimpleDateFormat(itemView.getContext()
                    .getString(R.string.date_time_format))
                    .format(new Timestamp(item.getDateTime()));
            tvDateTime.setText(formattedDateTime);
            itemView.setOnClickListener(view -> listener.onItemClick(item));
        }
    }
}

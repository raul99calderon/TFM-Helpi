package es.upm.miw.helpi.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import es.upm.miw.helpi.R;
import es.upm.miw.helpi.models.EventRequestItem;
import es.upm.miw.helpi.models.JoinRequestState;

public class EventRequestAdapter extends RecyclerView.Adapter<EventRequestAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(EventRequestItem item);
    }

    private List<EventRequestItem> items;
    private final LayoutInflater mInflater;
    private final OnItemClickListener listener;

    public EventRequestAdapter(Context context, OnItemClickListener listener) {
        mInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setEvents(List<EventRequestItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addItem(EventRequestItem item) {
        this.items.add(item);
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

    public void updateState(JoinRequestState state, String key) {
        for (int i = 0; i < items.size() ; i++) {
            if (this.items.get(i).getKey().equals(key)) {
                this.items.get(i).setState(state);
                notifyItemChanged(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_event, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindListener(items.get(position), this.listener);
    }

    @Override
    public int getItemCount() {
        return (items == null) ? 0 : items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvEventName,tvOrganizationName, tvLocation, tvDateTime;
        private final ConstraintLayout clItemJoinEvent;
        private final ImageView ivResponse;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvOrganizationName = itemView.findViewById(R.id.tvOrganizationName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            clItemJoinEvent = itemView.findViewById(R.id.clItemEvent);
            ivResponse = itemView.findViewById(R.id.ivResponse);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void bindListener(EventRequestItem item, OnItemClickListener listener) {
            tvEventName.setText(item.getEvent().getName());
            tvOrganizationName.setText(item.getEvent().getOrganizationName());
            tvLocation.setText(item.getEvent().getLocation().getPlace());
            @SuppressLint("SimpleDateFormat")
            String formattedDateTime = new SimpleDateFormat(itemView.getContext()
                    .getString(R.string.date_time_format))
                    .format(new Timestamp(item.getDateTime()));
            tvDateTime.setText(formattedDateTime);
            switch (item.getState()) {
                case ACCEPTED:
                    tvEventName.setTextColor(itemView.getContext().getColor(R.color.green_1));
                    clItemJoinEvent.setBackground(itemView.getContext()
                            .getDrawable(R.drawable.round_bg_green));
                    ivResponse.setImageResource(R.drawable.ic_baseline_check_24);
                    ivResponse.setVisibility(View.VISIBLE);
                    break;
                case PENDING:
                    tvEventName.setTextColor(itemView.getContext().getColor(R.color.grey_1));
                    clItemJoinEvent.setBackground(itemView.getContext()
                            .getDrawable(R.drawable.round_bg_grey));
                    ivResponse.setImageResource(R.drawable.ic_baseline_clock_24);
                    ivResponse.setVisibility(View.VISIBLE);
                    break;
                case DENIED:
                    tvEventName.setTextColor(itemView.getContext().getColor(R.color.red_1));
                    clItemJoinEvent.setBackground(itemView.getContext()
                            .getDrawable(R.drawable.round_bg_red));
                    ivResponse.setImageResource(R.drawable.ic_baseline_close_24);
                    ivResponse.setVisibility(View.VISIBLE);
                    break;
            }
            itemView.setOnClickListener(view -> listener.onItemClick(item));
        }
    }
}

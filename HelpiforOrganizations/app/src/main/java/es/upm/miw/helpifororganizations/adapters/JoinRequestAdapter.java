package es.upm.miw.helpifororganizations.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.upm.miw.helpifororganizations.R;
import es.upm.miw.helpifororganizations.models.EventJoinRequestItem;

public class JoinRequestAdapter extends RecyclerView.Adapter<JoinRequestAdapter.ViewHolder> {

    public interface OnButtonClickListener {
        void onAcceptButtonClick(EventJoinRequestItem eventJoinRequest);
        void onDenyButtonClick(EventJoinRequestItem eventJoinRequest);
    }

    private List<EventJoinRequestItem> items;
    private final LayoutInflater mInflater;
    private final OnButtonClickListener listener;

    public JoinRequestAdapter(Context context, OnButtonClickListener listener) {
        mInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRequests(List<EventJoinRequestItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addItem(EventJoinRequestItem request) {
        this.items.add(request);
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

    public void updateItem(EventJoinRequestItem item) {
        for (int i = 0; i < items.size() ; i++) {
            if (this.items.get(i).getKey().equals(item.getKey())) {
                this.items.get(i).setState(item.getState());
                notifyItemChanged(i);
                break;
            }
        }
    }

    public List<EventJoinRequestItem> getItems() {
        return this.items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_event_request, parent, false);
        return new JoinRequestAdapter.ViewHolder(itemView);
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
        private final TextView tvEmail;
        private final ImageButton btnAccept, btnDeny;
        private final ImageView ivState;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDeny = itemView.findViewById(R.id.btnDeny);
            ivState = itemView.findViewById(R.id.ivState);
        }

        public void bindListener(EventJoinRequestItem item, OnButtonClickListener listener) {
            tvEmail.setText(item.getUserEmail());
            btnAccept.setVisibility(View.GONE);
            btnDeny.setVisibility(View.GONE);

            switch (item.getState()) {
                case ACCEPTED:
                    ivState.setImageResource(R.drawable.ic_baseline_check_24);
                    break;
                case PENDING:
                    ivState.setImageResource(R.drawable.ic_baseline_clock_24);
                    btnAccept.setOnClickListener(view -> listener.onAcceptButtonClick(item));
                    btnDeny.setOnClickListener(view -> listener.onDenyButtonClick(item));
                    btnAccept.setVisibility(View.VISIBLE);
                    btnDeny.setVisibility(View.VISIBLE);
                    break;
                case DENIED:
                    ivState.setImageResource(R.drawable.ic_baseline_close_24);
                    break;
                case ATTENDED:
                    ivState.setImageResource(R.drawable.ic_baseline_stars_24);
                    break;
            }
        }
    }
}

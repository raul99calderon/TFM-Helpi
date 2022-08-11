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
import es.upm.miw.helpifororganizations.models.NoticeItem;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(NoticeItem item);
    }

    private List<NoticeItem> items;
    private final LayoutInflater mInflater;
    private final OnItemClickListener listener;

    public NoticeAdapter(Context context, OnItemClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<NoticeItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_notice, parent, false);
        return new NoticeAdapter.ViewHolder(itemView);
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

        private final TextView tvTitle, tvDateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
        }

        public void bindListener(NoticeItem item, OnItemClickListener listener) {
            tvTitle.setText(item.getTitle());
            @SuppressLint("SimpleDateFormat")
            String formattedDateTime = new SimpleDateFormat(itemView.getContext()
                    .getString(R.string.date_time_format))
                    .format(new Timestamp(item.getDateTime()));
            tvDateTime.setText(formattedDateTime);
            itemView.setOnClickListener(view -> listener.onItemClick(item));
        }
    }
}

package es.upm.miw.helpifororganizations.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
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
import es.upm.miw.helpifororganizations.models.StaffItem;


public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder> {


    public interface OnItemClickListener {
        void onItemClick(StaffItem item);
    }

    private List<StaffItem> items;
    private final LayoutInflater mInflater;
    private final OnItemClickListener listener;

    public StaffAdapter(Context context, OnItemClickListener listener) {
        mInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setStaffItems(List<StaffItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addItem(StaffItem staffItem) {
        this.items.add(staffItem);
        notifyItemInserted(this.items.size() - 1);
    }

    public void removeItem(String staffEmail) {
        for (int i = 0; i < items.size() ; i++) {
            if (this.items.get(i).getEmail().equals(staffEmail)) {
                this.items.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void setStaffPhoto(String staffEmail, Bitmap photo) {
        for (int i = 0; i < items.size() ; i++) {
            if (this.items.get(i).getEmail().equals(staffEmail)) {
                this.items.get(i).setPhoto(photo);
                notifyItemChanged(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_staff, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindListener(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return (items == null) ? 0 : items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName, tvEmail;
        private final ImageView ivPhoto;
        private final ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bindListener(StaffItem item, OnItemClickListener listener) {
            tvName.setText(item.getName());
            tvEmail.setText(item.getEmail());
            if (item.getPhoto() != null) {
                ivPhoto.setImageBitmap(item.getPhoto());
            }
            btnDelete.setOnClickListener(view -> listener.onItemClick(item));
        }
    }
}

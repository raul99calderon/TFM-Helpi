package es.upm.miw.helpi.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.upm.miw.helpi.R;
import es.upm.miw.helpi.models.OrganizationItem;


public class OrganizationAdapter extends RecyclerView.Adapter<OrganizationAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(OrganizationItem item);
    }

    private List<OrganizationItem> items;
    private final LayoutInflater mInflater;
    private final OnItemClickListener listener;

    public OrganizationAdapter(Context context, OnItemClickListener listener) {
        mInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setOrganizationItems(List<OrganizationItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addItem(OrganizationItem organizationItem) {
        this.items.add(organizationItem);
        notifyItemInserted(this.items.size() - 1);
    }

    public void setPhoto(String organizationEmail, Bitmap photo) {
        for (int i = 0;i<items.size();i++) {
            if (items.get(i).getEmail().equals(organizationEmail)) {
                items.get(i).setPhoto(photo);
                notifyItemChanged(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_organization, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindListener(items.get(position),listener);
    }

    @Override
    public int getItemCount() {
        return (items == null) ? 0 : items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final ImageView ivPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
        }

        public void bindListener(OrganizationItem item, final OnItemClickListener listener) {
            itemView.setOnClickListener(view -> listener.onItemClick(item));
            tvName.setText(item.getName());
            if (item.getPhoto() != null) {
                ivPhoto.setImageBitmap(item.getPhoto());
            }
        }
    }
}

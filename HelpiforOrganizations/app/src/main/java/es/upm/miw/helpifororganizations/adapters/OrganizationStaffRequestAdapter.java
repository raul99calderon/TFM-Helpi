package es.upm.miw.helpifororganizations.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.upm.miw.helpifororganizations.R;
import es.upm.miw.helpifororganizations.models.OrganizationStaffRequest;

public class OrganizationStaffRequestAdapter extends RecyclerView.Adapter<OrganizationStaffRequestAdapter.ViewHolder> {

    private List<OrganizationStaffRequest> items;
    private final LayoutInflater mInflater;

    public OrganizationStaffRequestAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setOrganizationStaffRequests(List<OrganizationStaffRequest> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addItem(OrganizationStaffRequest item) {
        this.items.add(item);
        notifyItemInserted(this.items.size() - 1);
    }

    public void removeItem(String staffEmail) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getStaffEmail().equals(staffEmail)) {
                items.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void updateItem(OrganizationStaffRequest organizationStaffRequest) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getStaffEmail().equals(organizationStaffRequest.getStaffEmail())) {
                items.get(i).setConfirmed(organizationStaffRequest.isConfirmed());
                notifyItemChanged(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public OrganizationStaffRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_organization_staff_request, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizationStaffRequestAdapter.ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return (items == null) ? 0 : items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvEmail;
        private final ImageView ivConfirmed;

        public ViewHolder(View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            ivConfirmed = itemView.findViewById(R.id.ivConfirmed);
        }

        public void bind(OrganizationStaffRequest item) {
            tvEmail.setText(item.getStaffEmail());
            if (item.isConfirmed())
                ivConfirmed.setImageResource(R.drawable.ic_baseline_check_24);
            else
                ivConfirmed.setImageResource(R.drawable.ic_baseline_clock_24);
        }
    }
}

package es.upm.miw.helpiforstaff.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.upm.miw.helpiforstaff.R;
import es.upm.miw.helpiforstaff.models.OrganizationStaffRequestDto;

public class OrganizationStaffRequestDtoAdapter extends RecyclerView.Adapter<OrganizationStaffRequestDtoAdapter.ViewHolder> {

    public interface OnButtonClickListener {
        void onItemClick(OrganizationStaffRequestDto item);
    }

    private List<OrganizationStaffRequestDto> items;
    private final LayoutInflater mInflater;
    private final OnButtonClickListener listener;

    public OrganizationStaffRequestDtoAdapter(Context context, OnButtonClickListener listener) {
        mInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setOrganizationStaffRequestDtos(List<OrganizationStaffRequestDto> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addItem(OrganizationStaffRequestDto item) {
        this.items.add(item);
        notifyItemInserted(this.items.size() - 1);
    }

    public void removeItem(String email) {
        for (int i = 0; i < items.size(); i++) {
            if (this.items.get(i).getOrganizationEmail().equals(email)) {
                this.items.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void updateItem(OrganizationStaffRequestDto item) {
        for (int i = 0; i < items.size(); i++) {
            if (this.items.get(i).getOrganizationEmail().equals(item.getOrganizationEmail())) {
                this.items.get(i).setConfirmed(item.isConfirmed());
                notifyItemChanged(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_organization_staff_request, parent, false);
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
        private final TextView tvEmail;
        private final ImageView ivCheck;
        private final Button btnConfirm;

        public ViewHolder(View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            ivCheck = itemView.findViewById(R.id.ivCheck);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
        }

        public void bindListener(OrganizationStaffRequestDto item, final OnButtonClickListener listener) {
            tvEmail.setText(item.getOrganizationEmail());
            if (item.isConfirmed()) {
                btnConfirm.setVisibility(View.GONE);
                ivCheck.setVisibility(View.VISIBLE);
            } else {
                ivCheck.setVisibility(View.GONE);
                btnConfirm.setVisibility(View.VISIBLE);
            }

            btnConfirm.setOnClickListener(view -> listener.onItemClick(item));
        }
    }
}

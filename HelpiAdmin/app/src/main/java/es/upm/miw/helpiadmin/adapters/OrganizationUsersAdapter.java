package es.upm.miw.helpiadmin.adapters;

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

import es.upm.miw.helpiadmin.R;
import es.upm.miw.helpiadmin.models.ConfirmationUserDto;

public class OrganizationUsersAdapter extends RecyclerView.Adapter<OrganizationUsersAdapter.ViewHolder>{

    public interface OnButtonClickListener {
        void onItemClick(ConfirmationUserDto item);
    }

    private final OnButtonClickListener listener;
    private List<ConfirmationUserDto> items;
    private final LayoutInflater mInflater;

    public OrganizationUsersAdapter(Context context, OnButtonClickListener listener) {
        mInflater = LayoutInflater.from(context);
        this.listener = listener;
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setConfirmationUsers(List<ConfirmationUserDto> confirmationUserDtos) {
        this.items = confirmationUserDtos;
        notifyDataSetChanged();
    }

    public void addItem(ConfirmationUserDto confirmationUserDto) {
        this.items.add(confirmationUserDto);
        notifyItemInserted(this.items.size() - 1);
    }

    public void removeItem(String organizationEmail) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getEmail().equals(organizationEmail)) {
                items.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void updateItem(ConfirmationUserDto confirmationUserDto) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getEmail().equals(confirmationUserDto.getEmail())) {
                items.get(i).setConfirmed(confirmationUserDto.isConfirmed());
                notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return (items == null) ? 0 : items.size();
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

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvEmail, tvName;
        private final Button btnConfirm;
        private final ImageView ivCheck;

        public ViewHolder(View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvName = itemView.findViewById(R.id.tvName);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            ivCheck = itemView.findViewById(R.id.ivCheck);
        }

        public void bindListener(final ConfirmationUserDto item, final OnButtonClickListener listener) {
            this.tvEmail.setText(item.getEmail());
            this.tvName.setText(item.getName());
            if (item.isConfirmed()) {
                btnConfirm.setVisibility(View.GONE);
                ivCheck.setVisibility(View.VISIBLE);
            }
            else {
                ivCheck.setVisibility(View.GONE);
                btnConfirm.setVisibility(View.VISIBLE);
            }

            btnConfirm.setOnClickListener(view -> listener.onItemClick(item));
        }
    }
}

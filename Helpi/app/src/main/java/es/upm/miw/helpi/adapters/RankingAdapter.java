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
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.upm.miw.helpi.R;
import es.upm.miw.helpi.models.RankingUserItem;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private List<RankingUserItem> items;
    private final LayoutInflater mInflater;

    public RankingAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<RankingUserItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addItem(RankingUserItem user) {
        this.items.add(user);
        notifyItemInserted(this.items.size() - 1);
    }

    public void setPhoto(String email, Bitmap photo) {
        for (int i = 0 ; i<items.size();i++) {
            if (items.get(i).getEmail().equals(email)) {
                items.get(i).setPhoto(photo);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void setName(String email, String name) {
        for (int i = 0 ; i<items.size();i++) {
            if (items.get(i).getEmail().equals(email)) {
                items.get(i).setName(name);
                notifyItemChanged(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_ranking, parent, false);
        return new RankingAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), position+1);
    }

    @Override
    public int getItemCount() {
        return (items == null) ? 0 : items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName, tvEmail, tvPosition, tvNumEventsAttended;
        private final ImageView ivPosition, ivPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            tvNumEventsAttended = itemView.findViewById(R.id.tvNumEventsAttended);
            ivPosition = itemView.findViewById(R.id.ivPosition);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
        }

        public void bind(RankingUserItem user, int position) {
            if (user.getName() != null)
                tvName.setText(user.getName());
            else tvName.setText("");
            tvNumEventsAttended.setText(String.valueOf(user.getNumAttendedEvents()));
            tvEmail.setText(user.getEmail());
            if (user.getPhoto() != null)
                ivPhoto.setImageBitmap(user.getPhoto());
            switch (position) {
                case 1:
                    ivPosition.setImageDrawable(AppCompatResources
                        .getDrawable(itemView.getContext(), R.drawable.gold));
                    ivPosition.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    ivPosition.setImageDrawable(AppCompatResources
                            .getDrawable(itemView.getContext(), R.drawable.silver));
                    ivPosition.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    ivPosition.setImageDrawable(AppCompatResources
                            .getDrawable(itemView.getContext(), R.drawable.bronze));
                    ivPosition.setVisibility(View.VISIBLE);
                    break;
                default:
                    tvPosition.setVisibility(View.VISIBLE);
                    tvPosition.setText(String.valueOf(position));
            }

        }
    }
}

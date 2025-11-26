package com.example.anime_watchlist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.anime_watchlist.models.Anime;

import java.util.List;

public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder> {

    private List<Anime> animeList;
    private int layoutId;
    private Context context;

    public AnimeAdapter(List<Anime> list, int layoutId) {
        this.animeList = list;
        this.layoutId = layoutId;
    }

    public void updateData(List<Anime> newList) {
        animeList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new AnimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeViewHolder holder, int position) {
        Anime anime = animeList.get(position);
        
        if (holder.title != null) holder.title.setText(anime.getTitle());
        
        if (holder.desc != null) {
            if (anime.getEpisodes() != null) {
                holder.desc.setText("Ep: " + anime.getEpisodes());
            } else {
                 holder.desc.setText("");
            }
        }

        if (holder.image != null && anime.getImages() != null && anime.getImages().getJpg() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(anime.getImages().getJpg().getImage_url())
                    .into(holder.image);
        }

        // Handle Click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AnimeDetailsActivity.class);
            intent.putExtra("anime_data", anime);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return animeList.size(); }

    class AnimeViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, desc;

        AnimeViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.animeImage);
            title = itemView.findViewById(R.id.animeTitle);
            desc = itemView.findViewById(R.id.animeDesc);
        }
    }
}

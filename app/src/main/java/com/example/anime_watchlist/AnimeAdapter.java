package com.example.anime_watchlist;

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

    public AnimeAdapter(List<Anime> list) { animeList = list; }

    public void updateData(List<Anime> newList) {
        animeList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_anime, parent, false);
        return new AnimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeViewHolder holder, int position) {
        Anime anime = animeList.get(position);
        holder.title.setText(anime.getTitle());
        holder.desc.setText("Episodes: " + anime.getEpisodes() + " | Score: " + anime.getScore());

        Glide.with(holder.itemView.getContext())
                .load(anime.getImages().getJpg().getImage_url())
                .into(holder.image);
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

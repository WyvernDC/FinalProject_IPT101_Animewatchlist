package com.example.anime_watchlist;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anime_watchlist.models.Genre;

import java.util.ArrayList;
import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {

    private List<Genre> genres;
    private List<Genre> selectedGenres = new ArrayList<>();
    private OnGenreSelectedListener listener;

    public interface OnGenreSelectedListener {
        void onSelectionChanged(List<Genre> selectedGenres);
    }

    public GenreAdapter(List<Genre> genres, OnGenreSelectedListener listener) {
        this.genres = genres;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_genre, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.bind(genre, selectedGenres.contains(genre));
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public void updateGenres(List<Genre> newGenres) {
        this.genres = newGenres;
        notifyDataSetChanged();
    }

    public void clearSelection() {
        selectedGenres.clear();
        notifyDataSetChanged();
        if (listener != null) listener.onSelectionChanged(selectedGenres);
    }

    public void selectGenreById(int malId) {
        for (Genre g : genres) {
            if (g.getMal_id() == malId) {
                if (!selectedGenres.contains(g)) {
                    selectedGenres.add(g);
                    notifyDataSetChanged();
                    if (listener != null) listener.onSelectionChanged(selectedGenres);
                }
                return;
            }
        }
    }

    class GenreViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_genre_name);
            itemView.setOnClickListener(v -> {
                Genre genre = genres.get(getAdapterPosition());
                if (selectedGenres.contains(genre)) {
                    selectedGenres.remove(genre);
                } else {
                    selectedGenres.add(genre);
                }
                notifyItemChanged(getAdapterPosition());
                if (listener != null) listener.onSelectionChanged(selectedGenres);
            });
        }

        public void bind(Genre genre, boolean isSelected) {
            tvName.setText(genre.getName());
            if (isSelected) {
                tvName.setBackgroundResource(R.drawable.bg_genre_selected);
                tvName.setTextColor(Color.WHITE);
            } else {
                tvName.setBackgroundResource(R.drawable.bg_genre_unselected);
                tvName.setTextColor(Color.parseColor("#CCCCCC"));
            }
        }
    }
}

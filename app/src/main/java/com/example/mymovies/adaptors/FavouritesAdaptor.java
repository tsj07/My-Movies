package com.example.mymovies.adaptors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymovies.R;
import com.example.mymovies.room.MoviesEntity;

import java.util.List;

public class FavouritesAdaptor extends RecyclerView.Adapter<FavouritesAdaptor.SearchViewHolder> {
    List<MoviesEntity> list;
    Context context;
    MoviesEntity entity;
    OnItemClick listener;

    public FavouritesAdaptor(Context context, OnItemClick listener) {
        this.context = context;
        this.listener = listener;
    }

    public interface OnItemClick {
        void onDelete(MoviesEntity entity, int pos);
        void onItemViewClick(String name, int pos);
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.favourites_rv, parent, false),
                listener
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, @SuppressLint("RecyclerView") int position) {
        entity = list.get(position);
        holder.title.setText(entity.getmName());
        holder.year.setText(entity.getmYear());
        if (!entity.getmPoster().equals("N/A")) {
            holder.setImage(entity.getmPoster());
        } else
            holder.poster.setImageResource(R.drawable.img_3);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                entity = list.get(position);
                listener.onDelete(entity, position);
                notifyDataSetChanged();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<MoviesEntity> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView poster, delete;
        TextView title, year;
        RelativeLayout rlFavorites;

        public SearchViewHolder(@NonNull View itemView, OnItemClick listener) {
            super(itemView);
            poster = itemView.findViewById(R.id.ivPoster2);
            title = itemView.findViewById(R.id.tvTitle2);
            year = itemView.findViewById(R.id.tvYear2);
            delete = itemView.findViewById(R.id.ivDelete);
            rlFavorites = itemView.findViewById(R.id.rlFav);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listener.onItemViewClick(title.getText().toString().trim(), pos);
                        }
                    }
                }
            });

        }

        public void setImage(String URL) {
            Glide.with(itemView.getContext()).load(URL).into(poster);
        }

    }

}

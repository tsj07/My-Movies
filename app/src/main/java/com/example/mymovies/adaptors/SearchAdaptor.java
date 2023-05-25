package com.example.mymovies.adaptors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymovies.R;
import com.example.mymovies.model.Search;
import com.example.mymovies.room.MoviesEntity;

import java.util.List;

public class SearchAdaptor extends RecyclerView.Adapter<SearchAdaptor.SearchViewHolder> {
    List<Search> list;
    Context context;
    OnItemClick listener;
    List<MoviesEntity> favMovies;

    public SearchAdaptor(List<MoviesEntity> moviesList, List<Search> list, Context context, OnItemClick listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
        this.favMovies = moviesList;
    }

    public interface OnItemClick {
        void onItemViewClick(Search model, int pos);
        void onAdd(Search model, int pos);
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.search_rv, parent, false),
                listener
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Search model = list.get(position);
        holder.title.setText(model.getTitle());
        holder.year.setText(model.getYear());
        String ImdbID = model.getImdbID();
        if (!model.getPoster().equals("N/A")) {
            holder.setImage(model.getPoster());
        } else
            holder.poster.setImageResource(R.drawable.img_3);

        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAdd(model, position);
                if (model.isFavourite()) {
                    holder.favorite.setImageResource(R.drawable.favorite_red);
                    Toast.makeText(holder.favorite.getContext(), "Added to favourites", Toast.LENGTH_SHORT).show();
                } else {
                    holder.favorite.setImageResource(R.drawable.ic_favourite);
                    Toast.makeText(holder.favorite.getContext(), "Removed from favourites", Toast.LENGTH_SHORT).show();
                }
            }
        });

        for(int i = 0; i<favMovies.size(); i++){
            String favMovieID =  favMovies.get(i).imdbID;
            if(ImdbID.equalsIgnoreCase(favMovieID)){
                holder.favorite.setImageResource(R.drawable.favorite_red);
                break;
            }else {
                holder.favorite.setImageResource(R.drawable.ic_favourite);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView poster, favorite;
        TextView title, year;

        public SearchViewHolder(@NonNull View itemView, OnItemClick listener) {
            super(itemView);
            poster = itemView.findViewById(R.id.ivPoster);
            title = itemView.findViewById(R.id.tvTitle);
            year = itemView.findViewById(R.id.tvYear);
            favorite = itemView.findViewById(R.id.ivFavoriteSearch);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int pos = getAbsoluteAdapterPosition();
                        Search model = new Search();
                        model.setTitle(title.getText().toString());
                        model.setYear(year.getText().toString());
                        model.setPoster(poster.toString());
                        if (pos != RecyclerView.NO_POSITION)
                            listener.onItemViewClick(model, pos);
                    }
                }
            });
        }

        public void setImage(String URL) {
            Glide.with(itemView.getContext()).load(URL).into(poster);
        }

    }

}

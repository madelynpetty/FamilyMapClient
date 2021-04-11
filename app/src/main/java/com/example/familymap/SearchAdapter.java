package com.example.familymap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.IconDrawable;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private ArrayList<ListItemData> results;
    private Context context;

    public SearchAdapter(ArrayList<ListItemData> results, Context context) {
        this.results = results;
        this.context = context;
    }

    public void filterList(ArrayList<ListItemData> filterList) {
        results = filterList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        ListItemData modal = results.get(position);
        holder.name.setText(modal.name);
        holder.description.setText(modal.description);
        holder.image.setImageDrawable(modal.image);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name, description;
        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.search_name);
            description = itemView.findViewById(R.id.search_description);
            image = itemView.findViewById(R.id.searchActivityImageField);
            //((ImageView) findViewById(R.id.ImageField)).setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_male).colorRes(R.color.teal_200).sizeDp(40));
        }
    }
}

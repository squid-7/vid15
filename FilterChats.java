package com.yourpackagename.adapters;

import android.widget.Filter;
import com.yourpackagename.models.ModelChats;
import java.util.ArrayList;

/**
 * Filter implementation to search chats by user name (case-insensitive).
 */
public class FilterChats extends Filter {
    private AdapterChats adapter;
    private ArrayList<ModelChats> filterList;

    public FilterChats(AdapterChats adapter, ArrayList<ModelChats> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0) {
            String search = constraint.toString().toUpperCase();
            ArrayList<ModelChats> filteredModels = new ArrayList<>();
            for (ModelChats model : filterList) {
                if (model.getName() != null && model.getName().toUpperCase().contains(search)) {
                    filteredModels.add(model);
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        } else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.chatsArrayList = (ArrayList<ModelChats>) results.values;
        adapter.notifyDataSetChanged();
    }
}

package com.yourpackagename.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import com.yourpackagename.R;
import com.yourpackagename.activities.ChatActivity;
import com.yourpackagename.databinding.RowChatsBinding;
import com.yourpackagename.models.ModelChats;
import java.util.ArrayList;

/**
 * Adapter for displaying each chat in the chats RecyclerView.
 * Supports filtering/search.
 */
public class AdapterChats extends RecyclerView.Adapter<AdapterChats.HolderChats> implements Filterable {
    public ArrayList<ModelChats> chatsArrayList;
    public ArrayList<ModelChats> filterList;
    private Context context;
    private FilterChats filter;
    private String myUid;

    public AdapterChats(Context context, ArrayList<ModelChats> chatsArrayList, String myUid) {
        this.context = context;
        this.chatsArrayList = chatsArrayList;
        this.filterList = chatsArrayList;
        this.myUid = myUid;
    }

    @NonNull
    @Override
    public HolderChats onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chats, parent, false);
        return new HolderChats(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderChats holder, int position) {
        ModelChats model = chatsArrayList.get(position);

        // PROFILE
        Glide.with(context)
                .load(model.getProfileImageUrl())
                .placeholder(R.drawable.ic_person_white)
                .into(holder.profileIv);
        holder.nameTv.setText(model.getName());

        // MESSAGE + TYPE
        if (model.getMessageType() != null && model.getMessageType().equals("TEXT")) {
            holder.lastMessageTv.setText(model.getMessage());
        } else {
            holder.lastMessageTv.setText("Sends Attachment");
        }

        // DATE
        holder.dateTimeTv.setText(Utils.formatTimestampDateTime(model.getTimestamp()));

        // OnClick: open chat activity for this receiptUid
        holder.itemView.setOnClickListener(v -> {
            String receiptUid = model.getReceiptUid();
            if (receiptUid != null) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("receiptUid", receiptUid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() { return chatsArrayList.size(); }

    // Filtering for search
    @Override
    public Filter getFilter() {
        if (filter == null) filter = new FilterChats(this, filterList);
        return filter;
    }

    class HolderChats extends RecyclerView.ViewHolder {
        ShapeableImageView profileIv;
        TextView nameTv, lastMessageTv, dateTimeTv;

        public HolderChats(@NonNull View itemView) {
            super(itemView);
            profileIv = itemView.findViewById(R.id.profileIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            lastMessageTv = itemView.findViewById(R.id.lastMessageTv);
            dateTimeTv = itemView.findViewById(R.id.dateTimeTv);
        }
    }
}

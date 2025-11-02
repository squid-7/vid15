package com.yourpackagename.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.yourpackagename.adapters.AdapterChats;
import com.yourpackagename.databinding.FragmentChatsBinding;
import com.yourpackagename.models.ModelChats;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Fragment to display all user's chats.
 * Shows list, handles filter, binding, sorting (by last timestamp).
 */
public class ChatsFragment extends Fragment {
    private FragmentChatsBinding binding;
    private static final String TAG = "CHATS_TAG";
    private FirebaseAuth firebaseAuth;
    private String myUid;
    private Context mContext;
    private ArrayList<ModelChats> chatsArrayList;
    private AdapterChats adapterChats;

    @Override
    public void onAttach(@NonNull Context context) {
        this.mContext = context;
        super.onAttach(context);
    }

    public ChatsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getUid();

        loadChats();

        // Search functionality for chats
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterChats.getFilter().filter(s.toString());
                } catch (Exception e) {
                    Log.e(TAG, "onTextChanged: ", e);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadChats() {
        chatsArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatsArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String chatKey = ds.getKey();
                    if (chatKey != null && chatKey.contains(myUid)) {
                        ModelChats modelChats = new ModelChats();
                        modelChats.setChatKey(chatKey);
                        // load/set additional fields as per Firebase structure
                        chatsArrayList.add(modelChats);
                    }
                }
                adapterChats = new AdapterChats(mContext, chatsArrayList, myUid);
                binding.chatsRv.setAdapter(adapterChats);
                sort();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Delayed sort to wait for messages sync (as per your video)
    private void sort() {
        new Handler().postDelayed(() -> {
            Collections.sort(chatsArrayList, (model1, model2) -> Long.compare(model2.getTimestamp(), model1.getTimestamp()));
            adapterChats.notifyDataSetChanged();
        }, 1000);
    }
}

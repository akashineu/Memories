package com.akash.memories.util;


import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akash.memories.R;
import com.akash.memories.model.PostModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PostModel> postModelArrayList;

    public CustomAdapter(Context context, ArrayList<PostModel> postModelArrayList) {
        this.context = context;
        this.postModelArrayList = postModelArrayList;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // public TextView textViewDate;
        public TextView textViewTitle;
        public ImageView imageViewMemory;
        public TextView textViewDescription;
        public TextView textViewTime;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            // textViewDate = view.findViewById(R.id.textViewDate);
            textViewTitle = view.findViewById(R.id.textViewTitle);
            imageViewMemory = view.findViewById(R.id.imageViewMemory);
            textViewDescription = view.findViewById(R.id.textViewDescription);
            textViewTime = view.findViewById(R.id.textViewTime);

        }


    }



    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        PostModel postModel = postModelArrayList.get(position);
        //viewHolder.textViewDate.setText(postModel.get);
        viewHolder.textViewTitle.setText(postModel.getUserName() + " | " + postModel.getCaption());
       // viewHolder.imageViewMemory.setImageURI(postModel.getImage());
        Picasso.get().load(postModel.getImage()).placeholder(R.drawable.friends).fit().into(viewHolder.imageViewMemory);
        viewHolder.textViewDescription.setText(postModel.getDescription());


        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        //viewHolder.getTextView().setText(localDataSet[position]);


        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(postModel.getTimeAdded());
        viewHolder.textViewTime.setText(timeAgo);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return postModelArrayList.size();
    }
}


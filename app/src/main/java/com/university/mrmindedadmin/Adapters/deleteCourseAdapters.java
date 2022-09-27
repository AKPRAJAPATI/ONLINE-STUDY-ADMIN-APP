package com.university.mrmindedadmin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.university.mrmindedadmin.Models.courseModel;
import com.university.mrmindedadmin.R;
import com.university.mrmindedadmin.databinding.ListitemBinding;
import com.university.mrmindedadmin.deleteClickedEvent;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class deleteCourseAdapters extends RecyclerView.Adapter<deleteCourseAdapters.mainViewHolder> {


    private Context context;
    private ArrayList<courseModel> arrayList;
    deleteClickedEvent clickedEvent;

    public deleteCourseAdapters(Context context, ArrayList<courseModel> arrayList, deleteClickedEvent clickedEvent) {
        this.context = context;
        this.arrayList = arrayList;
        this.clickedEvent = clickedEvent;
    }

    @NonNull
    @Override
    public mainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new mainViewHolder(LayoutInflater.from(context).inflate(R.layout.listitem, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull mainViewHolder holder, int position) {
        courseModel model = arrayList.get(position);
        Picasso.get().load(model.getThumbnail()).into(holder.binding.courseThumbnail);
        holder.binding.coursePrice.setText("Buy now : " + String.valueOf(model.getPrice()));
        holder.binding.courseName.setText(model.getCourse_name());

        //////////////marque work ////////////////////
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clickedEvent.checkItem(model.getUniquekey(), model.getThumbnail());
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedEvent.updateItem(model.getUniquekey(), model.getThumbnail());
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class mainViewHolder extends RecyclerView.ViewHolder {
        private ListitemBinding binding;

        public mainViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ListitemBinding.bind(itemView);
        }
    }
}

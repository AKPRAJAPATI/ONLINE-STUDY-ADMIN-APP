package com.university.mrmindedadmin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.university.mrmindedadmin.Models.playInterface;
import com.university.mrmindedadmin.Models.videoListModel;
import com.university.mrmindedadmin.R;
import com.university.mrmindedadmin.databinding.CourseListRowItemsBinding;

import java.util.ArrayList;

public class videoListAdapters extends RecyclerView.Adapter<videoListAdapters.mainViewHolder> {


    private Context context;
    private ArrayList<videoListModel> arrayList;
    private playInterface clicklistner;

    public videoListAdapters(Context context, ArrayList<videoListModel> arrayList, playInterface clicklistner) {
        this.context = context;
        this.arrayList = arrayList;
        this.clicklistner = clicklistner;
    }

    @NonNull
    @Override
    public mainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new mainViewHolder(LayoutInflater.from(context).inflate(R.layout.course_list_row_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull mainViewHolder holder, int position) {
        videoListModel model = arrayList.get(position);
        holder.binding.contentTitle.setText(model.getVideoName());
        holder.binding.updateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicklistner.updateClickLister(holder.getAdapterPosition(), model.getVideoUniqueKey());
            }
        });
        holder.binding.deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicklistner.deleteClickLister(holder.getAdapterPosition(), model.getVideoUniqueKey());
            }
        });
        holder.binding.contentTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicklistner.playVideo(holder.getAdapterPosition(), model.getVideoUrl(), model.getVideoUniqueKey());
            }
        });
        holder.binding.contentNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicklistner.playVideo(holder.getAdapterPosition(), model.getVideoUrl(), model.getVideoUniqueKey());
            }
        });
        holder.binding.playback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicklistner.playVideo(holder.getAdapterPosition(), model.getVideoUrl(), model.getVideoUniqueKey());
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class mainViewHolder extends RecyclerView.ViewHolder {
        private CourseListRowItemsBinding binding;

        public mainViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CourseListRowItemsBinding.bind(itemView);
        }
    }
}

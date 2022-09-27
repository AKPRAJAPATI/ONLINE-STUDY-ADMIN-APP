package com.university.mrmindedadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.university.mrmindedadmin.Application.DetailActivity;
import com.university.mrmindedadmin.Models.courseModel;
import com.university.mrmindedadmin.R;
import com.university.mrmindedadmin.databinding.ListitemBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class mainAdapters extends RecyclerView.Adapter<mainAdapters.mainViewHolder> {


    private Context context;
    private ArrayList<courseModel> arrayList;

    public mainAdapters(Context context, ArrayList<courseModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
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
        holder.binding.coursePrice.setText("Buy now : "+String.valueOf(model.getPrice()));
        holder.binding.courseName.setText(model.getCourse_name());

        //////////////marque work ////////////////////
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,DetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("imageThumbnail",model.getThumbnail());
                intent.putExtra("uniqueKey",model.getUniquekey());
                context.startActivity(intent);
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

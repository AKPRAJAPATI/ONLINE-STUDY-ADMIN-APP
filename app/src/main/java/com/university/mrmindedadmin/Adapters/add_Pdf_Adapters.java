package com.university.mrmindedadmin.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.university.mrmindedadmin.Application.pdf.addPdfActivity;
import com.university.mrmindedadmin.Models.courseModel;
import com.university.mrmindedadmin.R;
import com.university.mrmindedadmin.databinding.ListitemBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class add_Pdf_Adapters extends RecyclerView.Adapter<add_Pdf_Adapters.mainViewHolder> {


    private Context context;
    private ArrayList<courseModel> arrayList;

    public add_Pdf_Adapters(Context context, ArrayList<courseModel> arrayList) {
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

                Intent intent = new Intent(context, addPdfActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("uniqueKey",model.getUniquekey());
                intent.putExtra("courseName",model.getCourse_name());
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

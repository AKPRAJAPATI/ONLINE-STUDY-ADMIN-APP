package com.university.mrmindedadmin.Delete_Database.Adapters;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.university.mrmindedadmin.Models.slideOnclickInterface;
import com.university.mrmindedadmin.Models.slidersModel;
import com.university.mrmindedadmin.R;
import com.university.mrmindedadmin.databinding.SlideItemBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class slidersAdapters extends RecyclerView.Adapter<slidersAdapters.slideViewHolder> {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private Context context;
    private ArrayList<slidersModel> arrayList;
    private slideOnclickInterface clickListener;

    public slidersAdapters(Context context, ArrayList<slidersModel> arrayList, slideOnclickInterface clickListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public slideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new slideViewHolder(LayoutInflater.from(context).inflate(R.layout.slide_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull slideViewHolder holder, int position) {
        slidersModel model = arrayList.get(position);

        Picasso.get().load(model.getSlidersImg()).into(holder.binding.imageSlidersItem);

        sharedPreferences = context.getSharedPreferences("ourUserData", MODE_PRIVATE);
        String collgename = sharedPreferences.getString("college_", "");
        String coursename = sharedPreferences.getString("course_", "");

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

//        holder.binding.updateSlideImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent = new Intent(context, updateNotice.class);
////                intent.putExtra("my_course", coursename);
////                intent.putExtra("my_college", collgename);
////                intent.putExtra("uniqueKey",model.getNoticeUnqueKey());
////                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                context.startActivity(intent);
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                ((Activity) context).startActivityForResult(intent, 100);
//            }
//        });
//
//        holder.binding.deleteSlideImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                databaseReference.child(coursename).child(collgename).child(auth.getUid()).child("Slider Image").child(model.getSlideUniqueKey()).removeValue();
//            }
//        });

        holder.binding.deleteSlideImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.deleteClickLister(position,model.getUniqueKey());
            }
        });
        holder.binding.updateSlideImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.updateClickLister(position,model.getUniqueKey());
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class slideViewHolder extends RecyclerView.ViewHolder {
        private SlideItemBinding binding;

        public slideViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SlideItemBinding.bind(itemView);
        }
    }
    //////////onpen gallery/////////////

    /////////////////close work //////////////////
}

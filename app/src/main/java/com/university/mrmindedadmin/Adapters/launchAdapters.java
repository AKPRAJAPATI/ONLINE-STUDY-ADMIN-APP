package com.university.mrmindedadmin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.university.mrmindedadmin.Models.courseModel;
import com.university.mrmindedadmin.R;
import com.university.mrmindedadmin.databinding.LunchItemBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class launchAdapters extends RecyclerView.Adapter<launchAdapters.mainViewHolder> {
    private Context context;
    private ArrayList<courseModel> arrayList;
    private AlertDialog.Builder alearDailog;

    public launchAdapters(Context context, ArrayList<courseModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public mainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new mainViewHolder(LayoutInflater.from(context).inflate(R.layout.lunch_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull mainViewHolder holder, int position) {
        courseModel model = arrayList.get(position);
        Picasso.get().load(model.getThumbnail()).into(holder.binding.courseThumbnail);
        holder.binding.courseName.setText(model.getCourse_name());
        alearDailog = new AlertDialog.Builder(context.getApplicationContext());
        //////////////marque work ////////////////////
        FirebaseDatabase.getInstance().getReference().child("Admin").child(FirebaseAuth.getInstance().getUid()).child("Course").child(FirebaseAuth.getInstance().getUid()).child(model.getUniquekey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    boolean launching = snapshot.child("launched").getValue(boolean.class);
                    if (launching == true)
                    {
                        holder.binding.rightTick.setVisibility(View.VISIBLE);
                    }else{
                        holder.binding.rightTick.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference().child("Admin").child(FirebaseAuth.getInstance().getUid()).child("Course").child(FirebaseAuth.getInstance().getUid()).child(model.getUniquekey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            boolean launching = snapshot.child("launched").getValue(boolean.class);
                            if (launching == true)
                            {
                                Toast.makeText(context, "Course already launched ", Toast.LENGTH_SHORT).show();
                            }else{
                                HashMap<String , Object> hashMap = new HashMap<>();
                                hashMap.put("launched", true);
                                FirebaseDatabase.getInstance().getReference().child("Admin").child(FirebaseAuth.getInstance().getUid()).child("Course").child(FirebaseAuth.getInstance().getUid()).child(model.getUniquekey()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(context, "Course launched", Toast.LENGTH_SHORT).show();
                                            holder.binding.rightTick.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class mainViewHolder extends RecyclerView.ViewHolder {
        private LunchItemBinding binding;

        public mainViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LunchItemBinding.bind(itemView);
        }
    }
}

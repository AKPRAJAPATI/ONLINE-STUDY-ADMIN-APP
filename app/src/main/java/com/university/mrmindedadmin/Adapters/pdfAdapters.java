package com.university.mrmindedadmin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.university.mrmindedadmin.Models.pdfInterface;
import com.university.mrmindedadmin.Models.pdfModel;
import com.university.mrmindedadmin.R;
import com.university.mrmindedadmin.databinding.PdfListItemBinding;

import java.util.ArrayList;

public class pdfAdapters extends RecyclerView.Adapter<pdfAdapters.mainViewHolder> {


    private Context context;
    private ArrayList<pdfModel> arrayList;
    private pdfInterface clicked;


    public pdfAdapters(Context context, ArrayList<pdfModel> arrayList, pdfInterface clicked) {
        this.context = context;
        this.arrayList = arrayList;
        this.clicked = clicked;
    }

    @NonNull
    @Override
    public mainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new mainViewHolder(LayoutInflater.from(context).inflate(R.layout.pdf_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull mainViewHolder holder, int position) {
        pdfModel model = arrayList.get(position);
        holder.binding.pdfName.setText(model.getPdfName());



        holder.binding.updatePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
 clicked.updateClickLister(holder.getAdapterPosition(),model.getPdfUniqueKey());
            }
        });
        holder.binding.deletePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked.deleteClickLister(holder.getAdapterPosition(),model.getPdfUniqueKey());
            }
        });

        holder.binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked.showpdf(holder.getAdapterPosition(),model.getPdfUrl(),model.getPdfUniqueKey());
            }
        });
        holder.binding.pdfName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked.showpdf(holder.getAdapterPosition(),model.getPdfUrl(),model.getPdfUniqueKey());
            }
        });
        holder.binding.contentNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked.showpdf(holder.getAdapterPosition(),model.getPdfUrl(),model.getPdfUniqueKey());

            }
        });

//        holder.binding.pdfTime.setText(model.getPdfTime());
        //////////////marque work ////////////////////




    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class mainViewHolder extends RecyclerView.ViewHolder {
        private PdfListItemBinding binding;

        public mainViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = PdfListItemBinding.bind(itemView);
        }
    }
}

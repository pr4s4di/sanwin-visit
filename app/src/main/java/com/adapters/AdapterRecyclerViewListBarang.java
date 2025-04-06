package com.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.interfaces.IViewHolderListener;
import com.models.ModelListBarang;
import com.sanken.sanwinvisit.R;
import com.viewholders.ViewHolderListBarang;

import java.util.ArrayList;

public class AdapterRecyclerViewListBarang extends RecyclerView.Adapter<ViewHolderListBarang> {

    private ArrayList<ModelListBarang> barangs;
    private ArrayList<ModelListBarang> barangsCopy = new ArrayList<>();
    private IViewHolderListener listener;

    public AdapterRecyclerViewListBarang(ArrayList<ModelListBarang> barangs, IViewHolderListener listener) {
        this.listener = listener;
        this.barangs = barangs;
        this.barangsCopy.addAll(barangs);
    }

    @NonNull
    @Override
    public ViewHolderListBarang onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_barang, parent, false);
        return new ViewHolderListBarang(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderListBarang holder, int position) {
        ModelListBarang barang = barangs.get(position);
        holder.getTxtViewKodeBarang().setText(barang.getKodeBarang());
        holder.getTxtViewNamaBarang().setText(barang.getNamaBarang());
        holder.getTxtViewQtyBarang().setText(barang.getQtyBarang());
        holder.setListener(listener);
    }

    public void filter(String text) {
        barangs.clear();
        if(text.isEmpty()){
            barangs.addAll(barangsCopy);
        } else{
            text = text.toLowerCase();
            for(ModelListBarang item: barangsCopy){
                if(item.getKodeBarang().toLowerCase().contains(text)){
                    barangs.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return barangs.size();
    }
}

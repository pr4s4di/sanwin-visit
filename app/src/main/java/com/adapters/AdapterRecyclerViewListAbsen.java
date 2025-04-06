package com.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.interfaces.IViewHolderListener;
import com.models.ModelListAbsen;
import com.sanken.sanwinvisit.R;
import com.viewholders.ViewHolderListAbsen;

import java.util.ArrayList;

public class AdapterRecyclerViewListAbsen extends RecyclerView.Adapter<ViewHolderListAbsen>{

    private IViewHolderListener listener;
    private ArrayList<ModelListAbsen> absens;

    public AdapterRecyclerViewListAbsen(ArrayList<ModelListAbsen> absens, IViewHolderListener listener) {
        this.listener = listener;
        this.absens = absens;
    }

    @NonNull
    @Override
    public ViewHolderListAbsen onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_absen, parent, false);
        return new ViewHolderListAbsen(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderListAbsen holder, final int position) {
        ModelListAbsen absen = absens.get(position);
        switch (absen.getStatus())
        {
            case "MASUK":
                holder.getImageViewStatus().setImageResource(R.mipmap.ic_login);
                break;
            case "KELUAR":
                holder.getImageViewStatus().setImageResource(R.mipmap.ic_logout);
                break;
            case "KUNJUNGAN":
                holder.getImageViewStatus().setImageResource(R.drawable.ic_car_orange);
                break;
            case "PHONE":
                holder.getImageViewStatus().setImageResource(R.drawable.ic_phone_blue);
                break;
        }
        holder.getTxtViewTglCheckOut().setText(absen.getWaktuCheckOut());
        holder.getTxtViewKodeKunjungan().setText(absen.getKodeKunjungan());
        holder.getTxtViewDateTime().setText(absen.getWaktuHadir());
        holder.getTxtViewStatus().setText(absen.getStatus());
        holder.getTxtViewAlamat().setText(absen.getAlamat());
        holder.setListener(listener);
    }

    @Override
    public int getItemCount() {
        return absens.size();
    }
}

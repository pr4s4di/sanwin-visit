package com.viewholders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.interfaces.IViewHolderListener;
import com.sanken.sanwinvisit.R;

public class ViewHolderListBarang extends RecyclerView.ViewHolder implements View.OnClickListener {

    private IViewHolderListener listener;
    private CardView cardViewListBarang;
    private TextView txtViewKodeBarang;
    private TextView txtViewNamaBarang;
    private TextView txtViewQtyBarang;
    private ImageView imageViewDeleteItem;

    public ViewHolderListBarang(View itemView) {
        super(itemView);
        txtViewKodeBarang = itemView.findViewById(R.id.txtViewKodeBarang);
        txtViewNamaBarang = itemView.findViewById(R.id.txtViewNamaBarang);
        txtViewQtyBarang = itemView.findViewById(R.id.txtViewQtyBarang);
        cardViewListBarang = itemView.findViewById(R.id.cardViewListBarang);
        imageViewDeleteItem = itemView.findViewById(R.id.imageViewDeleteItem);
        cardViewListBarang.setOnClickListener(this);
        imageViewDeleteItem.setOnClickListener(this);
        if (txtViewQtyBarang.getText().toString().trim().isEmpty()) {
            txtViewQtyBarang.setVisibility(View.GONE);
            imageViewDeleteItem.setVisibility(View.GONE);
        }
    }

    public TextView getTxtViewKodeBarang() {
        return txtViewKodeBarang;
    }

    public TextView getTxtViewNamaBarang() {
        return txtViewNamaBarang;
    }

    public TextView getTxtViewQtyBarang() {
        return txtViewQtyBarang;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onViewHolderClick(v, txtViewKodeBarang.getText().toString());
        }
    }

    public void setListener(IViewHolderListener listener) {
        this.listener = listener;
    }
}

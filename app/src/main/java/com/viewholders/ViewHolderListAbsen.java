package com.viewholders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.interfaces.IViewHolderListener;
import com.sanken.sanwinvisit.R;

public class ViewHolderListAbsen extends RecyclerView.ViewHolder implements View.OnClickListener{

    private IViewHolderListener listener;
    private CardView cardView;
    private ImageView imageViewStatus;
    private TextView txtViewKodeKunjungan;
    private TextView txtViewDateTime;
    private TextView txtViewStatus;
    private TextView txtViewAlamat;
    private TextView txtViewTglCheckOut;

    public ViewHolderListAbsen(View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.cardViewListAbsen);
        imageViewStatus = itemView.findViewById(R.id.imageViewStatus);
        txtViewKodeKunjungan = itemView.findViewById(R.id.txtViewKodeKunjungan);
        txtViewDateTime = itemView.findViewById(R.id.txtViewDateTime);
        txtViewTglCheckOut = itemView.findViewById(R.id.txtViewTglCheckOut);
        txtViewStatus = itemView.findViewById(R.id.txtViewStatus);
        txtViewAlamat = itemView.findViewById(R.id.txtViewAlamat);
        cardView.setOnClickListener(this);
    }

    public TextView getTxtViewKodeKunjungan() {
        return txtViewKodeKunjungan;
    }

    public TextView getTxtViewDateTime() {
        return txtViewDateTime;
    }

    public TextView getTxtViewTglCheckOut() {
        return txtViewTglCheckOut;
    }

    public TextView getTxtViewStatus() {
        return txtViewStatus;
    }

    public TextView getTxtViewAlamat() {
        return txtViewAlamat;
    }

    public ImageView getImageViewStatus() {
        return imageViewStatus;
    }

    public void setImageViewStatus(ImageView imageViewStatus) {
        this.imageViewStatus = imageViewStatus;
    }

    public CardView getCardView() {
        return cardView;
    }

    @Override
    public void onClick(View v) {
        if(listener != null)
        {
            listener.onViewHolderClick(v, txtViewKodeKunjungan.getText().toString());
        }
    }

    public void setListener(IViewHolderListener listener) {
        this.listener = listener;
    }
}

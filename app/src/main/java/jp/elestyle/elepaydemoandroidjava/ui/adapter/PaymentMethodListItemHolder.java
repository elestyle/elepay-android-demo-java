package jp.elestyle.elepaydemoandroidjava.ui.adapter;

import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import jp.elestyle.elepaydemoandroidjava.R;

class PaymentMethodListItemHolder extends RecyclerView.ViewHolder {

    private ImageButton imageButton;

    PaymentMethodListItemHolder(@NonNull View itemView) {
        super(itemView);

        imageButton = itemView.findViewById(R.id.paymentMethodListItemImageButton);
    }

    ImageButton getImageButton() {
        return imageButton;
    }
}

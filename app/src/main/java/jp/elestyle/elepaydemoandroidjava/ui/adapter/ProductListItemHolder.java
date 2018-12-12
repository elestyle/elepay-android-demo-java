package jp.elestyle.elepaydemoandroidjava.ui.adapter;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import jp.elestyle.elepaydemoandroidjava.R;

class ProductListItemHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView titleView;
    TextView priceView;
    Button priceButton;

    ProductListItemHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.product_list_item_image);
        titleView = itemView.findViewById(R.id.product_list_item_title);
        priceView = itemView.findViewById(R.id.product_list_item_price);
        priceButton = itemView.findViewById(R.id.product_list_item_button_buy);
    }
}

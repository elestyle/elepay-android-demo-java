package jp.elestyle.elepaydemoandroidjava.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import jp.elestyle.elepaydemoandroidjava.R;
import jp.elestyle.elepaydemoandroidjava.data.Product;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListItemHolder> {
    private List<Product> productList = new ArrayList<Product>() {{
        add(new Product("", "MOORING SMART MATTRESS PAD S", "1"));
        add(new Product("", "MOORING SMART MATTRESS PAD D", "100"));
    }};
    private ProductListAdapterListener listener;

    public ProductListAdapter(ProductListAdapterListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductListItemHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_list_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListItemHolder holder, final int position) {
        Product data = productList.get(position);

//        holder.imageView.setImageBitmap(null);
        holder.imageView.setImageResource(R.mipmap.product_sample_img);
        holder.titleView.setText(data.getTitle());
        holder.priceView.setText(data.getPrice());
        if (!holder.priceButton.hasOnClickListeners()) {
            holder.priceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onProductListItemPriceButtonClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public Product getItemAt(int index) {
        return productList.get(index);
    }
}

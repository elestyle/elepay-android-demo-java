package jp.elestyle.elepaydemoandroidjava.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import jp.elestyle.elepaydemoandroidjava.R;
import jp.elestyle.elepaydemoandroidjava.data.Product;
import jp.elestyle.elepaydemoandroidjava.ui.adapter.ProductListAdapter;
import jp.elestyle.elepaydemoandroidjava.ui.adapter.ProductListAdapterListener;

public class ProductListActivity extends AppCompatActivity {

    private ProductListAdapter productListAdapter;
    private ProductListAdapterListener adapterListener = new ProductListAdapterListener() {
        @Override
        public void onProductListItemPriceButtonClick(int itemIndex) {
            Product product = productListAdapter.getItemAt(itemIndex);
            Log.d("ProducatListActivity", "onProductListItemPriceButtonClick(): " + product.getTitle() + " " + product.getPrice());
            Intent intent = new Intent(ProductListActivity.this, PaymentActivity.class);
            intent.putExtra(PaymentActivity.INTENT_KEY_AMOUNT, product.getPrice());
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView productListView = findViewById(R.id.activity_product_list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        productListView.setLayoutManager(layoutManager);
        productListView.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
        productListAdapter = new ProductListAdapter(adapterListener);
        productListView.setAdapter(productListAdapter);
    }

}

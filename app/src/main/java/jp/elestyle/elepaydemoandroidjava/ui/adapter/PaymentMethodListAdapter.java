package jp.elestyle.elepaydemoandroidjava.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import jp.elestyle.elepaydemoandroidjava.R;
import jp.elestyle.elepaydemoandroidjava.data.PaymentMethodItemData;
import jp.elestyle.elepaydemoandroidjava.util.PaymentMethod;

public class PaymentMethodListAdapter extends RecyclerView.Adapter<PaymentMethodListItemHolder> {

    private PaymentMethodListAdapterListener listener;
    private List<PaymentMethodItemData> list = new ArrayList<PaymentMethodItemData>() {{
        add(new PaymentMethodItemData(R.mipmap.ic_credit_card, PaymentMethod.CREDIT_CARD));
        add(new PaymentMethodItemData(R.drawable.ic_alipay, PaymentMethod.ALIPAY));
        add(new PaymentMethodItemData(R.mipmap.ic_union_pay, PaymentMethod.UNION_PAY));
        add(new PaymentMethodItemData(R.mipmap.ic_paypal_logo_200px, PaymentMethod.PAYPAL));
        add(new PaymentMethodItemData(R.mipmap.ic_linepay, PaymentMethod.LINE_PAY));
    }};
    private int selectedPosition = 0;

    public PaymentMethodListAdapter(PaymentMethodListAdapterListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PaymentMethodListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PaymentMethodListItemHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.payment_method_list_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PaymentMethodListItemHolder holder, int position) {
        final PaymentMethodItemData itemData = list.get(position);
        holder.getImageButton().setImageResource(itemData.getImageRes());

        if (!holder.getImageButton().hasOnClickListeners()) {
            holder.getImageButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSelectPaymentMethod(itemData.getPaymentMethod());

                    notifyItemChanged(selectedPosition);
                    selectedPosition = holder.getLayoutPosition();
                    notifyItemChanged(selectedPosition);
                }
            });
        }
        holder.getImageButton().setSelected(position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

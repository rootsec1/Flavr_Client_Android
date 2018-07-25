package io.github.abhishekwl.flavrclient.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import io.github.abhishekwl.flavrclient.Helpers.Constants;
import io.github.abhishekwl.flavrclient.Models.Food;
import io.github.abhishekwl.flavrclient.R;
import java.util.ArrayList;

public class CartItemRecyclerViewAdapter extends RecyclerView.Adapter<CartItemRecyclerViewAdapter.CartViewHolder> {

  private Context rootContext;
  private ArrayList<Food> foodArrayList;
  private String currencyCode;

  public CartItemRecyclerViewAdapter(Context context, ArrayList<Food> foodArrayList) {
    this.rootContext = context;
    this.foodArrayList = foodArrayList;
    this.currencyCode = Constants.currencyCode;
  }

  @NonNull
  @Override
  public CartItemRecyclerViewAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list_item, parent, false);
    return new CartViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull CartItemRecyclerViewAdapter.CartViewHolder holder, int position) {
    Food food = foodArrayList.get(position);
    holder.bind(holder.itemView.getContext(), food, position);
  }

  @Override
  public int getItemCount() {
    return foodArrayList.size();
  }

  class CartViewHolder extends ViewHolder {

    @BindView(R.id.cartListItemImageView)
    ImageView itemImageView;
    @BindView(R.id.cartListItemNameTextView)
    TextView itemNameTextView;
    @BindView(R.id.cartListItemCostTextView)
    TextView itemCostTextView;
    @BindView(R.id.cartListItemLinearLayout)
    LinearLayout itemLinearLayout;
    @BindColor(R.color.colorAccent) int colorAccent;
    @BindColor(R.color.colorTextDark) int colorTextDark;

    CartViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    void bind(Context context, Food food, int index) {
      Glide.with(context).load(food.getImage()).into(itemImageView);
      itemNameTextView.setText(food.getName());
      itemCostTextView.setText(currencyCode.concat(" ").concat(Double.toString(food.getCost())));

      itemLinearLayout.setOnClickListener(v -> {
        Snackbar.make(itemLinearLayout, "Long press the item to delete.", Snackbar.LENGTH_SHORT).show();
      });

      itemLinearLayout.setOnLongClickListener(v -> {
        Snackbar.make(itemLinearLayout, "Are you sure you want to delete ".concat(food.getName()).concat(" from your cart?"), Snackbar.LENGTH_LONG)
            .setActionTextColor(colorAccent)
            .setAction("YES", v2 -> {
              foodArrayList.remove(index);
              notifyDataSetChanged();
            }).show();
        return true;
      });

    }
  }
}

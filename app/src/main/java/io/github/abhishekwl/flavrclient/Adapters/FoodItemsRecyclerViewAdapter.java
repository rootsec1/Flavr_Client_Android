package io.github.abhishekwl.flavrclient.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import io.github.abhishekwl.flavrclient.Helpers.Constants;
import io.github.abhishekwl.flavrclient.Models.Food;
import io.github.abhishekwl.flavrclient.R;
import java.util.ArrayList;

public class FoodItemsRecyclerViewAdapter extends RecyclerView.Adapter<FoodItemsRecyclerViewAdapter.FoodItemViewHolder> {

  private Context rootContext;
  private ArrayList<Food> foodArrayList;

  FoodItemsRecyclerViewAdapter(Context context, ArrayList<Food> foodArrayList) {
    this.rootContext = context;
    this.foodArrayList = foodArrayList;
  }

  @NonNull
  @Override
  public FoodItemsRecyclerViewAdapter.FoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_list_item, parent, false);
    return new FoodItemViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull FoodItemsRecyclerViewAdapter.FoodItemViewHolder holder, int position) {
    Food food = foodArrayList.get(position);
    holder.bind(holder.itemView.getContext(), food);
  }

  @Override
  public int getItemCount() {
    return foodArrayList.size();
  }

  class FoodItemViewHolder extends ViewHolder {

    @BindView(R.id.foodListItemImageView)
    ImageView itemImageView;
    @BindView(R.id.foodListItemNameTextView)
    TextView itemNameTextView;
    @BindView(R.id.foodListItemCostTextView)
    TextView itemCostTextView;
    @BindView(R.id.foodListItemCardView)
    CardView itemCardView;
    @BindColor(R.color.colorItemSelected) int colorSelected;
    @BindColor(R.color.colorTextDark) int colorTextDark;
    @BindColor(android.R.color.white) int colorWhite;
    @BindColor(android.R.color.tab_indicator_text) int colorTextLight;

    FoodItemViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    void bind(Context context, Food food) {
      Glide.with(context).load(food.getImage()).into(itemImageView);
      itemNameTextView.setText(food.getName());
      itemCostTextView.setText(Constants.currencyCode.concat(" ".concat(Double.toString(food.getCost()))));

      itemCardView.setOnClickListener(v -> {
        food.setFoodSelcted(!food.isFoodSelcted());
        renderLayout(food);
      });

      renderLayout(food);
    }

    private void renderLayout(Food food) {
      if (food.isFoodSelcted()) {
        itemCardView.setCardBackgroundColor(colorSelected);
        itemNameTextView.setTextColor(colorWhite);
        itemCostTextView.setTextColor(colorWhite);
      } else {
        itemCardView.setCardBackgroundColor(colorWhite);
        itemNameTextView.setTextColor(colorTextDark);
        itemCostTextView.setTextColor(colorTextLight);
      }
    }
  }
}

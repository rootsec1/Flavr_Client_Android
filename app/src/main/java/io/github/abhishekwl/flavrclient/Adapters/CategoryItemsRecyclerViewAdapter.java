package io.github.abhishekwl.flavrclient.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.abhishekwl.flavrclient.Models.Category;
import io.github.abhishekwl.flavrclient.R;
import java.util.ArrayList;

public class CategoryItemsRecyclerViewAdapter extends RecyclerView.Adapter<CategoryItemsRecyclerViewAdapter.CategoryViewHolder> {

  private ArrayList<Category> categoryArrayList;
  private Context rootContext;

  public CategoryItemsRecyclerViewAdapter(Context context, ArrayList<Category> categoryArrayList) {
    this.rootContext = context;
    this.categoryArrayList = categoryArrayList;
  }

  @NonNull
  @Override
  public CategoryItemsRecyclerViewAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item, parent, false);
    return new CategoryViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull CategoryItemsRecyclerViewAdapter.CategoryViewHolder holder, int position) {
    Category category = categoryArrayList.get(position);
    holder.bind(category, holder.itemView.getContext());
  }

  @Override
  public int getItemCount() {
    return categoryArrayList.size();
  }

  class CategoryViewHolder extends ViewHolder {

    @BindView(R.id.categoryListItemCategoryNameTextView)
    TextView categoryNameTextView;
    @BindView(R.id.categoryListItemFoodItemsRecyclerView)
    RecyclerView foodItemsRecyclerView;

    private FoodItemsRecyclerViewAdapter foodItemsRecyclerViewAdapter;

    CategoryViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    void bind(Category category, Context context) {
      categoryNameTextView.setText(category.getCategoryName());
      foodItemsRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
      foodItemsRecyclerView.setItemAnimator(new DefaultItemAnimator());
      foodItemsRecyclerView.setHasFixedSize(true);
      foodItemsRecyclerViewAdapter = new FoodItemsRecyclerViewAdapter(context, category.getFoodArrayList());
      foodItemsRecyclerView.setAdapter(foodItemsRecyclerViewAdapter);
    }
  }
}

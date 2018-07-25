package io.github.abhishekwl.flavrclient.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.google.firebase.auth.FirebaseAuth;
import io.github.abhishekwl.flavrclient.Adapters.CartItemRecyclerViewAdapter;
import io.github.abhishekwl.flavrclient.Helpers.ApiClient;
import io.github.abhishekwl.flavrclient.Helpers.ApiInterface;
import io.github.abhishekwl.flavrclient.Models.Food;
import io.github.abhishekwl.flavrclient.R;
import java.util.ArrayList;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {

  @BindView(R.id.cartFoodItemsRecyclerView)
  RecyclerView cartFoodItemsRecyclerView;
  @BindView(R.id.cartBuyButton)
  FloatingActionButton cartBuyButton;

  private Unbinder unbinder;
  private ArrayList<Food> foodArrayList = new ArrayList<>();
  private FirebaseAuth firebaseAuth;
  private ApiInterface apiInterface;
  private CartItemRecyclerViewAdapter cartItemRecyclerViewAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cart);

    unbinder = ButterKnife.bind(CartActivity.this);
    initializeComponents();
    initializeViews();
  }

  private void initializeComponents() {
    firebaseAuth = FirebaseAuth.getInstance();
    apiInterface = ApiClient.getClient(getApplicationContext()).create(ApiInterface.class);
    foodArrayList = getIntent().getParcelableArrayListExtra("FOOD");
    Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#424242'>Cart</font>"));
  }

  private void initializeViews() {
    setupRecyclerView();
  }

  private void setupRecyclerView() {
    cartFoodItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    cartFoodItemsRecyclerView.setHasFixedSize(true);
    cartFoodItemsRecyclerView.setItemAnimator(new DefaultItemAnimator());
    cartFoodItemsRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
    cartItemRecyclerViewAdapter = new CartItemRecyclerViewAdapter(getApplicationContext(), foodArrayList);
    cartFoodItemsRecyclerView.setAdapter(cartItemRecyclerViewAdapter);
  }

  @Override
  protected void onDestroy() {
    unbinder.unbind();
    super.onDestroy();
  }
}

package io.github.abhishekwl.flavrclient.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.firebase.auth.FirebaseAuth;
import io.github.abhishekwl.flavrclient.Adapters.CategoryItemsRecyclerViewAdapter;
import io.github.abhishekwl.flavrclient.Helpers.ApiClient;
import io.github.abhishekwl.flavrclient.Helpers.ApiInterface;
import io.github.abhishekwl.flavrclient.Models.Category;
import io.github.abhishekwl.flavrclient.Models.Food;
import io.github.abhishekwl.flavrclient.Models.Hotel;
import io.github.abhishekwl.flavrclient.Models.User;
import io.github.abhishekwl.flavrclient.R;
import java.util.ArrayList;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.mainToolbar)
  Toolbar mainToolbar;
  @BindView(R.id.mainCategoriesRecyclerView)
  RecyclerView mainCategoriesRecyclerView;
  @BindView(R.id.mainSwipeRefreshLayout)
  SwipeRefreshLayout mainSwipeRefreshLayout;
  @BindView(R.id.mainCheckoutFab)
  FloatingActionButton mainCheckoutFab;
  @BindView(R.id.mainHotelNameTextView)
  TextView mainHotelNameTextView;
  @BindColor(R.color.colorAccent) int colorAccent;
  @BindColor(R.color.colorTextDark) int colorTextDark;

  private FirebaseAuth firebaseAuth;
  private Unbinder unbinder;
  private Hotel currentHotel;
  private ApiInterface apiInterface;
  private MessageListener messageListener;
  private User currentUser;
  private CategoryItemsRecyclerViewAdapter categoryItemsRecyclerViewAdapter;
  private ArrayList<Category> categories = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    unbinder = ButterKnife.bind(MainActivity.this);
    initializeComponents();
    initializeViews();
  }

  private void initializeComponents() {
    firebaseAuth = FirebaseAuth.getInstance();
    apiInterface = ApiClient.getClient(getApplicationContext()).create(ApiInterface.class);
    scanForBeacons();
    fetchCurrentHotel("bGxNp1kQ2UhNM0kpCevP3muvT2h2");  //TODO: Remove later
    currentUser = getIntent().getParcelableExtra("USER");
  }

  private void initializeViews() {
    setSupportActionBar(mainToolbar);
    Objects.requireNonNull(getSupportActionBar()).setTitle("");
    mainSwipeRefreshLayout.setColorSchemeColors(colorAccent, colorTextDark);
    mainSwipeRefreshLayout.setRefreshing(true);
    mainCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    mainCategoriesRecyclerView.setItemAnimator(new DefaultItemAnimator());
    mainCategoriesRecyclerView.setHasFixedSize(true);
    mainSwipeRefreshLayout.setOnRefreshListener(() -> {
      if (currentHotel==null) notifyMessage("Still looking for you :|");
      else fetchFoodItemsFromCurrentHotel();
    });
  }

  private void scanForBeacons() {
    messageListener = new MessageListener() {
      @Override
      public void onFound(Message message) {
        super.onFound(message);
        Nearby.getMessagesClient(MainActivity.this).unsubscribe(messageListener);
        String rootString = new String(message.getContent());
        String hotelUid = rootString.split(":")[1];
        fetchCurrentHotel(hotelUid);
      }

      @Override
      public void onLost(Message message) {
        super.onLost(message);
        Nearby.getMessagesClient(MainActivity.this).unsubscribe(messageListener);
        String rootString = new String(message.getContent());
        String hotelUid = rootString.split(":")[1];
        fetchCurrentHotel(hotelUid);
      }
    };
  }

  private void fetchCurrentHotel(String hotelUid) {
    apiInterface.getHotel(hotelUid).enqueue(new Callback<Hotel>() {
      @Override
      public void onResponse(@NonNull Call<Hotel> call, @NonNull Response<Hotel> response) {
        currentHotel = response.body();
        setupHotelUi();
      }

      @Override
      public void onFailure(@NonNull Call<Hotel> call, @NonNull Throwable t) {
        notifyMessage(t.getMessage());
      }
    });
  }

  private void setupHotelUi() {
    mainHotelNameTextView.setText(currentHotel.getName());
    fetchFoodItemsFromCurrentHotel();
  }

  private void fetchFoodItemsFromCurrentHotel() {
    apiInterface.getFoodsFromHotel(currentHotel.getId(), currentHotel.getUid()).enqueue(
        new Callback<ArrayList<Food>>() {
          @Override
          public void onResponse(@NonNull Call<ArrayList<Food>> call, @NonNull Response<ArrayList<Food>> response) {
            //noinspection unchecked
            new SortFoodIntoCategories().execute(response.body());
          }

          @Override
          public void onFailure(@NonNull Call<ArrayList<Food>> call, @NonNull Throwable t) {
            notifyMessage(t.getMessage());
          }
        });
  }

  @SuppressLint("StaticFieldLeak")
  private class SortFoodIntoCategories extends AsyncTask<ArrayList<Food>, Void, ArrayList<Category>> {

    @SafeVarargs
    @Override
    protected final ArrayList<Category> doInBackground(ArrayList<Food>... arrayLists) {
      ArrayList<Category> categoryArrayList = new ArrayList<>();
      String[] categoryNamesArray = getResources().getStringArray(R.array.categories);
      for (String categoryName: categoryNamesArray) {
        ArrayList<Food> tempFoodArrayList = new ArrayList<>();
        for (Food food: arrayLists[0]) {
          if (food.getCategory().equalsIgnoreCase(categoryName)) tempFoodArrayList.add(food);
        }
        if (!tempFoodArrayList.isEmpty()) categoryArrayList.add(new Category(tempFoodArrayList));
      }
      return categoryArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<Category> categoryArrayList) {
      super.onPostExecute(categoryArrayList);
      categories.clear();
      categories.addAll(categoryArrayList);
      categoryItemsRecyclerViewAdapter = new CategoryItemsRecyclerViewAdapter(getApplicationContext(), categories);
      mainCategoriesRecyclerView.setAdapter(categoryItemsRecyclerViewAdapter);
      mainSwipeRefreshLayout.setRefreshing(false);
    }
  }

  @OnClick(R.id.mainCheckoutFab)
  public void onCheckoutButtonPress() {
    //noinspection unchecked
    new ExtractSelectedFoods().execute(categories);
  }

  @SuppressLint("StaticFieldLeak")
  private class ExtractSelectedFoods extends AsyncTask<ArrayList<Category>, Void, ArrayList<Food>> {

    @SafeVarargs
    @Override
    protected final ArrayList<Food> doInBackground(ArrayList<Category>... arrayLists) {
      ArrayList<Food> foodArrayList = new ArrayList<>();
      for (Category category: arrayLists[0]) {
        for (Food food: category.getFoodArrayList()) if (food.isFoodSelcted()) foodArrayList.add(food);
      }
      return foodArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<Food> foodArrayList) {
      super.onPostExecute(foodArrayList);
      Intent cartActivityIntent = new Intent(MainActivity.this, CartActivity.class);
      cartActivityIntent.putExtra("FOOD", foodArrayList);
      startActivity(cartActivityIntent);
    }
  }

  private void notifyMessage(String message) {
    if (mainSwipeRefreshLayout.isRefreshing()) mainSwipeRefreshLayout.setRefreshing(false);
    Snackbar.make(mainCategoriesRecyclerView, message, Snackbar.LENGTH_SHORT).show();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menuItemSignOut:
        firebaseAuth.signOut();
        startActivity(new Intent(MainActivity.this, SignInActivity.class));
        finish();
        break;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onStart() {
    super.onStart();
    scanForBeacons();
    Nearby.getMessagesClient(MainActivity.this).subscribe(messageListener);
  }

  @Override
  protected void onStop() {
    Nearby.getMessagesClient(MainActivity.this).unsubscribe(messageListener);
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    unbinder.unbind();
    super.onDestroy();
  }
}

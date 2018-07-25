package io.github.abhishekwl.flavrclient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import io.github.abhishekwl.flavrclient.Helpers.ApiClient;
import io.github.abhishekwl.flavrclient.Helpers.ApiInterface;
import io.github.abhishekwl.flavrclient.Models.User;
import io.github.abhishekwl.flavrclient.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

  private FirebaseAuth firebaseAuth;
  private ApiInterface apiInterface;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
    setContentView(R.layout.activity_splash);

    initializeComponents();
    initializeViews();
  }

  private void initializeViews() {
    setupHandler();
  }

  private void setupHandler() {
    if (firebaseAuth.getCurrentUser()==null) {
      startActivity(new Intent(SplashActivity.this, SignInActivity.class));
      finish();
    } else fetchCurrentUser();
  }

  private void fetchCurrentUser() {
    apiInterface.getUser(firebaseAuth.getUid()).enqueue(new Callback<User>() {
      @Override
      public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
        Intent mainActivityIntent = new Intent(SplashActivity.this, MainActivity.class);
        mainActivityIntent.putExtra("USER", response.body());
        startActivity(mainActivityIntent);
        finish();
      }

      @Override
      public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
      }
    });
  }

  private void initializeComponents() {
    firebaseAuth = FirebaseAuth.getInstance();
    apiInterface = ApiClient.getClient(getApplicationContext()).create(ApiInterface.class);
  }
}

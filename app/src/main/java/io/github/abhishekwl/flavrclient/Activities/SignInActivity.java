package io.github.abhishekwl.flavrclient.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.google.firebase.auth.FirebaseAuth;
import io.github.abhishekwl.flavrclient.Helpers.ApiClient;
import io.github.abhishekwl.flavrclient.Helpers.ApiInterface;
import io.github.abhishekwl.flavrclient.Models.User;
import io.github.abhishekwl.flavrclient.R;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

  @BindView(R.id.signInEmailEditText)
  TextInputEditText signInEmailEditText;
  @BindView(R.id.signInPasswordEditText)
  TextInputEditText signInPasswordEditText;
  @BindView(R.id.signInButton)
  Button signInButton;
  @BindView(R.id.signInSignUpTextView)
  TextView signInSignUpTextView;

  private FirebaseAuth firebaseAuth;
  private Unbinder unbinder;
  private ApiInterface apiInterface;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_in);

    unbinder = ButterKnife.bind(SignInActivity.this);
    initializeComponents();
    initializeViews();
  }

  private void initializeComponents() {
    firebaseAuth = FirebaseAuth.getInstance();
    apiInterface = ApiClient.getClient(getApplicationContext()).create(ApiInterface.class);
  }

  private void initializeViews() {

  }

  @SuppressLint("SetTextI18n")
  private void notifyMessage(String message) {
    if (!signInButton.isEnabled()) {
      signInButton.setEnabled(true);
      signInButton.setText("Sign in");
    }
    Snackbar.make(signInButton, message, Snackbar.LENGTH_SHORT).show();
  }

  @OnClick(R.id.signInButton)
  public void onSignInButtonPress() {
    String clientEmailId = signInEmailEditText.getText().toString();
    String clientPassword = signInPasswordEditText.getText().toString();

    if (TextUtils.isEmpty(clientEmailId) || TextUtils.isEmpty(clientPassword)) notifyMessage("Please fill both the fields.");
    else signInUser(clientEmailId, clientPassword);
  }

  @SuppressLint("SetTextI18n")
  private void signInUser(String clientEmailId, String clientPassword) {
    signInButton.setText("Signing in..");
    signInButton.setEnabled(false);

    firebaseAuth.signInWithEmailAndPassword(clientEmailId, clientPassword).addOnCompleteListener(
        task -> {
          if (task.isSuccessful()) fetchCurrentUser();
          else notifyMessage(Objects.requireNonNull(task.getException()).getMessage());
        });
  }

  private void fetchCurrentUser() {
    apiInterface.getUser(firebaseAuth.getUid()).enqueue(new Callback<User>() {
      @Override
      public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
        Intent mainActivityIntent = new Intent(SignInActivity.this, MainActivity.class);
        mainActivityIntent.putExtra("USER", response.body());
        startActivity(mainActivityIntent);
        finish();
      }

      @Override
      public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
        notifyMessage(t.getMessage());
      }
    });
  }

  @OnClick(R.id.signInSignUpTextView)
  public void onSignUpTextViewPress() {
    startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
  }

  @Override
  protected void onDestroy() {
    unbinder.unbind();
    super.onDestroy();
  }
}

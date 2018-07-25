package io.github.abhishekwl.flavrclient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.google.firebase.auth.FirebaseAuth;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.abhishekwl.flavrclient.Helpers.ApiClient;
import io.github.abhishekwl.flavrclient.Helpers.ApiInterface;
import io.github.abhishekwl.flavrclient.Models.User;
import io.github.abhishekwl.flavrclient.R;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

  @BindView(R.id.signUpProfilePictureImageView)
  CircleImageView signUpProfilePictureImageView;
  @BindView(R.id.signUpNameEditText)
  TextInputEditText signUpNameEditText;
  @BindView(R.id.signUpPhoneEditText)
  TextInputEditText signUpPhoneEditText;
  @BindView(R.id.signUpEmailEditText)
  TextInputEditText signUpEmailEditText;
  @BindView(R.id.signUpPasswordEditText)
  TextInputEditText signUpPasswordEditText;
  @BindView(R.id.signUpConfirmPasswordEditText)
  TextInputEditText signUpConfirmPasswordEditText;
  @BindView(R.id.signUpButton)
  Button signUpButton;

  private Unbinder unbinder;
  private ApiInterface apiInterface;
  private FirebaseAuth firebaseAuth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_up);

    unbinder = ButterKnife.bind(SignUpActivity.this);
    initializeComponents();
    initializeViews();
  }

  private void initializeComponents() {
    firebaseAuth = FirebaseAuth.getInstance();
    apiInterface = ApiClient.getClient(getApplicationContext()).create(ApiInterface.class);
  }

  private void initializeViews() {

  }

  @OnClick(R.id.signUpButton)
  public void onSignUpButtonPress() {
    String clientName = signUpNameEditText.getText().toString();
    String clientPhone = signUpPhoneEditText.getText().toString();
    String clientEmail = signUpEmailEditText.getText().toString();
    String clientPassword = signUpPasswordEditText.getText().toString();
    String clientConfirmPassword = signUpConfirmPasswordEditText.getText().toString();

    if (TextUtils.isEmpty(clientName) || TextUtils.isEmpty(clientPhone) || TextUtils.isEmpty(clientEmail) || TextUtils.isEmpty(clientPassword))
      notifyMessage("Please fill all the fields.");
    else if (!clientPassword.equals(clientConfirmPassword)) notifyMessage("Passwords do not match.");
    else signUpUser(clientName, clientPhone, clientEmail, clientPassword);
  }

  private void signUpUser(String clientName, String clientPhone, String clientEmail, String clientPassword) {
    signUpButton.setText("Signing up..");
    signUpButton.setEnabled(false);

    firebaseAuth.createUserWithEmailAndPassword(clientEmail, clientPassword).addOnCompleteListener(
        task -> {
          if (task.isSuccessful()) createNewUser(clientName, clientPhone, clientEmail);
          else notifyMessage(Objects.requireNonNull(task.getException()).getMessage());
        });
  }

  private void createNewUser(String clientName, String clientPhone, String clientEmail) {
    apiInterface.createUser(firebaseAuth.getUid(), clientName, clientEmail, clientPhone, "").enqueue(
        new Callback<User>() {
          @Override
          public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
            Intent mainActivityIntent = new Intent(SignUpActivity.this, MainActivity.class);
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

  private void notifyMessage(String message) {
    if (!signUpButton.isEnabled()) {
      signUpButton.setText("Sign up");
      signUpButton.setEnabled(false);
    }
    Snackbar.make(signUpButton, message, Snackbar.LENGTH_SHORT).show();
  }

  @Override
  protected void onDestroy() {
    unbinder.unbind();
    super.onDestroy();
  }
}

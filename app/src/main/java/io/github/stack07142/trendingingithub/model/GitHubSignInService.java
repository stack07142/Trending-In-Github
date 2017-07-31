package io.github.stack07142.trendingingithub.model;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GithubAuthProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import io.github.stack07142.trendingingithub.util.DebugLog;
import io.github.stack07142.trendingingithub.util.ResultCode;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GitHubSignInService extends AppCompatActivity {

    private final String TAG = GitHubSignInService.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private final String GITHUB_CLIENT_ID = "3447b7de47287871b562";
    private final String GITHUB_CLIENT_SECRET = "23989ba2dc5b9049ab21984e0c7a8505a68147c9";
    private final String GITHUB_TOKEN = "b0bbbc8aafad37edd7d7426f53038642a2de8d87";
    private final String GITHUB_REDIRECT_URL = "stack07142://git.oauth2token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                // User is signed In
                if (user != null) {

                    DebugLog.logD(TAG, "User is signed In");
                    Toast.makeText(GitHubSignInService.this, "SUCCESS -> change to Snackbar", Toast.LENGTH_SHORT).show();

                    finish();
                }
                // User is signed Out
                else {

                    DebugLog.logD(TAG, "User is signed Out");
                }
            }
        }; // ~mAuthListener

        Bundle bundle = getIntent().getExtras();
        int requestCode = bundle.getInt(ResultCode.REQUEST_CODE, ResultCode.NONE);

        DebugLog.logD(TAG, "requestCode = " + requestCode);

        // Sign In
        if (requestCode == ResultCode.REQUEST_GITHUB_SIGNIN) {

            /**
             * Github - Web Application Flow
             *
             * The flow to authorize users for your app is:
             * 1. Users are redirected to request their GitHub identity
             * 2. Users are redirected back to your site by GitHub
             * 3. Your GitHub App accesses the API with the user's access token
             */

            // 1. Users are redirected to request their GitHub identity
            // GET http://github.com/login/oauth/authorize
            HttpUrl httpUrl = new HttpUrl.Builder()
                    .scheme("http")
                    .host("github.com")
                    .addPathSegment("login")
                    .addPathSegment("oauth")
                    .addPathSegment("authorize")
                    .addQueryParameter("client_id", GITHUB_CLIENT_ID)
                    .addQueryParameter("redirect_uri", GITHUB_REDIRECT_URL)
                    .addQueryParameter("state", getRandomString())
                    .addQueryParameter("scope", "user:email")
                    .build();

            DebugLog.logD(TAG, "httpUrl = " + httpUrl.toString());

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(httpUrl.toString()));
            startActivity(intent);
        }

        // Called after the GitHub server redirect us to GITHUB_REDIRECT_URL
        Uri uri = getIntent().getData();

        if (uri != null && uri.toString().startsWith(GITHUB_REDIRECT_URL)) {

            String code = uri.getQueryParameter("code");
            String state = uri.getQueryParameter("state");

            if (code != null && state != null) {

                DebugLog.logD(TAG, "code = " + code);
                DebugLog.logD(TAG, "state = " + state);

                // 2. Users are redirected back to your site by GitHub
                // POST https://github.com/login/oauth/access_token
                sendPost(code, state);
            } else {

                // TODO : Set Result - Error
            }
        }
    } // ~onCreate

    /**
     * An unguessable random string.
     * It is used to protect against cross-site request forgery attacks.
     */
    private String getRandomString() {

        return new BigInteger(130, new Random()).toString(32);
    }

    private void sendPost(String code, String state) {

        OkHttpClient okHttpClient = new OkHttpClient();

        FormBody form = new FormBody.Builder()
                .add("client_id", GITHUB_CLIENT_ID)
                .add("client_secret", GITHUB_CLIENT_SECRET)
                .add("code", code)
                .add("redirect_uri", GITHUB_REDIRECT_URL)
                .add("state", state)
                .build();

        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(form)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                e.printStackTrace();
                // TODO : Set Result - Error
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                // Response form : access_token=e72e16c7e42f292c6912e7710c838347ae178b4a&token_type=bearer
                String responseBody = response.body().string();
                String[] splittedBody = responseBody.split("=|&");

                DebugLog.logD(TAG, "responseBody = " + responseBody);

                if (splittedBody[0].equalsIgnoreCase("access_token")) {

                    signInWithToken(splittedBody[1]);
                } else {

                    // TODO : Set Result - Error
                }
            }
        });
    }

    private void signInWithToken(String token) {

        DebugLog.logD(TAG, "signInWithToken()");

        // credential object from the token
        AuthCredential credential = GithubAuthProvider.getCredential(token);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            DebugLog.logD(TAG, "signInWithCredential, task-onComplete");
                        } else {

                            DebugLog.logD(TAG, "signInWithCredential, getException = ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        // TODO : Set Result - Error
                        DebugLog.logD(TAG, "signInWithCredential, task-onFailure");
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mAuth.removeAuthStateListener(mAuthListener);
    }
}

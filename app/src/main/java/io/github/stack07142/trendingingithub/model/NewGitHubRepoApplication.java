package io.github.stack07142.trendingingithub.model;

import android.app.Application;

import io.github.stack07142.trendingingithub.BuildConfig;
import io.github.stack07142.trendingingithub.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class NewGitHubRepoApplication extends Application {

    Retrofit retrofit;
    private GitHubRepoService gitHubRepoService;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        setupAPIClient();
    }

    private void setupAPIClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {

            @Override
            public void log(String message) {

                Timber.d(message);
            }
        });

        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(
                        chain -> {
                            Request request = chain.request();
                            Request newReq =
                                    request.newBuilder()
                                            .addHeader("Authorization", String.format("token %s", getString(R.string.github_oauth_token)))
                                            .build();
                            return chain.proceed(newReq);
                        })
                .build();

        retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        gitHubRepoService = retrofit.create(GitHubRepoService.class);
    }

    public GitHubRepoService getGitHubRepoService() {

        return gitHubRepoService;
    }
}

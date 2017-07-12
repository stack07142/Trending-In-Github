package io.github.stack07142.trendingingithub.model;

import android.app.Application;

import io.github.stack07142.trendingingithub.util.DebugLog;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewGitHubRepoApplication extends Application {

    Retrofit retrofit;
    private GitHubService gitHubService;

    @Override
    public void onCreate() {
        super.onCreate();

        setupAPIClient();
    }

    private void setupAPIClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {

            @Override
            public void log(String message) {

                DebugLog.logD("API LOG", message);
            }
        });

        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

        retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                //.baseUrl("https://api.github.com")
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        gitHubService = retrofit.create(GitHubService.class);
    }

    public GitHubService getGitHubService() {

        return gitHubService;
    }
}

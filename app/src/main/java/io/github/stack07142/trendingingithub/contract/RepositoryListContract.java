package io.github.stack07142.trendingingithub.contract;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.github.stack07142.trendingingithub.model.GitHubService;

/**
 * Presenter <-> View 통신 인터페이스
 */

public interface RepositoryListContract {

    // TypeDef - IntDef
    // Constants
    public static final int SUCCESS = 0;
    public static final int FAIL = 1;

    // Declare the @IntDef for these contants
    @IntDef({SUCCESS, FAIL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Result {
    }


    /**
     * MVP의 View가 구현할 인터페이스
     * Presenter가 View를 조작할 때 사용
     */
    interface View {

        String getSelectedLanguage();

        void showProgress();

        void hideProgress();

        void showRepositories(GitHubService.Repositories repositories);

        void showNoti(@Result int result);

        void startDetailActivity(String fullRepositoryName);
    }

    /**
     * MVP의 Presenter가 구현할 인터페이스
     * View가 Presenter에 Event를 알릴 때 사용
     */
    interface UserAction {

        void selectLanguage(String language);

        void selectRepositoryItem(GitHubService.RepositoryItem item);
    }
}

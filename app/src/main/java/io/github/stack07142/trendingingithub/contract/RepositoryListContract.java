package io.github.stack07142.trendingingithub.contract;

import java.util.ArrayList;

import io.github.stack07142.trendingingithub.model.GitHubService;
import io.github.stack07142.trendingingithub.util.ResultCode;

/**
 * Presenter <-> View 통신 인터페이스
 */

public interface RepositoryListContract {

    /**
     * MVP의 View가 구현할 인터페이스
     * Presenter가 View를 조작할 때 사용
     */
    interface View {

        String getSelectedLanguage();

        void showProgress();

        void hideProgress();

        void showRepositories(GitHubService.Repositories repositories);

        void showNoti(@ResultCode.Result int result);

        void startDetailActivity(String fullRepositoryName);
    }

    /**
     * MVP의 Presenter가 구현할 인터페이스
     * View가 Presenter에 Event를 알릴 때 사용
     */
    interface UserAction {

        void selectLanguage(ArrayList<String> languages, String period);

        void selectRepositoryItem(GitHubService.RepositoryItem item);

    }
}

package io.github.stack07142.trendingingithub.contract;

import io.github.stack07142.trendingingithub.model.GitHubService;

public interface DetailRepositoryContract {

    /**
     * MVP의 View가 구현할 인터페이스
     * Presenter가 View를 조작할 때 이용
     */
    interface View {

        String getFullRepositoryName();

        void showRepositoryInfo(GitHubService.RepositoryItem response);

        void startBrowser(String url);

        void showError(String message);
    }

    /**
     * MVP의 Presenter가 구현할 인터페이스
     * View가 Presenter에 Event를 알릴 때 사용
     */
    interface UserActions {

        void titleClick();

        void prepare();
    }
}

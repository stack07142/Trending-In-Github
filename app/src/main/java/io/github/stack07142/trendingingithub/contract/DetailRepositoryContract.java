package io.github.stack07142.trendingingithub.contract;

import io.github.stack07142.trendingingithub.model.GitHubService;
import io.github.stack07142.trendingingithub.util.ResultCode;

public interface DetailRepositoryContract {

    /**
     * MVP의 View가 구현할 인터페이스
     * Presenter가 View를 조작할 때 이용
     */
    interface View {

        String getFullRepositoryName();

        void showProgress();

        void hideProgress();

        void showLayout();

        void hideLayout();

        void showRepositoryInfo(GitHubService.RepositoryItem response);

        void startBrowser(String url);

        void showNoti(@ResultCode.Result int result);
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

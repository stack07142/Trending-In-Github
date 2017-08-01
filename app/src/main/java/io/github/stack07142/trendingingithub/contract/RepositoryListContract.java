package io.github.stack07142.trendingingithub.contract;

import java.util.ArrayList;

import io.github.stack07142.trendingingithub.model.GitHubRepoService;
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

        void showProgress();

        void hideProgress();

        void showRepositories(ArrayList<GitHubRepoService.RepositoryItem> repositories);

        void showEmptyScreen();

        void showNoti(@ResultCode.Result int result);

        void startDetailActivity(String fullRepositoryName);

        void startSignInOutActivity(@ResultCode.Result int requestCode);
    }

    /**
     * MVP의 Presenter가 구현할 인터페이스
     * View가 Presenter에 Event를 알릴 때 사용
     */
    interface UserAction {

        void selectLanguage(ArrayList<String> languages, String period);

        void selectRepositoryItem(GitHubRepoService.RepositoryItem item);

        void selectSignInMenu();

        void selectSignOutMenu();

        void selectStar();

        void unselectStar();

        void completeSignInOut(@ResultCode.Result int resultCode);
    }
}

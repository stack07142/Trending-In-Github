package io.github.stack07142.trendingingithub.contract;

import io.github.stack07142.trendingingithub.util.ResultCode;

/**
 * Presenter <-> View 통신 인터페이스
 */

public interface SignInOutContract {

    /**
     * MVP의 View가 구현할 인터페이스
     * Presenter가 View를 조작할 때 사용
     */
    interface View {

        void setResult(@ResultCode.Result int requestCode);

        void finishView();
    }

    /**
     * MVP의 Presenter가 구현할 인터페이스
     * View가 Presenter에 Event를 알릴 때 사용
     */
    interface UserAction {

        void signIn();

        void signOut();

        void githubRedirect(String code, String state);
    }
}

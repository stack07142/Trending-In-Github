package io.github.stack07142.trendingingithub.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import br.tiagohm.markdownview.css.styles.Github;
import io.github.stack07142.trendingingithub.R;
import io.github.stack07142.trendingingithub.databinding.MarkdownViewBinding;
import io.github.stack07142.trendingingithub.util.BaseActivityUtil;

import static io.github.stack07142.trendingingithub.util.ResultCode.README_DOWNLOAD_URL;

public class MarkDownViewActivity extends BaseActivityUtil {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String readme_download_url = intent.getStringExtra(README_DOWNLOAD_URL);

        MarkdownViewBinding mBinding = DataBindingUtil.setContentView(this, R.layout.markdown_view);

        mBinding.markdownView.addStyleSheet(new Github());
        mBinding.markdownView.loadMarkdownFromUrl(readme_download_url);
    }
}

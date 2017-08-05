package io.github.stack07142.trendingingithub.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.List;

import io.github.stack07142.trendingingithub.R;
import io.github.stack07142.trendingingithub.databinding.RepoItemBinding;
import io.github.stack07142.trendingingithub.model.GitHubRepoService;
import io.github.stack07142.trendingingithub.model.LanguageColorsData;

class RepositoryAdapter extends RecyclerView.Adapter<RepositoryAdapter.RepoViewHolder> {

    private final Context context;
    private final OnRepoItemClickListener onRepoItemClickListener;

    private List<GitHubRepoService.RepositoryItem> items;

    private final int CONTENT_TYPE = 0;
    private final int AD_TYPE = 1;

    // RepositoryAdapter - Constructor
    RepositoryAdapter(Context context, OnRepoItemClickListener onRepoItemClickListener) {

        this.context = context;
        this.onRepoItemClickListener = onRepoItemClickListener;
    }

    /**
     * 리포지토리의 데이터를 설정해서 갱신한다
     */
    void setItemsAndRefresh(List<GitHubRepoService.RepositoryItem> items) {

        this.items = items;
        notifyDataSetChanged();
    }

    private GitHubRepoService.RepositoryItem getItemAt(int position) {

        return items.get(position);
    }

    /**
     * RecyclerView의 아이템 뷰 생성과 뷰를 유지할 ViewHolder를 생성
     */
    @Override
    public RepoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        AdView adView;
        RepoViewHolder repoViewHolder;

        if (viewType == AD_TYPE) {

            adView = new AdView(context);
            adView.setAdSize(AdSize.BANNER);

            adView.setAdUnitId(context.getString(R.string.banner_ad_unit_id));

            float density = context.getResources().getDisplayMetrics().density;
            int height = Math.round(AdSize.BANNER.getHeight() * density);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height);
            adView.setLayoutParams(params);

            AdRequest request = new AdRequest.Builder().build();
            adView.loadAd(request);

            repoViewHolder = new RepoViewHolder(adView);
        } else {

            final View view = LayoutInflater.from(context).inflate(R.layout.repo_item, parent, false);
            repoViewHolder = new RepoViewHolder(view);
        }

        return repoViewHolder;
    }

    /**
     * onCreateViewHolder로 만든 ViewHolder의 뷰에
     * setItemsAndRefresh(items)으로 설정된 데이터를 넣는다
     */
    @Override
    public void onBindViewHolder(final RepoViewHolder holder, int position) {

        if (position % 6 != 3) {
            if (true) {

                final GitHubRepoService.RepositoryItem item = getItemAt(position);

                holder.bindItem(item);
                final RepoItemBinding binding = holder.getBinding();

                // 뷰가 클릭되면 클릭된 아이템을 Listener에게 알린다
                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        onRepoItemClickListener.onRepositoryItemClick(item);
                    }
                });

                // Repo의 Language가 null인 경우 language icon을 표시하지 않는다
                if (item.language == null) {

                    holder.binding.repoLanguageIcon.setVisibility(View.GONE);
                } else {

                    holder.binding.repoLanguageIcon.setVisibility(View.VISIBLE);

                    // Change shape_language_icon color dynamically
                    GradientDrawable bgShape = (GradientDrawable) binding.repoLanguageIcon.getBackground();
                    bgShape.setColor(new LanguageColorsData().getColor(item.language));
                }

                // 이미지는 Glide라는 라이브러리로 데이터를 설정한다
                Glide.with(context)
                        .load(item.owner.avatar_url)
                        .asBitmap().centerCrop().into(new BitmapImageViewTarget(binding.repoImage) {

                    @Override
                    protected void setResource(Bitmap resource) {
                        // 이미지를 동그랗게 만든다
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        binding.repoImage.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (position % 6 == 3) {

            return AD_TYPE;
        }

        return CONTENT_TYPE;
    }

    @Override
    public int getItemCount() {

        if (items == null) {
            return 0;
        }
        return items.size();
    }

    static class RepoViewHolder extends RecyclerView.ViewHolder {

        private RepoItemBinding binding;

        RepoViewHolder(View itemView) {
            super(itemView);

            if (!(itemView instanceof AdView)) {

                binding = DataBindingUtil.bind(itemView);
            }
        }

        void bindItem(GitHubRepoService.RepositoryItem item) {

            binding.setRepository(item);
        }

        RepoItemBinding getBinding() {

            return binding;
        }

    }

    interface OnRepoItemClickListener {

        void onRepositoryItemClick(GitHubRepoService.RepositoryItem item);
    }
}

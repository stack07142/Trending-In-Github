package io.github.stack07142.trendingingithub.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.List;

import io.github.stack07142.trendingingithub.model.GitHubService;
import io.github.stack07142.trendingingithub.R;

public class RepositoryAdapter extends RecyclerView.Adapter<RepositoryAdapter.RepoViewHolder> {

    private final Context context;
    private final OnRepoItemClickListener onRepoItemClickListener;

    private List<GitHubService.RepositoryItem> items;

    // RepositoryAdapter - Constructor
    public RepositoryAdapter(Context context, OnRepoItemClickListener onRepoItemClickListener) {

        this.context = context;
        this.onRepoItemClickListener = onRepoItemClickListener;
    }

    /**
     * 리포지토리의 데이터를 설정해서 갱신한다
     *
     * @param items
     */
    public void setItemsAndRefresh(List<GitHubService.RepositoryItem> items) {

        this.items = items;
        notifyDataSetChanged();
    }

    public GitHubService.RepositoryItem getItemAt(int position) {

        return items.get(position);
    }

    /**
     * RecyclerView의 아이템 뷰 생성과 뷰를 유지할 ViewHolder를 생성
     */
    @Override
    public RepoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View view = LayoutInflater.from(context).inflate(R.layout.repo_item, parent, false);

        return new RepoViewHolder(view);
    }

    /**
     * onCreateViewHolder로 만든 ViewHolder의 뷰에
     * setItemsAndRefresh(items)으로 설정된 데이터를 넣는다
     */
    @Override
    public void onBindViewHolder(final RepoViewHolder holder, int position) {

        final GitHubService.RepositoryItem item = getItemAt(position);

        // 뷰가 클릭되면 클릭된 아이템을 Listener에게 알린다
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRepoItemClickListener.onRepositoryItemClick(item);
            }
        });

        holder.name.setText(item.name);
        holder.detail.setText(item.description);
        holder.nStar.setText(item.stargazers_count);

        // 이미지는 Glide라는 라이브러리로 데이터를 설정한다
        Glide.with(context)
                .load(item.owner.avatar_url)
                .asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.image) {

            @Override
            protected void setResource(Bitmap resource) {
                // 이미지를 동그랗게 만든다
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                holder.image.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    @Override
    public int getItemCount() {

        if (items == null) {
            return 0;
        }
        return items.size();
    }

    static class RepoViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView detail;
        private final ImageView image;
        private final TextView nStar;

        public RepoViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.repo_name);
            detail = (TextView) itemView.findViewById(R.id.repo_detail);
            image = (ImageView) itemView.findViewById(R.id.repo_image);
            nStar = (TextView) itemView.findViewById(R.id.repo_star);
        }
    }

    interface OnRepoItemClickListener {

        void onRepositoryItemClick(GitHubService.RepositoryItem item);
    }
}

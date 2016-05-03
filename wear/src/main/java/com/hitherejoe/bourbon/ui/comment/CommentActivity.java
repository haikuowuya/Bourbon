package com.hitherejoe.bourbon.ui.comment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hitherejoe.bourbon.R;
import com.hitherejoe.bourboncommon.common.data.model.Comment;
import com.hitherejoe.bourboncommon.common.data.model.Shot;
import com.hitherejoe.bourboncommon.common.ui.shot.ShotMvpView;
import com.hitherejoe.bourboncommon.common.ui.shot.ShotPresenter;
import com.hitherejoe.bourbon.ui.base.BaseActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CommentActivity extends BaseActivity implements ShotMvpView {

    public static final String EXTRA_SHOT =
            "com.hitherejoe.bourbon.ui.comment.CommentActivity.EXTRA_SHOT";

    @Bind(R.id.image_message) ImageView mErrorImage;
    @Bind(R.id.page_indicator) PagerIndicatorView mPagerIndicatorView;
    @Bind(R.id.progress) ProgressBar mProgress;
    @Bind(R.id.layout_footer) RelativeLayout mFooterlayout;
    @Bind(R.id.text_messager) TextView mErrorText;
    @Bind(R.id.layout_message) View mErrorView;
    @Bind(R.id.pager_comments) ViewPager mShotsPager;

    @Inject ShotPresenter mShotPresenter;

    private CommentsAdapter mCommentAdapter;

    public static Intent newIntent(Context context, Shot shot) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra(EXTRA_SHOT, shot);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        activityComponent().inject(this);

        Shot shot = getIntent().getParcelableExtra(EXTRA_SHOT);

        if (shot == null) {
            throw new IllegalArgumentException("CommentActivity requires a shot instance!");
        }

        mShotPresenter.attachView(this);
        mCommentAdapter = new CommentsAdapter(this);
        mShotsPager.setAdapter(mCommentAdapter);

        mShotPresenter.getComments(shot.id, ShotPresenter.SHOT_COUNT, ShotPresenter.SHOT_PAGE);
    }

    @Override
    public void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void showComments(List<Comment> comments) {
        mCommentAdapter.setComments(comments);
        mCommentAdapter.notifyDataSetChanged();
        mPagerIndicatorView.attachViewPager(mShotsPager);
        mPagerIndicatorView.bringToFront();
        setUIErrorState(false);
    }

    @Override
    public void showError() {
        mErrorImage.setImageResource(R.drawable.ic_sentiment_very_dissatisfied_gray_48dp);
        mErrorText.setText(getString(R.string.text_error_loading_shots));
        setUIErrorState(true);
    }

    @Override
    public void showEmptyComments() {
        mErrorImage.setImageResource(R.drawable.ic_empty_glass_gray_48dp);
        mErrorText.setText(getString(R.string.text_no_recent_comments));
        setUIErrorState(true);
    }

    private void setUIErrorState(boolean isError) {
        mShotsPager.setVisibility(isError ? View.GONE : View.VISIBLE);
        mFooterlayout.setVisibility(isError ? View.GONE : View.VISIBLE);
        mErrorView.setVisibility(isError ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showCommentsTitle(boolean hasComments) {

    }

}
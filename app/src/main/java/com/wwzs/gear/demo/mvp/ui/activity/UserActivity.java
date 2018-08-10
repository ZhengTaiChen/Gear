package com.wwzs.gear.demo.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.paginate.Paginate;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wwzs.gear.base.BaseActivity;
import com.wwzs.gear.demo.R;
import com.wwzs.gear.demo.di.component.DaggerUserComponent;
import com.wwzs.gear.demo.di.module.UserModule;
import com.wwzs.gear.demo.mvp.contract.UserContract;
import com.wwzs.gear.demo.mvp.model.entity.User;
import com.wwzs.gear.demo.mvp.presenter.UserPresenter;
import com.wwzs.gear.di.component.AppComponent;
import com.wwzs.gear.http.imageloader.ImageLoader;
import com.wwzs.gear.http.imageloader.glide.ImageConfigImpl;
import com.wwzs.gear.utils.ArmsUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wwzs.gear.utils.Preconditions.checkNotNull;


public class UserActivity extends BaseActivity<UserPresenter> implements UserContract.View, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Inject
    RxPermissions mRxPermissions;
    @Inject
    RecyclerView.LayoutManager mLayoutManager;

    BaseQuickAdapter<User, BaseViewHolder> mAdapter;

    private Paginate mPaginate;
    private boolean isLoadingMore;
    private ImageLoader mImageLoader;//用于加载图片的管理类,默认使用 Glide,使用策略模式,可替换框架

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerUserComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .userModule(new UserModule(this))
                .build()
                .inject(this);
        mImageLoader = appComponent.imageLoader();

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_user; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initRecyclerView();
        mRecyclerView.setAdapter(mAdapter = new BaseQuickAdapter<User, BaseViewHolder>(R.layout.recycle_list) {
            @Override
            protected void convert(BaseViewHolder helper, User item) {
                helper.setText(R.id.tv_name, item.getLogin());
                mImageLoader.loadImage(UserActivity.this,
                        ImageConfigImpl
                                .builder()
                                .url(item.getAvatarUrl())
                                .imageView(helper.getView(R.id.iv_avatar))
                                .build());


            }
        });
        initPaginate();
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        ArmsUtils.configRecyclerView(mRecyclerView, mLayoutManager);
    }


    @Override
    public void showLoading() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

    /**
     * 开始加载更多
     */
    @Override
    public void startLoadMore() {
        isLoadingMore = true;
    }

    /**
     * 结束加载更多
     */
    @Override
    public void endLoadMore() {
        isLoadingMore = false;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public RxPermissions getRxPermissions() {
        return mRxPermissions;
    }

    @Override
    public BaseQuickAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new BaseQuickAdapter<User, BaseViewHolder>(R.layout.recycle_list) {
                @Override
                protected void convert(BaseViewHolder helper, User item) {
                    helper.setText(R.id.tv_name, item.getLogin());
                    mImageLoader.loadImage(UserActivity.this,
                            ImageConfigImpl
                                    .builder()
                                    .url(item.getAvatarUrl())
                                    .imageView(helper.getView(R.id.tv_prompt))
                                    .build());


                }
            };
        }
        return mAdapter;
    }

    /**
     * 初始化Paginate,用于加载更多
     */
    private void initPaginate() {
        if (mPaginate == null) {
            Paginate.Callbacks callbacks = new Paginate.Callbacks() {
                @Override
                public void onLoadMore() {
                    mPresenter.requestUsers(false);
                }

                @Override
                public boolean isLoading() {
                    return isLoadingMore;
                }

                @Override
                public boolean hasLoadedAllItems() {
                    return false;
                }
            };

            mPaginate = Paginate.with(mRecyclerView, callbacks)
                    .setLoadingTriggerThreshold(0)
                    .build();
            mPaginate.setHasMoreDataToLoad(false);
        }
    }

    @Override
    protected void onDestroy() {
//        DefaultAdapter.releaseAllHolder(mRecyclerView);//super.onDestroy()之后会unbind,所有view被置为null,所以必须在之前调用
        super.onDestroy();
        this.mRxPermissions = null;
        this.mPaginate = null;


    }

    @Override
    public void onRefresh() {
        mPresenter.requestUsers(true);
    }
}

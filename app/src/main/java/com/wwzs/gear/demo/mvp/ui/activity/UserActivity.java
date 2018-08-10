package com.wwzs.gear.demo.mvp.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wwzs.gear.base.BaseActivity;
import com.wwzs.gear.demo.di.component.DaggerUserComponent;
import com.wwzs.gear.demo.di.module.UserModule;
import com.wwzs.gear.demo.mvp.contract.UserContract;
import com.wwzs.gear.demo.mvp.presenter.UserPresenter;

import com.wwzs.gear.demo.R;
import com.wwzs.gear.di.component.AppComponent;
import com.wwzs.gear.utils.ArmsUtils;

import static com.wwzs.gear.utils.Preconditions.checkNotNull;


public class UserActivity extends BaseActivity<UserPresenter> implements UserContract.View {

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerUserComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .userModule(new UserModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_user; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

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

    @Override
    public void startLoadMore() {

    }

    @Override
    public void endLoadMore() {

    }

    @Override
    public Activity getActivity() {
        return null;
    }

    @Override
    public RxPermissions getRxPermissions() {
        return null;
    }
}

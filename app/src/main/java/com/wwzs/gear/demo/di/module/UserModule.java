package com.wwzs.gear.demo.di.module;



import dagger.Module;
import dagger.Provides;

import com.wwzs.gear.demo.mvp.contract.UserContract;
import com.wwzs.gear.demo.mvp.model.UserModel;
import com.wwzs.gear.di.scope.ActivityScope;


@Module
public class UserModule {
    private UserContract.View view;

    /**
     * 构建UserModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public UserModule(UserContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    UserContract.View provideUserView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    UserContract.Model provideUserModel(UserModel model) {
        return model;
    }
}
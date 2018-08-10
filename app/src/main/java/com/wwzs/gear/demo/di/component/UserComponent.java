package com.wwzs.gear.demo.di.component;

import dagger.Component;



import com.wwzs.gear.demo.di.module.UserModule;


import com.wwzs.gear.demo.mvp.ui.activity.UserActivity;
import com.wwzs.gear.di.component.AppComponent;
import com.wwzs.gear.di.scope.ActivityScope;

@ActivityScope
@Component(modules = UserModule.class, dependencies = AppComponent.class)
public interface UserComponent {
    void inject(UserActivity activity);
}
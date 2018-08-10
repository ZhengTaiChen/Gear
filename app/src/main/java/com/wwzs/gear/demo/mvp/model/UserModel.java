package com.wwzs.gear.demo.mvp.model;

import android.app.Application;

import com.google.gson.Gson;




import javax.inject.Inject;

import com.wwzs.gear.demo.mvp.contract.UserContract;
import com.wwzs.gear.demo.mvp.model.api.cache.CommonCache;
import com.wwzs.gear.demo.mvp.model.api.service.UserService;
import com.wwzs.gear.demo.mvp.model.entity.User;
import com.wwzs.gear.di.scope.ActivityScope;
import com.wwzs.gear.integration.IRepositoryManager;
import com.wwzs.gear.mvp.BaseModel;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;


@ActivityScope
public class UserModel extends BaseModel implements UserContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;
    public static final int USERS_PER_PAGE = 10;
    @Inject
    public UserModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<List<User>> getUsers(int lastIdQueried, boolean update) {
        //使用rxcache缓存,上拉刷新则不读取缓存,加载更多读取缓存
        return Observable.just(mRepositoryManager
                .obtainRetrofitService(UserService.class)
                .getUsers(lastIdQueried, USERS_PER_PAGE))
                .flatMap((Function<Observable<List<User>>, ObservableSource<List<User>>>) listObservable -> mRepositoryManager.obtainCacheService(CommonCache.class)
                        .getUsers(listObservable
                                , new DynamicKey(lastIdQueried)
                                , new EvictDynamicKey(update))
                        .map(listReply -> listReply.getData()));
    }
}
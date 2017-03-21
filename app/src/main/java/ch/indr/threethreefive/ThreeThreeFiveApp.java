package ch.indr.threethreefive;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class ThreeThreeFiveApp extends MultiDexApplication {

  private AppComponent component;

  @CallSuper
  @Override public void onCreate() {
    super.onCreate();

    // if (!BuildConfig.DEBUG && !isInUnitTests()) {
    if (!isInUnitTests()) {
      Fabric.with(this, new Crashlytics());
    }

    MultiDex.install(this);

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }

    component = DaggerAppComponent.builder()
        .appModule(getAppModule())
        .build();
    component().inject(this);
  }

  @NonNull protected AppModule getAppModule() {
    return new AppModule(this);
  }

  public AppComponent component() {
    return component;
  }

  public boolean isInUnitTests() {
    return false;
  }
}

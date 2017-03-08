package ch.indr.threethreefive;

import android.support.annotation.CallSuper;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;

import ch.indr.threethreefive.libs.ActivityStack;
import ch.indr.threethreefive.libs.ActivityStackType;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class ThreeThreeFiveApp extends MultiDexApplication {
  private AppComponent component;

  @Override
  @CallSuper
  public void onCreate() {
    super.onCreate();

    if (!BuildConfig.DEBUG && !isInUnitTests()) {
      Fabric.with(this, new Crashlytics());
    }

    MultiDex.install(this);

    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }

    ActivityStackType activityStack = new ActivityStack();
    registerActivityLifecycleCallbacks(activityStack);

    component = DaggerAppComponent.builder()
        .appModule(new AppModule(this, activityStack))
        .build();
    component().inject(this);
  }

  public AppComponent component() {
    return component;
  }

  public boolean isInUnitTests() {
    return false;
  }
}
/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.List;

public interface ActivityStackType extends Application.ActivityLifecycleCallbacks {
  List<String> getTitles();

  @Override void onActivityCreated(Activity activity, Bundle bundle);

  @Override void onActivityStarted(Activity activity);

  @Override void onActivityResumed(Activity activity);

  @Override void onActivityPaused(Activity activity);

  @Override void onActivityStopped(Activity activity);

  @Override void onActivitySaveInstanceState(Activity activity, Bundle bundle);

  @Override void onActivityDestroyed(Activity activity);
}

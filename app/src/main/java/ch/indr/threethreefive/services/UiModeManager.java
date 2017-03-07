/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.services;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import ch.indr.threethreefive.libs.PreferencesType;
import ch.indr.threethreefive.ui.activities.ButtonGuideActivity;
import ch.indr.threethreefive.ui.activities.ListGuideActivity;

import static android.content.Context.UI_MODE_SERVICE;

public class UiModeManager implements UiModeManagerType {

  private final Context context;
  private final PreferencesType preferences;

  public UiModeManager(final @NonNull Context context, final @NonNull PreferencesType preferences) {
    this.context = context;
    this.preferences = preferences;
  }

  public int getCurrentUiMode() {
    return preferences.uiMode().get();
  }

  @Override public void launchButtonsUi(Context context) {
    setCurrentUiMode(UI_MODE_BUTTONS);
    startActivity(context, ButtonGuideActivity.class);
  }

  @Override public void launchListUi(Context context) {
    setCurrentUiMode(UI_MODE_LIST);
    startActivity(context, ListGuideActivity.class);
  }

  private android.app.UiModeManager getUiModeManager() {
    return (android.app.UiModeManager) context.getSystemService(UI_MODE_SERVICE);
  }

  private void setCurrentUiMode(int uiMode) {
    preferences.uiMode().set(uiMode);
    switch (uiMode) {
      case UiModeManagerType.UI_MODE_BUTTONS:
        preferences.uiModeButtonsLaunchCounter().increment();
        break;
      case UiModeManagerType.UI_MODE_LIST:
        preferences.uiModeListLaunchCounter().increment();
        break;
    }
  }

  private void startActivity(Context packageContext, Class<?> cls) {
    Intent intent = new Intent(packageContext, cls);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    context.startActivity(intent);
  }
}

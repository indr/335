/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.services;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

public class AccessibilityServices {
  private AccessibilityManager accessibilityManager;

  public static AccessibilityServices newInstance(final @NonNull Context context) {
    AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
    return new AccessibilityServices(accessibilityManager);
  }

  @VisibleForTesting AccessibilityServices(final @NonNull AccessibilityManager accessibilityManager) {
    this.accessibilityManager = accessibilityManager;
  }

  public boolean isEnabled() {
    return accessibilityManager.isEnabled();
  }

  public boolean isTouchExplorationEnabled() {
    return accessibilityManager.isTouchExplorationEnabled();
  }

  public boolean isSpokenFeedbackEnabled() {
    final List<AccessibilityServiceInfo> enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN);
    return enabledServices != null && !enabledServices.isEmpty();
  }
}

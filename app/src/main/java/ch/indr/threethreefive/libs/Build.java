/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;

import ch.indr.threethreefive.BuildConfig;

public final class Build {

  private final PackageInfo packageInfo;

  public Build(final @NonNull PackageInfo packageInfo) {
    this.packageInfo = packageInfo;
  }

  public @NonNull String applicationId() {
    return packageInfo.packageName;
  }

  public boolean isDebug() {
    return BuildConfig.DEBUG;
  }

  public boolean isRelease() {
    return !BuildConfig.DEBUG;
  }
}

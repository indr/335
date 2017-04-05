/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.utils;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

public class UriUtils {
  private UriUtils() {
  }

  public static String getString(final @Nullable Uri uri) {
    return uri == null || isEmpty(uri) ? null : uri.toString();
  }

  /**
   * Removes the last path segment or root "/".
   */
  public static Uri getParentUri(Uri childUri) {
    Uri.Builder builder = childUri.buildUpon().path("");

    List<String> pathSegments = childUri.getPathSegments();
    for (int i = 0; i < pathSegments.size() - 1; i++) {
      builder.appendPath(pathSegments.get(i));
    }

    Uri parentUri = builder.build();
    if (parentUri.getPath().isEmpty()) {
      parentUri = parentUri.buildUpon().path("/").build();
    }
    return parentUri;
  }

  /**
   * Returns true if provided uri is null or equal to "".
   */
  public static boolean isEmpty(final @Nullable Uri uri) {
    return !isNotEmpty(uri);
  }

  /**
   * Returns true if provided uri is null or equal to "".
   */
  public static Boolean isNotEmpty(final @Nullable Uri uri) {
    return uri != null && !uri.toString().isEmpty();
  }

  public static boolean isFile(final @NonNull Uri uri) {
    String scheme = uri.getScheme();
    return scheme == null || "file".equals(scheme.toLowerCase());
  }

  public static boolean isStream(final @NonNull Uri uri) {
    return !isFile(uri);
  }
}

/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.db.music;

import android.support.annotation.Nullable;

public class MusicStoreUtils {

  @Nullable public static String sanitize(final @Nullable String value) {
    if (value == null) return null;
    if (value.equals("<unknown>")) return "Unknown";
    if (value.contains("_") && !value.contains(" ")) {
      return value.replaceAll("_", " ");
    }

    return value;
  }
}

/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.utils;

import android.support.annotation.Nullable;
import android.support.v4.media.MediaDescriptionCompat;

public final class MediaDescriptionUtils {

  private MediaDescriptionUtils() {
  }

  public static @Nullable String fullTitle(@Nullable MediaDescriptionCompat description) {
    if (description == null) return null;

    CharSequence title = description.getTitle();
    CharSequence subtitle = description.getSubtitle();
    if (title != null && subtitle != null && subtitle.length() > 0) {
      return title + " - " + subtitle;
    } else {
      return title == null ? null : title.toString();
    }
  }
}

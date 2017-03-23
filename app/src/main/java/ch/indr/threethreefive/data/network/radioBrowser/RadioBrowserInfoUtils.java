/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser;

import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import timber.log.Timber;

public class RadioBrowserInfoUtils {


  private RadioBrowserInfoUtils() {
  }

  public static Date convertDateTimeString(@Nullable String value) {
    if (value == null) return null;

    final DateFormat format = new SimpleDateFormat("y-L-d H:m:s", Locale.US);
    format.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

    try {
      return format.parse(value);
    } catch (ParseException e) {
      Timber.w(e, "Error parsing date time %s", value);
      return null;
    }
  }
}


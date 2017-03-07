/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.utils;

public final class StringUtils {
  private StringUtils() {
  }

  public static boolean isEmpty(String string) {
    return string == null || string.isEmpty();
  }

  public static boolean isEmpty(CharSequence string) {
    return string == null || string.length() == 0;
  }

  public static boolean isNotEmpty(String string) {
    return !isEmpty(string);
  }

  public static boolean isNotEmpty(CharSequence string) {
    return !isEmpty(string);
  }
}

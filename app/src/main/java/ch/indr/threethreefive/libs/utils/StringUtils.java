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

  public static boolean isEmpty(final String string) {
    return string == null || string.isEmpty();
  }

  public static boolean isEmpty(final CharSequence string) {
    return string == null || string.length() == 0;
  }

  public static boolean isNotEmpty(final String string) {
    return !isEmpty(string);
  }

  public static boolean isNotEmpty(final CharSequence string) {
    return !isEmpty(string);
  }

  public static String getString(final CharSequence value) {
    return value == null ? null : value.toString();
  }

  public static String join(final Object[] array, final String separator) {
    return org.apache.commons.lang3.StringUtils.join(array, separator);
  }

  public static String join(final Iterable<?> iterable, final String separator) {
    return org.apache.commons.lang3.StringUtils.join(iterable, separator);
  }
}

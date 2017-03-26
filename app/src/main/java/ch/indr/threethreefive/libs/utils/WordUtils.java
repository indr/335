/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.utils;

public final class WordUtils {

  public static String capitalize(final String str) {
    return capitalize(str, null);
  }

  public static String capitalize(final String str, final char... delimiters) {
    final int delimLen = delimiters == null ? -1 : delimiters.length;
    if (StringUtils.isEmpty(str) || delimLen == 0) {
      return str;
    }
    final char[] buffer = str.toCharArray();
    boolean capitalizeNext = true;
    for (int i = 0; i < buffer.length; i++) {
      final char ch = buffer[i];
      if (isDelimiter(ch, delimiters)) {
        capitalizeNext = true;
      } else if (capitalizeNext) {
        buffer[i] = Character.toTitleCase(ch);
        capitalizeNext = false;
      }
    }
    return new String(buffer);
  }

  private static boolean isDelimiter(final char ch, final char[] delimiters) {
    if (delimiters == null) {
      return Character.isWhitespace(ch);
    }
    for (final char delimiter : delimiters) {
      if (ch == delimiter) {
        return true;
      }
    }
    return false;
  }

}

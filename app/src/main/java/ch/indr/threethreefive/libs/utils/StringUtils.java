/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.utils;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class StringUtils {

  private StringUtils() {
  }

  public static final String EMPTY = "";

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

  public static String join(final Iterable<?> iterable, final String separator) {
    if (iterable == null) {
      return null;
    }
    return join(iterable.iterator(), separator);
  }

  public static String join(final Iterator<?> iterator, final String separator) {

    // handle null, zero and one elements before building a buffer
    if (iterator == null) {
      return null;
    }
    if (!iterator.hasNext()) {
      return EMPTY;
    }
    final Object first = iterator.next();
    if (!iterator.hasNext()) {
      @SuppressWarnings("deprecation") // ObjectUtils.toString(Object) has been deprecated in 3.2
      final String result = ObjectUtils.toString(first);
      return result;
    }

    // two or more elements
    final StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
    if (first != null) {
      buf.append(first);
    }

    while (iterator.hasNext()) {
      if (separator != null) {
        buf.append(separator);
      }
      final Object obj = iterator.next();
      if (obj != null) {
        buf.append(obj);
      }
    }
    return buf.toString();
  }

  public static @Nullable String[] split(@Nullable String str, char separatorChar) {
    return splitWorker(str, separatorChar, false);
  }

  private static String[] splitWorker(final String str, final char separatorChar, final boolean preserveAllTokens) {
    // Performance tuned for 2.0 (JDK1.4)

    if (str == null) {
      return null;
    }
    final int len = str.length();
    if (len == 0) {
      return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    final List<String> list = new ArrayList<String>();
    int i = 0, start = 0;
    boolean match = false;
    boolean lastMatch = false;
    while (i < len) {
      if (str.charAt(i) == separatorChar) {
        if (match || preserveAllTokens) {
          list.add(str.substring(start, i));
          match = false;
          lastMatch = true;
        }
        start = ++i;
        continue;
      }
      lastMatch = false;
      match = true;
      i++;
    }
    if (match || preserveAllTokens && lastMatch) {
      list.add(str.substring(start, i));
    }
    return list.toArray(new String[list.size()]);
  }
}

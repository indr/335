/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.utils;

import android.util.Pair;

public class PairUtils {

  public static <F, S> boolean IsNull(final Pair<F, S> pair) {
    return pair == null || (pair.first == null || pair.second == null);
  }

  public static <F, S> boolean isNotNull(final Pair<F, S> pair) {
    return pair != null && pair.first != null && pair.second != null;
  }
}

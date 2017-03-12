/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.utils;

import java.util.ArrayList;
import java.util.List;

public final class CollectionUtils {

  public static <T> List<T> slice(List<T> list, int fromIndex, int toIndex) {
    list = list.subList(fromIndex, Math.min(toIndex, list.size()));
    return new ArrayList<T>(list);
  }
}

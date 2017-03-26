/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser.transformers;

import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

public class Transformers {

  public static <T> RequestListener<T[]> ArrayToList(RequestListener<List<T>> listener) {
    return new ArrayToListTransformer<T>(listener);
  }
}

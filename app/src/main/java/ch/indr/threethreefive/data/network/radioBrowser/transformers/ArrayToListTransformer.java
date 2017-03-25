/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser.transformers;

import com.octo.android.robospice.request.listener.RequestListener;

import java.util.Arrays;
import java.util.List;

import ch.indr.threethreefive.libs.net.ResponseTransformer;

public class ArrayToListTransformer<T> extends ResponseTransformer<T[], List<T>> {

  public ArrayToListTransformer(RequestListener<List<T>> listener) {
    super(listener);
  }

  @Override public void onRequestSuccess(T[] ts) {
    listener.onRequestSuccess(Arrays.asList(ts));
  }
}

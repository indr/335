/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser.transformers;

import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.indr.threethreefive.data.network.radioBrowser.model.Station;
import ch.indr.threethreefive.data.network.radioBrowser.model.Tag;
import ch.indr.threethreefive.libs.net.ResponseTransformer;

public class StationsToTagsTransformer extends ResponseTransformer<Station[], List<Tag>> {

  private final RequestListener<List<Tag>> listener;

  public StationsToTagsTransformer(RequestListener<List<Tag>> listener) {
    super(listener);
    this.listener = listener;
  }

  @Override public void onRequestSuccess(Station[] stations) {
    final Map<String, Integer> map = new HashMap<String, Integer>();
    for (Station station : stations) {
      final String[] tags = station.getTags();
      for (String tag : tags) {
        if (map.containsKey(tag)) {
          map.put(tag, map.get(tag) + 1);
        } else {
          map.put(tag, 1);
        }
      }
    }

    List<Tag> result = new ArrayList<>(map.size());
    for (Map.Entry<String, Integer> entry : map.entrySet()) {
      result.add(new Tag(entry.getKey(), entry.getValue()));
    }
    listener.onRequestSuccess(result);
  }
}

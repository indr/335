/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser.transformers;

import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

import ch.indr.threethreefive.data.network.radioBrowser.model.Genre;
import ch.indr.threethreefive.data.network.radioBrowser.model.GenresBuilder;
import ch.indr.threethreefive.data.network.radioBrowser.model.Station;
import ch.indr.threethreefive.data.network.radioBrowser.model.Tag;
import ch.indr.threethreefive.libs.net.ResponseTransformer;

public class StationsToGenreTransformer extends ResponseTransformer<Station[], List<Genre>> {

  public StationsToGenreTransformer(RequestListener<List<Genre>> listener) {
    super(listener);
  }

  @Override public void onRequestSuccess(Station[] stations) {
    final GenresBuilder genresBuilder = new GenresBuilder();

    for (Station station : stations) {
      for (String tagName : station.getTagNames()) {
        genresBuilder.add(Tag.fromName(tagName, 1));
      }
    }

    listener.onRequestSuccess(genresBuilder.getGenres());
  }
}

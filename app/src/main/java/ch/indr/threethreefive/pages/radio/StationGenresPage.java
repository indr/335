/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages.radio;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.octo.android.robospice.request.listener.RequestListener;

import java.util.Collection;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.data.network.radioBrowser.model.Genre;
import ch.indr.threethreefive.data.network.radioBrowser.model.Station;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.libs.pages.SpiceBasePage;

public class StationGenresPage extends SpiceBasePage implements RequestListener<Station> {

  private String stationId;

  public StationGenresPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);

    this.stationId = bundle.getString("id");

    setTitle(getString(R.string.genres));
  }

  @Override public void onStart() {
    super.onStart();

    apiClient.getStation(stationId, false, this);
  }

  @Override public void onRequestSuccess(Station station) {
    if (station == null) {
      handle(getString(R.string.station_not_found_error, this.stationId));
      return;
    }

    final Collection<Genre> genres = station.getGenres();
    final PageItemsBuilder builder = pageItemsBuilder();
    addPageItems(builder, genres);
    setPageItems(builder);
  }

  private void addPageItems(PageItemsBuilder builder, Collection<Genre> genres) {
    if (genres.size() == 0) {
      builder.addText(getString(R.string.no_genres_found));
      return;
    }

    for (Genre genre : genres) {
      builder.addLink(PageUris.radioGenre(genre.getId()), genre.getName());
    }
  }
}

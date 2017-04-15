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

import java.util.List;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.data.network.radioBrowser.model.Genre;
import ch.indr.threethreefive.data.network.radioBrowser.model.GenresBuilder;
import ch.indr.threethreefive.data.network.radioBrowser.model.Station;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageItemsExpander;
import ch.indr.threethreefive.libs.PageUris;

public class GenrePage extends StationListBasePage {

  private Genre genre;

  private PageItemsExpander<Station> expander = new PageItemsExpander<>();

  public GenrePage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);
    setFavorable(true);

    final String genreId = getUriParam("id");
    this.genre = GenresBuilder.getGenre(genreId);

    setDescription(this.genre.getName(), getString(R.string.radio_genre));
  }

  @Override public void onStart() {
    super.onStart();

    apiClient.getStationsByGenre(genre, this);
  }

  @Override protected void addPageItems(PageItemsBuilder builder, List<Station> stations) {
    builder.addToggleFavorite(getCurrentPageLink());

    if (stations.size() == 0) {
      builder.addText(getString(R.string.no_stations_found));
      return;
    }

    for (Station station : stations) {
      builder.addLink(PageUris.radioStation(station.getId()),
          station.getName(),
          station.makeSubtitle("LG"),
          station.makeContentDescription("LG"),
          station.getLogoUri(),
          R.drawable.ic_radio_grey600_36dp);
    }
  }
}

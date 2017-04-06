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

import java.util.Collections;
import java.util.List;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.data.network.radioBrowser.model.Genre;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.libs.utils.CollectionUtils;

public class CountryGenresPage extends GenreListBasePage {

  private String countryId;

  public CountryGenresPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);

    this.countryId = getUriParam("countryId");
    setTitle(countryId);
  }

  @Override public void onStart() {
    super.onStart();

    apiClient.getGenresByCountry(countryId, this);
  }

  @Override protected void addPageItems(PageItemsBuilder builder, List<Genre> genres) {
    if (genres.size() == 0) {
      builder.addText(getString(R.string.no_genres_found));
      return;
    }

    for (Genre genre : genres) {
      final int stationCount = genre.getStationCount();
      final String subtitle = getResources().getQuantityString(R.plurals.radio_stations, stationCount, stationCount);
      builder.addLink(PageUris.radioCountryGenre(countryId, genre.getId()),
          genre.getName(),
          subtitle,
          genre.getName() + ", " + subtitle);
    }
  }

  @Override protected void setPageItems(@NonNull PageItemsBuilder builder) {
    builder.addLink(PageUris.radioCountryStations(countryId), getString(R.string.show_all_stations));
    super.setPageItems(builder);
  }

  @Override protected void populateLists(List<Genre> genres) {
    Collections.sort(genres, new Genre.StationCountComparator());

    List<Genre> topGenres = CollectionUtils.slice(genres, 0, Math.min(genres.size(), MAX_NUMBER_OF_TOP_GENRES));
    List<Genre> moreGenres = CollectionUtils.slice(genres, 0, Math.min(genres.size(), MAX_NUMBER_OF_MORE_GENRES));

    Collections.sort(genres, new Genre.NameComparator());
    Collections.sort(topGenres, new Genre.NameComparator());
    Collections.sort(moreGenres, new Genre.NameComparator());

    expander.add(topGenres, getString(R.string.show_top_genres));
    expander.add(moreGenres, getString(R.string.show_more_genres));
    expander.add(genres, getString(R.string.show_all_genres));
  }
}

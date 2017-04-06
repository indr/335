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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.data.network.radioBrowser.model.Genre;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.libs.utils.CollectionUtils;
import timber.log.Timber;

public class GenresPage extends GenreListBasePage {

  private static final int MIN_STATION_COUNT_FOR_ALL_GENRES = 9;

  private List<String> excludedFromTopGenres;
  private List<String> excludedFromMoreGenres;

  public GenresPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);

    this.excludedFromTopGenres = Arrays.asList(context.getResources().getStringArray(R.array.excluded_from_top_genres));
    this.excludedFromMoreGenres = Arrays.asList(context.getResources().getStringArray(R.array.excluded_from_more_genres));

    setTitle(getString(R.string.genres));
  }

  @Override public void onStart() {
    super.onStart();

    apiClient.getGenres(this);
  }

  @Override protected void addPageItems(PageItemsBuilder builder, List<Genre> genres) {
    if (genres.size() == 0) {
      builder.addText(getString(R.string.no_genres_found));
      return;
    }

    for (Genre genre : genres) {
      final int stationCount = genre.getStationCount();
      final String subtitle = getResources().getQuantityString(R.plurals.radio_stations, stationCount, stationCount);
      builder.addLink(PageUris.radioGenre(genre.getId()),
          genre.getName(),
          subtitle,
          genre.getName() + ", " + subtitle);
    }
  }

  @Override protected void populateLists(@NonNull List<Genre> response) {
    Timber.d("populateLists genres %d, %s", response.size(), this.toString());

    Timber.d("Filtering min station count and building all genres list, %s", this.toString());
    List<Genre> topGenres = new ArrayList<>();
    List<Genre> moreGenres = new ArrayList<>();
    List<Genre> allGenres = new ArrayList<>();

    for (Genre genre : response) {
      final int stationCount = genre.getStationCount();
      if (stationCount < MIN_STATION_COUNT_FOR_ALL_GENRES) {
        continue;
      }
      allGenres.add(genre);

      // Performance approximation: Instead of sorting all genres by station count, we put
      // candidates for top and more genres lists
      if (stationCount > 100) {
        topGenres.add(genre);
      }
      if (stationCount > 40) {
        moreGenres.add(genre);
      }
    }

    int numberOfExcludes = excludedFromMoreGenres.size() + excludedFromTopGenres.size();

    Timber.d("Building top genres list, %s", this.toString());
    Collections.sort(topGenres, new Genre.StationCountComparator());
    topGenres = topGenres.subList(0, Math.min(topGenres.size(), MAX_NUMBER_OF_TOP_GENRES + numberOfExcludes));
    topGenres = CollectionUtils.reject(topGenres, this::isExcludedFromTopGenres);
    topGenres = topGenres.subList(0, Math.min(topGenres.size(), MAX_NUMBER_OF_TOP_GENRES));

    Timber.d("Building more genres list, %s", this.toString());
    Collections.sort(moreGenres, new Genre.StationCountComparator());
    moreGenres = moreGenres.subList(0, Math.min(moreGenres.size(), MAX_NUMBER_OF_MORE_GENRES + numberOfExcludes));
    moreGenres = CollectionUtils.reject(moreGenres, this::isExcludedFromMoreGenres);
    moreGenres = moreGenres.subList(0, Math.min(moreGenres.size(), MAX_NUMBER_OF_MORE_GENRES));

    Timber.d("Sorting lists by name");
    Collections.sort(topGenres, new Genre.NameComparator());
    Collections.sort(moreGenres, new Genre.NameComparator());
    Collections.sort(allGenres, new Genre.NameComparator());

    Timber.d("top %d, more %d, all %d, %s", topGenres.size(), moreGenres.size(), allGenres.size(), this.toString());

    expander.add(topGenres, getString(R.string.show_top_genres));
    expander.add(moreGenres, getString(R.string.show_more_genres));
    expander.add(allGenres, getString(R.string.show_all_genres));
  }

  private boolean isExcludedFromTopGenres(Genre genre) {
    return excludedFromTopGenres.contains(genre.getId())
        || isExcludedFromMoreGenres(genre);
  }

  private boolean isExcludedFromMoreGenres(Genre genre) {
    return excludedFromMoreGenres.contains(genre.getId());
  }
}

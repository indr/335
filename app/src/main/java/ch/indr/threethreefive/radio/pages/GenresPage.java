/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.radio.pages;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageItemsExpander;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.libs.utils.CollectionUtils;
import ch.indr.threethreefive.navigation.SpiceBasePage;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.TagsRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Tag;
import timber.log.Timber;

public class GenresPage extends SpiceBasePage implements RequestListener<Tag[]> {

  private static final int MAX_NUMBER_OF_TOP_GENRES = 15;
  private static final int MAX_NUMBER_OF_MORE_GENRES = 50;
  private static final int MIN_STATION_COUNT_FOR_ALL_GENRES = 9;

  private List<String> excludedFromTopGenres;
  private List<String> excludedFromMoreGenres;

  private PageItemsExpander<Tag> expander = new PageItemsExpander<>();

  public GenresPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);

    setTitle("Genres");

    this.excludedFromTopGenres = Arrays.asList(context.getResources().getStringArray(R.array.excluded_from_top_genres));
    this.excludedFromMoreGenres = Arrays.asList(context.getResources().getStringArray(R.array.excluded_from_more_genres));
  }

  @Override public void onStart() {
    super.onStart();

    executeRequest(new TagsRequest("hidebroken=true&order=stationcount"), this);
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    handle(spiceException);
  }

  @Override public void onRequestSuccess(Tag[] response) {
    if (response == null) {
      handle(getString(R.string.no_genres_found_error));
      return;
    }

    populateLists(response);
    showNextItems(null);
  }

  private void showNextItems(Environment environment) {
    final PageItemsBuilder builder = pageItemsBuilder();
    expander.buildNext(builder, this::addGenreLinks, this::showNextItems);

    resetFirstVisibleItem();
    setPageItems(builder);
  }

  private void addGenreLinks(PageItemsBuilder builder, List<Tag> genres) {
    if (genres.size() == 0) {
      builder.addText(getString(R.string.no_genres_found));
      return;
    }

    for (Tag each : genres) {
      final String subtitle = String.format(Locale.US, "%d radio stations", each.getStationCount());
      builder.addLink(PageUris.radioGenre(each.getValue()),
          each.getName(), subtitle, each.getName() + ", " + subtitle);
    }
  }

  private void populateLists(@NonNull Tag[] response) {
    Timber.d("populateLists tags %d, %s", response.length, this.toString());

    Timber.d("Filtering min station count and building all genres list, %s", this.toString());
    List<Tag> topGenres = new ArrayList<>();
    List<Tag> moreGenres = new ArrayList<>();
    List<Tag> allGenres = new ArrayList<>();

    for (Tag tag : response) {
      final int stationCount = tag.getStationCount();
      if (stationCount < MIN_STATION_COUNT_FOR_ALL_GENRES) {
        continue;
      }
      allGenres.add(tag);

      // Performance approximation: Instead of sorting all genres by station count, we put
      // candidates for top and more genres lists
      if (stationCount > 100) {
        topGenres.add(tag);
      }
      if (stationCount > 40) {
        moreGenres.add(tag);
      }
    }

    int numberOfExcludes = excludedFromMoreGenres.size() + excludedFromTopGenres.size();

    Timber.d("Building top genres list, %s", this.toString());
    Collections.sort(topGenres, new Tag.StationCountComparator());
    topGenres = topGenres.subList(0, Math.min(topGenres.size(), MAX_NUMBER_OF_TOP_GENRES + numberOfExcludes));
    topGenres = CollectionUtils.reject(topGenres, this::isExcludedFromTopGenres);
    topGenres = topGenres.subList(0, Math.min(topGenres.size(), MAX_NUMBER_OF_TOP_GENRES));

    Timber.d("Building more genres list, %s", this.toString());
    Collections.sort(moreGenres, new Tag.StationCountComparator());
    moreGenres = moreGenres.subList(0, Math.min(moreGenres.size(), MAX_NUMBER_OF_MORE_GENRES + numberOfExcludes));
    moreGenres = CollectionUtils.reject(moreGenres, this::isExcludedFromMoreGenres);
    moreGenres = moreGenres.subList(0, Math.min(moreGenres.size(), MAX_NUMBER_OF_MORE_GENRES));

    Timber.d("Sorting lists by name");
    Collections.sort(topGenres, new Tag.NameComparator());
    Collections.sort(moreGenres, new Tag.NameComparator());
    Collections.sort(allGenres, new Tag.NameComparator());

    Timber.d("top %d, more %d, all %d, %s", topGenres.size(), moreGenres.size(), allGenres.size(), this.toString());

    expander.add(topGenres, getString(R.string.show_top_genres));
    expander.add(moreGenres, getString(R.string.show_more_genres));
    expander.add(allGenres, getString(R.string.show_all_genres));
  }

  private boolean isExcludedFromTopGenres(Tag tag) {
    return excludedFromTopGenres.contains(tag.getValue())
        || isExcludedFromMoreGenres(tag);
  }

  private boolean isExcludedFromMoreGenres(Tag tag) {
    return excludedFromMoreGenres.contains(tag.getValue());
  }
}

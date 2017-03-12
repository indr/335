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
import ch.indr.threethreefive.navigation.SpiceBasePage;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.TagsRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Tag;
import timber.log.Timber;

public class GenresPage extends SpiceBasePage implements RequestListener<Tag[]> {

  private static final int MIN_STATION_COUNT_FOR_TOP_GENRES = 90;
  private static final int MIN_STATION_COUNT_FOR_MORE_GENRES = 50;
  private static final int MIN_STATION_COUNT_FOR_ALL_GENRES = 3;
  private static final int MAX_NUMBER_OF_TOP_GENRES = 15;

  private List<String> excludedFromTopGenres;
  private List<String> excludedFromMoreGenres;

  private List<Tag> allGenres;
  private List<Tag> moreGenres;
  private List<Tag> topGenres;

  public GenresPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

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
    populateLists(response);
    showTopGenres();
  }

  private void showTopGenres() {
    final PageItemsBuilder builder = pageItemsBuilder();
    buildPageItems(builder, topGenres);
    builder.addItem("Show more Genres", this::showMoreGenres);
    setPageItems(builder);
  }

  private void showMoreGenres(Environment environment) {
    final PageItemsBuilder builder = pageItemsBuilder();
    buildPageItems(builder, moreGenres);
    builder.addItem("Show all Genres", this::showAllGenres);
    setPageItems(builder);
  }

  private void showAllGenres(Environment environment) {
    final PageItemsBuilder builder = pageItemsBuilder();
    buildPageItems(builder, allGenres);
    setPageItems(builder);
  }

  private void buildPageItems(PageItemsBuilder builder, List<Tag> genres) {
    for (Tag each : genres) {
      builder.addLink("/radio/genres/" + each.getValue(),
          each.getName(),
          String.format(Locale.US, "%d radio stations", each.getStationCount()),
          each.getName());
    }
  }

  private void populateLists(@NonNull Tag[] response) {
    Timber.d("populateLists tags %d, %s", response.length, this.toString());
    allGenres = new ArrayList<>();
    moreGenres = new ArrayList<>();
    topGenres = new ArrayList<>();
    int excluded = 0;

    for (Tag tag : response) {
      if (isTopGenre(tag)) {
        topGenres.add(tag);
      }
      if (isMoreGenre(tag)) {
        moreGenres.add(tag);
      }
      if (tag.getStationCount() >= MIN_STATION_COUNT_FOR_ALL_GENRES) {
        allGenres.add(tag);
      } else {
        excluded++;
      }
    }

    // Sort top genres by station count to get the top slice of it
    Collections.sort(topGenres, new Tag.StationCountComparator());
    if (topGenres.size() > 0) {
      topGenres = topGenres.subList(0, Math.min(MAX_NUMBER_OF_TOP_GENRES, topGenres.size()));
    }

    Timber.d("Genres: top %d, more %d, all %d, excluded %d",
        topGenres.size(), moreGenres.size(), allGenres.size(), excluded);

    // Sort all lists by name
    final Tag.NameComparator c = new Tag.NameComparator();
    Collections.sort(topGenres, c);
    Collections.sort(moreGenres, c);
    Collections.sort(allGenres, c);
  }

  private boolean isTopGenre(Tag tag) {
    return tag.getStationCount() >= MIN_STATION_COUNT_FOR_TOP_GENRES && !isExcludedFromTopGenres(tag);
  }

  private boolean isMoreGenre(Tag tag) {
    return tag.getStationCount() >= MIN_STATION_COUNT_FOR_MORE_GENRES && !isExcludedFromMoreGenres(tag);
  }

  private boolean isExcludedFromTopGenres(Tag tag) {
    return excludedFromTopGenres.contains(tag.getValue())
        || isExcludedFromMoreGenres(tag);
  }

  private boolean isExcludedFromMoreGenres(Tag tag) {
    return excludedFromMoreGenres.contains(tag.getValue());
  }
}

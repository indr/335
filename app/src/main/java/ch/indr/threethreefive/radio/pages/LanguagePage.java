/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.utils.CollectionUtils;
import ch.indr.threethreefive.libs.utils.StringUtils;
import ch.indr.threethreefive.navigation.SpiceBasePage;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.StationsRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Station;
import timber.log.Timber;

public class LanguagePage extends SpiceBasePage implements RequestListener<Station[]> {

  private String language;
  private List<Station> allStations;
  private List<Station> moreStations;
  private List<Station> topStations;

  public LanguagePage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);

    language = bundle.getString("id");
    if (StringUtils.isEmpty(language)) {
      setTitle(getString(R.string.language));
      throw new RuntimeException("Bundle does not contain an id or id is null or empty");
    }
    setTitle(language);
  }

  @Override public void onStart() {
    super.onStart();

    executeRequest(StationsRequest.byLanguage(language), this);
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    this.handle(spiceException);
  }

  @Override public void onRequestSuccess(Station[] response) {
    if (response == null) {
      handle(R.string.no_stations_found_error);
      return;
    }

    populateLists(response);
    showTopStations();
  }

  private void showTopStations() {
    final PageItemsBuilder builder = pageItemsBuilder();
    builder.addToggleFavorite(getCurrentPageLink());
    addStationLinks(builder, topStations);

    if (moreStations.size() > topStations.size()) {
      if (allStations.size() > moreStations.size()) {
        builder.addItem(getString(R.string.show_more_stations), this::showMoreStations);
      } else {
        builder.addItem(getString(R.string.show_all_stations), this::showAllStations);
      }
    }

    setPageItems(builder);
  }

  private void showMoreStations(Environment environment) {
    resetFirstVisibleItem();
    final PageItemsBuilder builder = pageItemsBuilder();
    builder.addToggleFavorite(getCurrentPageLink());
    addStationLinks(builder, moreStations);

    if (allStations.size() > moreStations.size()) {
      builder.addItem(getString(R.string.show_all_stations), this::showAllStations);
    }

    setPageItems(builder);
  }

  private void showAllStations(Environment environment) {
    resetFirstVisibleItem();
    final PageItemsBuilder builder = pageItemsBuilder();
    builder.addToggleFavorite(getCurrentPageLink());
    addStationLinks(builder, allStations);
    setPageItems(builder);
  }

  private void addStationLinks(PageItemsBuilder builder, List<Station> stations) {
    if (stations.size() == 0) {
      builder.addText(getString(R.string.no_stations_found));
      return;
    }

    for (Station station : stations) {
      builder.addLink("/radio/stations/" + station.getId(),
          station.getName(),
          station.makeSubtitle("CT"),
          station.makeDescription("CT"));
    }
  }

  private void populateLists(@NonNull Station[] response) {
    Timber.d("populateLists stations %d, %s", response.length, this.toString());

    this.allStations = Arrays.asList(response);

    Collections.sort(allStations, Station.getBestStationsComparator());
    this.topStations = CollectionUtils.slice(allStations, 0, 15);
    this.moreStations = CollectionUtils.slice(allStations, 0, 50);

    Collections.sort(topStations, new Station.NameComparator());
    Collections.sort(moreStations, new Station.NameComparator());
    Collections.sort(allStations, new Station.NameComparator());
  }
}

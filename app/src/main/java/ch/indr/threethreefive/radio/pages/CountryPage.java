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

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.libs.utils.CollectionUtils;
import ch.indr.threethreefive.navigation.SpiceBasePage;
import ch.indr.threethreefive.radio.radioBrowserInfo.StationUtils;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.StationsRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Station;
import timber.log.Timber;

public class CountryPage extends SpiceBasePage implements RequestListener<Station[]> {

  private String country;
  private List<Station> allStations;
  private List<Station> moreStations;
  private List<Station> topStations;

  public CountryPage(Environment environment) {
    super(environment);

    setTitle("Country");
  }

  @Override public void onCreate(@NonNull Context context, Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);

    this.country = bundle.getString("id");
    setTitle(country);
  }

  @Override public void onStart() {
    super.onStart();

    executeRequest(StationsRequest.byCountry(country), this);
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    handle(spiceException);
  }

  @Override public void onRequestSuccess(Station[] response) {
    populateLists(response);
    showTopStations();
  }

  private void showTopStations() {
    if (topStations.size() == moreStations.size()) {
      showMoreStations(null);
      return;
    }

    final PageItemsBuilder builder = pageItemsBuilder();
    builder.addToggleFavorite(getCurrentPageLink());
    addStationLinks(builder, topStations);
    builder.addItem("Show more Stations", this::showMoreStations);
    setPageItems(builder);
  }

  private void showMoreStations(Environment environment) {
    if (moreStations.size() == allStations.size()) {
      showAllStations(null);
      return;
    }

    resetFirstVisibleItem();
    final PageItemsBuilder builder = pageItemsBuilder();
    builder.addToggleFavorite(getCurrentPageLink());
    addStationLinks(builder, moreStations);
    builder.addItem("Show all Stations", this::showAllStations);
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
    for (Station station : stations) {
      builder.addLink(PageUris.makeStationUri(station.getId()),
          station.getName(),
          StationUtils.makeSubtitle(station, "LT"),
          StationUtils.makeDescription(station, "LT")
      );
    }
  }

  private void populateLists(Station[] response) {
    Timber.d("populateLists stations %d, %s", response.length, this.toString());

    this.allStations = Arrays.asList(response);
    Collections.sort(allStations, new Station.SummedVoteComparator());
    this.topStations = CollectionUtils.slice(allStations, 0, 14);

    Collections.sort(allStations, new Station.NameComparator());
    Collections.sort(topStations, new Station.NameComparator());

    this.moreStations = CollectionUtils.filter(allStations, (station) -> station.getSummedVotes() > 0);
  }
}

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
import ch.indr.threethreefive.libs.PageItemsExpander;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.libs.utils.CollectionUtils;
import ch.indr.threethreefive.navigation.SpiceBasePage;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.StationsRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Station;
import timber.log.Timber;

public class CountryStationsPage extends SpiceBasePage implements RequestListener<Station[]> {

  private String country;

  private PageItemsExpander<Station> expander = new PageItemsExpander<>();

  public CountryStationsPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);

    this.country = getUriParam("countryId");
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
    if (response == null) {
      handle(R.string.no_stations_found_error);
      return;
    }

    populateLists(response);
    showNextItems(null);
  }

  private void showNextItems(Environment environment) {
    final PageItemsBuilder builder = pageItemsBuilder();
    builder.addToggleFavorite(getCurrentPageLink());
    expander.buildNext(builder, this::addStationLinks, this::showNextItems);

    resetFirstVisibleItem();
    setPageItems(builder);
  }

  private void addStationLinks(PageItemsBuilder builder, List<Station> stations) {
    if (stations.size() == 0) {
      builder.addText(getString(R.string.no_stations_found));
      return;
    }

    for (Station station : stations) {
      builder.addLink(PageUris.radioStation(station.getId()),
          station.getName(),
          station.makeSubtitle("LT"),
          station.makeDescription("LT")
      );
    }
  }

  private void populateLists(@NonNull Station[] response) {
    Timber.d("populateLists stations %d, %s", response.length, this.toString());

    List<Station> allStations = Arrays.asList(response);

    Collections.sort(allStations, Station.getBestStationsComparator());
    List<Station> topStations = CollectionUtils.slice(allStations, 0, 15);
    List<Station> moreStations = CollectionUtils.slice(allStations, 0, 50);

    Collections.sort(topStations, new Station.NameComparator());
    Collections.sort(moreStations, new Station.NameComparator());
    Collections.sort(allStations, new Station.NameComparator());

    expander.add(topStations, getString(R.string.show_top_stations));
    expander.add(moreStations, getString(R.string.show_more_stations));
    expander.add(allStations, getString(R.string.show_all_stations));
  }
}

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
import java.util.List;
import java.util.Locale;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.navigation.SpiceBasePage;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.StationsRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Station;
import timber.log.Timber;

public class GenrePage extends SpiceBasePage implements RequestListener<Station[]> {

  private static final int MIN_SUMMED_VOTES_FOR_TOP_STATIONS = 10;

  private String tag;
  private ArrayList<Station> allStations;
  private ArrayList<Station> topStations;

  public GenrePage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);

    this.tag = bundle.getString("id");
    setTitle(tag);
  }

  @Override public void onStart() {
    super.onStart();

    executeRequest(StationsRequest.byTag(tag), this);
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    handle(spiceException);
  }

  @Override public void onRequestSuccess(Station[] response) {
    populateLists(response);
    if (topStations.size() >= 10) {
      showTopStations();
    } else {
      showAllStations(null);
    }
  }

  private void showTopStations() {
    final PageItemsBuilder builder = pageItemsBuilder();
    builder.addToggleFavorite(getCurrentPageLink());
    addStationLink(builder, topStations);
    builder.addItem("Show all Stations", this::showAllStations);
    setPageItems(builder);
  }

  private void showAllStations(Environment environment) {
    final PageItemsBuilder builder = pageItemsBuilder();
    builder.addToggleFavorite(getCurrentPageLink());
    addStationLink(builder, allStations);
    setPageItems(builder);
  }

  private void addStationLink(PageItemsBuilder builder, List<Station> stations) {
    for (Station station : stations) {
      builder.addLink("/radio/stations/" + station.getId(),
          station.getName(),
          String.format(Locale.US, "%s, %s",
              station.getCountry(), station.getLanguage()));
    }
  }

  private void populateLists(Station[] response) {
    Timber.d("populateLists stations %d, %s", response.length, this.toString());
    allStations = new ArrayList<>();
    topStations = new ArrayList<>();

    for (Station station : response) {
      if (station.getSummedVotes() >= MIN_SUMMED_VOTES_FOR_TOP_STATIONS) {
        topStations.add(station);
      }
      allStations.add(station);
    }

    Timber.d("Stations: top %d, all %d", topStations.size(), allStations.size());
  }
}

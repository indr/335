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

import java.util.Collections;
import java.util.List;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.data.network.radioBrowser.model.Station;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageItemsExpander;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.libs.pages.SpiceBasePage;
import ch.indr.threethreefive.libs.utils.CollectionUtils;
import timber.log.Timber;

public class LanguagePage extends SpiceBasePage implements RequestListener<List<Station>> {

  private String languageId;

  private PageItemsExpander<Station> expander = new PageItemsExpander<>();

  public LanguagePage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);

    this.languageId = getUriParam("id");

    setTitle(languageId);
  }

  @Override public void onStart() {
    super.onStart();

    apiClient.getStationsByLanguage(languageId, this);
  }

  @Override public void onRequestSuccess(List<Station> response) {
    if (response == null) {
      handle(getString(R.string.no_stations_found_error));
      return;
    }

    populateLists(response);
    showNextItems();
  }

  private void showNextItems() {
    final PageItemsBuilder builder = pageItemsBuilder();
    builder.addToggleFavorite(getCurrentPageLink());
    expander.buildNext(builder, this::addPageItems, this::showNextItems);

    resetFirstVisibleItem();
    setPageItems(builder.build());
  }

  private void addPageItems(PageItemsBuilder builder, List<Station> stations) {
    if (stations.size() == 0) {
      builder.addText(getString(R.string.no_stations_found));
      return;
    }

    for (Station station : stations) {
      builder.addLink(PageUris.radioStation(station.getId()),
          station.getName(),
          station.makeSubtitle("CG"),
          station.makeDescription("CG"),
          station.getLogoUri(),
          R.drawable.ic_radio_grey600_36dp);
    }
  }

  private void populateLists(@NonNull List<Station> allStations) {
    Timber.d("populateLists stations %d, %s", allStations.size(), this.toString());

    Collections.sort(allStations, Station.getDefaultStationListComparator());
    List<Station> topStations = CollectionUtils.slice(allStations, 0, 15);
    List<Station> moreStations = CollectionUtils.slice(allStations, 0, 50);

    expander.add(topStations, getString(R.string.show_top_stations));
    expander.add(moreStations, getString(R.string.show_more_stations));
    expander.add(allStations, getString(R.string.show_all_stations));
  }
}

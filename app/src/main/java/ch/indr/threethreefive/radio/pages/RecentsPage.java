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

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageUris;
import ch.indr.threethreefive.navigation.SpiceBasePage;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.StationsRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Station;

public class RecentsPage extends SpiceBasePage implements RequestListener<Station[]> {

  public RecentsPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);

    setTitle(getString(R.string.new_stations));
  }

  @Override public void onStart() {
    super.onStart();

    executeRequest(StationsRequest.recent(50), this);
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    handle(spiceException);
  }

  @Override public void onRequestSuccess(Station[] stations) {
    if (stations == null) {
      handle(getString(R.string.no_stations_found_error));
      return;
    }

    final PageItemsBuilder builder = pageItemsBuilder();
    builder.addToggleFavorite(getCurrentPageLink());
    addStationLinks(builder, stations);
    setPageItems(builder);
  }

  private void addStationLinks(PageItemsBuilder builder, Station[] stations) {
    if (stations.length == 0) {
      builder.addText(getString(R.string.no_stations_found));
      return;
    }

    for (Station station : stations) {
      builder.addLink(PageUris.radioStation(station.getId()),
          station.getName(),
          station.makeSubtitle("CLT"),
          station.makeDescription("CLT"));
    }
  }
}

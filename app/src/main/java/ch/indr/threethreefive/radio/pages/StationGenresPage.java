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

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.navigation.SpiceBasePage;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.StationRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Station;

public class StationGenresPage extends SpiceBasePage implements RequestListener<Station[]> {

  private String stationId;

  public StationGenresPage(Environment environment) {
    super(environment);

    setTitle("Genres");
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);

    this.stationId = bundle.getString("id");
  }

  @Override public void onStart() {
    super.onStart();

    executeRequest(new StationRequest(stationId), this);
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    handle(spiceException);
  }

  @Override public void onRequestSuccess(Station[] stations) {
    if (stations.length != 1) {
      handle(String.format("Could not find radion station %s.", this.stationId));
    }
    Station station = stations[0];

    final PageItemsBuilder builder = pageItemsBuilder();

    for (String tag : station.getTags()) {
      builder.addLink("/radio/genres/" + tag, tag);
    }

    setPageItems(builder);
  }
}

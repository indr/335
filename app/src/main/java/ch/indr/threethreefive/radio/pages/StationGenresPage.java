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
import ch.indr.threethreefive.data.network.radioBrowser.StationRequest;
import ch.indr.threethreefive.data.network.radioBrowser.model.Station;

public class StationGenresPage extends SpiceBasePage implements RequestListener<Station[]> {

  private String stationId;

  public StationGenresPage(Environment environment) {
    super(environment);
  }

  @Override public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    super.onCreate(context, uri, bundle);
    component().inject(this);

    setTitle(getString(R.string.genres));
    this.stationId = bundle.getString("id");
  }

  @Override public void onStart() {
    super.onStart();

    apiClient.getStation(stationId, this);
  }

  @Override public void onRequestFailure(SpiceException spiceException) {
    handle(spiceException);
  }

  @Override public void onRequestSuccess(Station[] stations) {
    if (stations == null || stations.length != 1) {
      handle(getString(R.string.station_not_found_error, this.stationId));
      return;
    }

    final String[] tags = stations[0].getTags();
    final PageItemsBuilder builder = pageItemsBuilder();
    addGenreLinks(builder, tags);
    setPageItems(builder);
  }

  private void addGenreLinks(PageItemsBuilder builder, String[] tags) {
    if (tags.length == 0) {
      builder.addText(getString(R.string.no_genres_found));
      return;
    }

    for (String tag : tags) {
      builder.addLink(PageUris.radioGenre(tag), tag);
    }
  }
}

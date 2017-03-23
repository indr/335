/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser;

import android.support.annotation.NonNull;

import com.google.api.client.http.HttpRequest;

import java.io.IOException;

import ch.indr.threethreefive.data.network.radioBrowser.model.Station;

public class StationRequest extends RadioBrowserInfoRequest<Station[]> {

  private final String stationId;

  public StationRequest(String stationId) {
    super(Station[].class);

    this.stationId = stationId;
  }

  @NonNull @Override protected HttpRequest buildHttpRequest() throws IOException {
    return getHttpRequestFactory().buildGetRequest(makeUrlV1("/stations/byid/" + stationId));
  }
}


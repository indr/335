/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.radio.radioBrowserInfo.api;

import com.google.api.client.http.GenericUrl;

import ch.indr.threethreefive.radio.radioBrowserInfo.RadioBrowserInfoRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Station;

public class StationRequest extends RadioBrowserInfoRequest<Station[]> {

  private final String stationId;

  public StationRequest(String stationId) {
    super(Station[].class);

    this.stationId = stationId;
  }

  @Override protected GenericUrl getUrl() {
    return makeUrl("/stations/byid/" + stationId);
  }
}


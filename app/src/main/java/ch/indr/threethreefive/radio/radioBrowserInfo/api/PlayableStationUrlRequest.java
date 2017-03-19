/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.radio.radioBrowserInfo.api;

import android.support.annotation.NonNull;

import com.google.api.client.http.GenericUrl;

import ch.indr.threethreefive.radio.radioBrowserInfo.RadioBrowserInfoRequest;

public class PlayableStationUrlRequest extends RadioBrowserInfoRequest<PlayableStationUrlRequest> {

  private final String stationId;

  public PlayableStationUrlRequest(@NonNull String stationId) {
    super(PlayableStationUrlRequest.class);

    this.stationId = stationId;
  }

  @Override protected GenericUrl getUrl() {
    return makeUrlV2("/url/" + stationId);
  }
}

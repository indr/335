/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser;

import android.support.annotation.NonNull;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.UrlEncodedContent;
import com.octo.android.robospice.persistence.DurationInMillis;

import java.io.IOException;

import ch.indr.threethreefive.data.network.radioBrowser.model.PlayableStationUrl;

public class PlayableStationUrlRequest extends RadioBrowserInfoRequest<PlayableStationUrl> {

  private final String stationId;

  public PlayableStationUrlRequest(@NonNull String stationId) {
    super(PlayableStationUrl.class);

    this.stationId = stationId;
  }

  @Override public long getCacheExpiryDuration() {
    return DurationInMillis.ALWAYS_EXPIRED;
  }

  @NonNull @Override protected HttpRequest buildHttpRequest() throws IOException {
    return getHttpRequestFactory().buildPostRequest(makeUrlV2("/url/" + stationId), new UrlEncodedContent(new Object()));
  }
}

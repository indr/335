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
import java.util.Map;

import ch.indr.threethreefive.data.network.radioBrowser.model.Station;

public class StationsSearchRequest extends RadioBrowserInfoRequest<Station[]> {

  private final Map<String, String> params;

  public StationsSearchRequest(Map<String, String> params) {
    super(Station[].class);

    this.params = params;
  }

  @Override public long getCacheExpiryDuration() {
    return DurationInMillis.ONE_DAY;
  }

  @NonNull @Override protected HttpRequest buildHttpRequest() throws IOException {
    params.put("hidebroken", "true");
    params.put("order", "name");
    params.put("reverse", "false");

    return getHttpRequestFactory().buildPostRequest(
        makeUrlV1("/stations/search"), new UrlEncodedContent(params));
  }
}

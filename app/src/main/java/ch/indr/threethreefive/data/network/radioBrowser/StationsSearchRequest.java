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
import java.util.HashMap;
import java.util.Map;

import ch.indr.threethreefive.data.network.radioBrowser.model.Station;

public class StationsSearchRequest extends RadioBrowserInfoRequest<Station[]> {

  private final Map<String, String> params = new HashMap<>();

  private String order = "name";
  private boolean reverse = false;

  public StationsSearchRequest() {
    super(Station[].class);
  }

  @Override public long getCacheExpiryDuration() {
    return DurationInMillis.ONE_DAY;
  }

  public StationsSearchRequest where(final @NonNull String name, final @NonNull String value) {
    this.params.put(name, value);
    return this;
  }

  public StationsSearchRequest order(final @NonNull String name, final boolean reverse) {
    this.order = name;
    this.reverse = reverse;
    return this;
  }

  public StationsSearchRequest offset(int number) {
    params.put("offset", String.valueOf(number));
    return this;
  }

  public StationsSearchRequest limit(int number) {
    params.put("limit", String.valueOf(number));
    return this;
  }

  @NonNull @Override protected HttpRequest buildHttpRequest() throws IOException {
    params.put("order", this.order);
    params.put("reverse", this.reverse ? "true" : "false");

    return getHttpRequestFactory().buildPostRequest(
        makeUrlV1("/stations/search"), new UrlEncodedContent(params));
  }

  public StationsSearchRequest expiresIn(long durationInMillis) {
    setCacheExpiryDuration(durationInMillis);
    return this;
  }
}

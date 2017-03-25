/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser;

import android.support.annotation.NonNull;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.octo.android.robospice.persistence.DurationInMillis;

import java.io.IOException;
import java.util.Map;

import ch.indr.threethreefive.data.network.radioBrowser.model.Station;

public class StationsRequest extends RadioBrowserInfoRequest<Station[]> {

  private static final String BASE_URL = "http://www.radio-browser.info/webservice/json/";

  private GenericUrl url;

  private StationsRequest(GenericUrl url) {
    super(Station[].class);
    this.url = url;
  }

  public static StationsRequest byCountry(@NonNull String country) {
    String url = BASE_URL + "stations/bycountryexact/" + country;
    return new StationsRequest(new GenericUrl(url));
  }

  public static StationsRequest byLanguage(@NonNull String language) {
    String url = BASE_URL + "stations/bylanguageexact/" + language;
    return new StationsRequest(new GenericUrl(url));
  }

  public static StationsRequest byTag(@NonNull String tag) {
    String url = BASE_URL + "stations/bytagexact/" + tag;
    return new StationsRequest(new GenericUrl(url));
  }

  public static StationsRequest recent(int rowCount) {
    String url = BASE_URL + "stations/lastchange/" + rowCount;
    StationsRequest request = new StationsRequest(new GenericUrl(url));
    request.setCacheExpiryDuration(DurationInMillis.ALWAYS_EXPIRED);
    return request;
  }

  @NonNull @Override protected HttpRequest buildHttpRequest() throws IOException {
    return getHttpRequestFactory().buildGetRequest(this.url);
  }
}

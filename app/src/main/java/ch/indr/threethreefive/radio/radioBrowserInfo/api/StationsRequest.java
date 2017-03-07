/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.radio.radioBrowserInfo.api;

import com.google.api.client.http.GenericUrl;
import com.octo.android.robospice.persistence.DurationInMillis;

import ch.indr.threethreefive.radio.radioBrowserInfo.RadioBrowserInfoRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Station;

public class StationsRequest extends RadioBrowserInfoRequest<Station[]> {
  private static final String BASE_URL = "http://www.radio-browser.info/webservice/json/";

  private GenericUrl url;

  private StationsRequest(GenericUrl url) {
    super(Station[].class);
    this.url = url;
  }

  @Override protected GenericUrl getUrl() {
    return url;
  }

  public static StationsRequest byCountry(String country) {
    String url = BASE_URL + "stations/bycountryexact/" + country;
    return new StationsRequest(new GenericUrl(url));
  }

  public static StationsRequest byLanguage(String language) {
    String url = BASE_URL + "stations/bylanguageexact/" + language;
    return new StationsRequest(new GenericUrl(url));
  }

  public static StationsRequest byTag(String tag) {
    String url = BASE_URL + "stations/bytagexact/" + tag;
    return new StationsRequest(new GenericUrl(url));
  }

  public static StationsRequest recent(int rowCount) {
    String url = BASE_URL + "stations/lastchange/" + rowCount;
    StationsRequest request = new StationsRequest(new GenericUrl(url));
    request.setCacheExpiryDuration(DurationInMillis.ALWAYS_EXPIRED);
    return request;
  }
}

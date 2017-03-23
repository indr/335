/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser;

import com.google.api.client.http.GenericUrl;
import com.octo.android.robospice.persistence.DurationInMillis;

import ch.indr.threethreefive.data.network.radioBrowser.model.Country;

public class CountriesRequest extends RadioBrowserInfoRequest<Country[]> {

  public CountriesRequest() {
    super(Country[].class);
  }

  @Override public long getCacheExpiryDuration() {
    return DurationInMillis.ONE_DAY;
  }

  @Override protected GenericUrl getUrl() {
    return makeUrlV1("/countries?hidebroken=true&order=value");
  }
}

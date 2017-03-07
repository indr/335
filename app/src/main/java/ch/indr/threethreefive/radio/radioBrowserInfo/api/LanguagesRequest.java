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
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Language;

public class LanguagesRequest extends RadioBrowserInfoRequest<Language[]> {

  public LanguagesRequest() {
    super(Language[].class);
  }

  @Override public long getCacheExpiryDuration() {
    return DurationInMillis.ONE_DAY;
  }

  @Override protected GenericUrl getUrl() {
    return makeUrl("/languages?hidebroken=true&order=value");
  }
}

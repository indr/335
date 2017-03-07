/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.radio.radioBrowserInfo;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.jackson.JacksonFactory;
import com.octo.android.robospice.persistence.DurationInMillis;

import ch.indr.threethreefive.libs.net.NetworkRequest;
import timber.log.Timber;

public abstract class RadioBrowserInfoRequest<TResult> extends NetworkRequest<TResult> {

  private long cacheExpiryDurationInMillis = DurationInMillis.ONE_HOUR;

  public RadioBrowserInfoRequest(Class<TResult> clazz) {
    super(clazz);
  }

  protected abstract GenericUrl getUrl();

  protected static GenericUrl makeUrl(String path) {
    return new GenericUrl("http://www.radio-browser.info/webservice/json" + path);
  }

  @Override public String getCacheKey() {
    return makeCacheKey(getUrl());
  }

  @Override public long getCacheExpiryDuration() {
    return this.cacheExpiryDurationInMillis;
  }

  public void setCacheExpiryDuration(long durationInMillis) {
    this.cacheExpiryDurationInMillis = durationInMillis;
  }

  @Override public TResult loadDataFromNetwork() throws Exception {
    GenericUrl url = getUrl();
    Timber.d("Requesting %s, %s", url.toString(), this.toString());

    HttpRequest request = getHttpRequestFactory().buildGetRequest(url);
    request.setParser(new JacksonFactory().createJsonObjectParser());

    HttpResponse response = request.execute();
    Timber.d("Response status code %d, %s", response.getStatusCode(), this.toString());

    return response.parseAs(getResultType());
  }
}

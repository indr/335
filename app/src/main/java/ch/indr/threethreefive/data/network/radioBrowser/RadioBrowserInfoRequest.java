/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.jackson.JacksonFactory;

import java.io.IOException;

import ch.indr.threethreefive.libs.net.HttpClientSpiceRequest;
import timber.log.Timber;

public abstract class RadioBrowserInfoRequest<TResult> extends HttpClientSpiceRequest<TResult> {

  private HttpRequest request;

  public RadioBrowserInfoRequest(Class<TResult> clazz) {
    super(clazz);
  }

  protected static GenericUrl makeUrlV1(@NonNull String path) {
    return new GenericUrl("http://www.radio-browser.info/webservice/json" + path);
  }

  protected static GenericUrl makeUrlV2(@NonNull String path) {
    return new GenericUrl("http://www.radio-browser.info/webservice/v2/json" + path);
  }

  @Nullable @Override public String getCacheKey() {
    return request == null ? null : makeCacheKey(request.getUrl() + request.getContent().toString());
  }

  @Override public TResult loadDataFromNetwork() throws Exception {
    Timber.d("loadDataFromNetwork %s", this.toString());
    this.request = buildHttpRequest();
    request.setParser(new JacksonFactory().createJsonObjectParser());

    Timber.d("Requesting %s, %s", request.getUrl().toString(), this.toString());
    HttpResponse response = request.execute();

    Timber.d("Response status code %d, %s", response.getStatusCode(), this.toString());
    return response.parseAs(getResultType());
  }

  @NonNull abstract protected HttpRequest buildHttpRequest() throws IOException;
}

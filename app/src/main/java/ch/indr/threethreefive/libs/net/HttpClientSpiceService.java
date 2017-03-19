/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.net;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.octo.android.robospice.JacksonGoogleHttpClientSpiceService;

import java.io.IOException;

import ch.indr.threethreefive.BuildConfig;

public class HttpClientSpiceService extends JacksonGoogleHttpClientSpiceService {

  public static final String USER_AGENT = "ThreeThreeFive/" + BuildConfig.VERSION_NAME + " (Android/" + BuildConfig.APPLICATION_ID + "/" + BuildConfig.VERSION_CODE + ")";

  @Override public void onCreate() {
    super.onCreate();

    httpRequestFactory = createRequestFactory();
  }

  public static HttpRequestFactory createRequestFactory() {
    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
    return httpTransport.createRequestFactory(new HttpRequestInitializer() {
      @Override public void initialize(HttpRequest request) throws IOException {
        request.getHeaders().setUserAgent(USER_AGENT);
      }
    });
  }
}

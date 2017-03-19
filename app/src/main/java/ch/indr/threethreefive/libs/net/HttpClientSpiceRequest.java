/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.net;

import com.google.api.client.http.GenericUrl;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import timber.log.Timber;

public abstract class HttpClientSpiceRequest<TResult> extends GoogleHttpClientSpiceRequest<TResult> {

  public HttpClientSpiceRequest(Class<TResult> clazz) {
    super(clazz);
  }

  public abstract String getCacheKey();

  public long getCacheExpiryDuration() {
    return DurationInMillis.ONE_MINUTE;
  }

  protected String makeCacheKey(GenericUrl url) {
    String cacheKey;
    try {
      byte[] bytes = url.toString().getBytes("UTF-8");
      final MessageDigest md5 = MessageDigest.getInstance("MD5");
      byte[] digest = md5.digest(bytes);
      String hexStr = "";
      for (byte each : digest) {
        hexStr += Integer.toString((each & 0xff) + 0x100, 16).substring(1);
      }
      cacheKey = hexStr;
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return null;
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return null;
    }

    Timber.d("Cache key %s, %s", cacheKey, this.toString());
    return cacheKey;
  }
}

/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.net;

import android.support.annotation.Nullable;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import timber.log.Timber;

public abstract class HttpClientSpiceRequest<TResult> extends GoogleHttpClientSpiceRequest<TResult> {

  private long cacheExpiryDurationInMillis = DurationInMillis.ONE_HOUR;

  public HttpClientSpiceRequest(Class<TResult> clazz) {
    super(clazz);
  }

  @Nullable public abstract String getCacheKey();

  public long getCacheExpiryDuration() {
    return this.cacheExpiryDurationInMillis;
  }

  public void setCacheExpiryDuration(long durationInMillis) {
    this.cacheExpiryDurationInMillis = durationInMillis;
  }

  @Nullable protected String makeCacheKey(String value) {
    String cacheKey;
    try {
      byte[] bytes = value.getBytes("UTF-8");
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

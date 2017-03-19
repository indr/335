/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.radio.radioBrowserInfo.api;

import android.support.annotation.Nullable;

import com.google.api.client.http.GenericUrl;
import com.octo.android.robospice.persistence.DurationInMillis;

import ch.indr.threethreefive.radio.radioBrowserInfo.RadioBrowserInfoRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Tag;

public class TagsRequest extends RadioBrowserInfoRequest<Tag[]> {

  private final String query;

  public TagsRequest(@Nullable String query) {
    super(Tag[].class);

    this.query = query;
  }

  @Override public long getCacheExpiryDuration() {
    return DurationInMillis.ONE_DAY;
  }

  @Override protected GenericUrl getUrl() {
    return makeUrlV1("/tags" + (query != null ? "?" + query : ""));
  }
}

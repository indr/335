/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.radio.radioBrowserInfo.api.json;

import com.google.api.client.util.Key;

import java.util.Date;

import ch.indr.threethreefive.radio.radioBrowserInfo.RadioBrowserInfoUtils;

public class Station {

  @Key private boolean ok;

  @Key private String message;

  @Key private String id;

  @Key private String name;

  @Key private String url;

  @Key private String homepage;

  @Key private String tags;

  @Key private String country;

  @Key private String language;

  @Key private String votes;

  @Key private String negativevotes;

  @Key private String codec;

  @Key private String bitrate;

  @Key private String lastchangetime;

  public boolean getOk() {
    return ok;
  }

  public String getMessage() {
    return message;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public String getHomepage() {
    return homepage;
  }

  public String[] getTags() {
    return tags == null ? null : tags.split(",");
  }

  public String getCountry() {
    return country;
  }

  public String getLanguage() {
    return language;
  }

  public int getVotes() {
    return votes == null ? 0 : Integer.parseInt(votes);
  }

  public int getNegativeVotes() {
    return negativevotes == null ? 0 : Integer.parseInt(negativevotes);
  }

  public int getSummedVotes() {
    return getVotes() - getNegativeVotes();
  }

  public String getCodec() {
    return codec;
  }

  public String getBitrate() {
    return bitrate;
  }

  public Date getLastChangeTime() {
    return RadioBrowserInfoUtils.convertDateTimeString(lastchangetime);
  }
}

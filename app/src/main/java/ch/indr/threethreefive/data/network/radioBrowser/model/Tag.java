/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser.model;

import android.support.annotation.NonNull;

import com.google.api.client.util.Key;

import java.util.Comparator;

public class Tag {

  @Key private String name;

  @Key private String value;

  @Key private int stationcount;

  public Tag() {
  }

  public Tag(final @NonNull String name, final int stationcount) {
    this.name = name;
    this.value = name;
    this.stationcount = stationcount;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public int getStationCount() {
    return stationcount;
  }

  public static class NameComparator implements Comparator<Tag> {
    @Override public int compare(Tag tag1, Tag tag2) {
      return tag1.getName().compareTo(tag2.getName());
    }
  }

  public static class StationCountComparator extends NameComparator {
    @Override public int compare(Tag tag1, Tag tag2) {
      int result = tag2.getStationCount() - tag1.getStationCount();
      if (result != 0) return result;

      return super.compare(tag1, tag2);
    }
  }
}

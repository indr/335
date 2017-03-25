/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser.model;

import android.support.annotation.NonNull;

import com.google.api.client.util.Key;

import org.apache.commons.lang3.text.WordUtils;

import java.util.Comparator;

public class Tag {

  @Key("value")
  private String _id;
  private String id;

  @Key("name")
  private String _name;
  private String name;

  @Key("stationcount")
  private int stationCount;

  public static Tag fromName(final @NonNull String name) {
    return new Tag(name.toLowerCase(), name, 0);
  }

  public static Tag fromName(final @NonNull String name, final int stationCount) {
    return new Tag(name.toLowerCase(), name, stationCount);
  }

  public Tag() {
  }

  public Tag(final @NonNull String name, final int stationCount) {
    this._id = name;
    this._name = name;
    this.stationCount = stationCount;
  }

  private Tag(final @NonNull String id, final @NonNull String name, final int stationCount) {
    this.id = id;
    this.name = name;
    this.stationCount = stationCount;
  }

  public String getId() {
    if (id == null) id = _id.toLowerCase();
    return id;
  }

  public String getName() {
    // TODO: Get translated tag name
    if (name == null) name = WordUtils.capitalize(_name);
    return name;
  }

  public void setName(final @NonNull String name) {
    this.name = name;
  }

  public int getStationCount() {
    return stationCount;
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

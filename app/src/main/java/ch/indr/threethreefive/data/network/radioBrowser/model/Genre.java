/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ch.indr.threethreefive.libs.utils.WordUtils;

public class Genre {

  private String id;

  private String name;

  private List<Tag> tags = new ArrayList<>();

  private int stationCount;

  public Genre() {
  }

  public Genre(final @NonNull String id, final int stationCount) {
    this.id = id;
    this.name = WordUtils.capitalize(id);
    this.stationCount = stationCount;
    this.tags.add(new Tag(id, stationCount));
  }

  public Genre(final @NonNull String id, List<String> tagIds) {
    this.id = id;
    this.name = WordUtils.capitalize(id);
    this.stationCount = 0;
    for (String tagId : tagIds) {
      this.tags.add(new Tag(tagId, 0));
    }
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getStationCount() {
    return stationCount;
  }

  public List<Tag> getTags() {
    return tags;
  }

  public void add(Tag tag) {
    this.stationCount += tag.getStationCount();
    this.tags.add(tag);
  }

  public static class NameComparator implements Comparator<Genre> {
    @Override public int compare(Genre genre1, Genre genre2) {
      return genre1.getName().compareTo(genre2.getName());
    }
  }

  public static class StationCountComparator extends NameComparator {
    @Override public int compare(Genre genre1, Genre genre2) {
      int result = genre2.getStationCount() - genre1.getStationCount();
      if (result != 0) return result;

      return super.compare(genre1, genre2);
    }
  }
}

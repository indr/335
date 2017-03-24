/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser.model;

import android.support.annotation.NonNull;

import java.util.Comparator;

public class Genre {

  private String name;

  private String value;

  private int stationcount;

  public Genre() {
  }

  public Genre(final @NonNull String name, final int stationcount) {
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

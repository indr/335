/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser.model;

import com.google.api.client.util.Key;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ch.indr.threethreefive.data.network.radioBrowser.RadioBrowserInfoUtils;

public class Station {

  private static DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

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

  @Key private String clickcount;

  @Key private String clicktrend;

  public Station() {
  }

  public Station(int id) {
    this.id = String.valueOf(id);
    this.name = "Station " + id;
  }

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

  public int getPositiveVotes() {
    return votes == null ? 0 : Integer.parseInt(votes);
  }

  public int getNegativeVotes() {
    return negativevotes == null ? 0 : Integer.parseInt(negativevotes);
  }

  public int getSummedVotes() {
    return getPositiveVotes() - getNegativeVotes();
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

  public int getClickCount() {
    return clickcount == null ? 0 : Integer.parseInt(clickcount);
  }

  public int getClickTrend() {
    return clicktrend == null ? 0 : Integer.parseInt(clicktrend);
  }

  public String makeSubtitle(String props) {
    List<String> result = new ArrayList<>();

    for (char prop : props.toCharArray()) {
      String value = null;
      switch (prop) {
        case 'C': // Country
          value = this.getCountry();
          break;
        case 'G': // Genre
        case 'T': // Tag
          value = StringUtils.join(this.getTags(), ", ");
          break;
        case 'L': // Languages
          value = this.getLanguage();
          break;
        case 'U': // Updated
          value = dateFormat.format(this.getLastChangeTime());
          break;
      }

      if (StringUtils.isNotEmpty(value)) {
        result.add(value);
      }
    }

    return StringUtils.join(result, ", ");
  }

  public String makeDescription(String props) {
    return this.getName() + ", " + makeSubtitle(props);
  }

  public static class NameComparator implements Comparator<Station> {
    @Override public int compare(Station station1, Station station2) {
      int result = station1.getName().compareTo(station2.getName());
      if (result != 0) return result;

      return station1.getId().compareTo(station2.getId());
    }
  }

  public static class SummedVoteComparator extends NameComparator {
    @Override public int compare(Station station1, Station station2) {
      int result = station2.getSummedVotes() - station1.getSummedVotes();
      if (result != 0) return result;

      result = station2.getPositiveVotes() - station1.getPositiveVotes();
      if (result != 0) return result;

      return super.compare(station1, station2);
    }
  }

  public static class ClickCountComparator extends SummedVoteComparator {
    @Override public int compare(Station station1, Station station2) {
      int result = station2.getClickCount() - station1.getClickCount();
      if (result != 0) return result;

      result = station2.getClickTrend() - station1.getClickTrend();
      if (result != 0) return result;

      return super.compare(station1, station2);
    }
  }

  public static Comparator<Station> getBestStationsComparator() {
    return new ClickCountComparator();
  }
}

/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser.model;

import android.support.annotation.Nullable;

import com.google.api.client.util.Key;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ch.indr.threethreefive.data.network.radioBrowser.RadioBrowserInfoUtils;
import ch.indr.threethreefive.libs.utils.StringUtils;

public class Station {

  private static DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

  @Key private boolean ok;

  @Key private String message;

  @Key private String id;

  @Key private String name;

  @Key("url")
  private String streamUri;

  @Key("homepage")
  private String homepage;

  @Key("favicon")
  protected String logoUri;

  @Key("tags")
  protected String _tags;

  @Key private String country;

  @Key private String language;

  @Key("votes")
  private int positiveVotes;

  @Key("negativevotes")
  private int negativeVotes;

  @Key private String codec;

  @Key private String bitrate;

  @Key private String lastchangetime;

  @Key("clickcount")
  private int clickCount;

  @Key("clicktrend")
  private int clickTrend;

  protected List<String> tags = null;

  private Collection<Genre> genres = null;

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

  public String getTitle() {
    return name;
  }

  public String getSubtitle() {
    return null;
  }

  public String getStreamUri() {
    return streamUri;
  }

  public String getHomepage() {
    return homepage;
  }

  public String getLogoUri() {
    return logoUri;
  }

  public Collection<Genre> getGenres() {
    if (this.genres == null) this.genres = GenresBuilder.getGenres(getTagNames());
    return this.genres;
  }

  public List<String> getTagNames() {
    if (tags == null) {
      tags = new ArrayList<>();
      String[] strings = StringUtils.isEmpty(_tags) ? new String[0] : _tags.split(" *, *");
      for (String tag : strings) {
        if (StringUtils.isNotEmpty(tag)) tags.add(tag);
      }
    }
    return tags;
  }

  public void setTagNames(final @Nullable String tagNames) {
    this._tags = tagNames;
    this.tags = null;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(final @Nullable String country) {
    this.country = country;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(final @Nullable String language) {
    this.language = language;
  }

  public int getPositiveVotes() {
    return positiveVotes;
  }

  public void setPositiveVotes(final int positiveVotes) {
    this.positiveVotes = positiveVotes;
  }

  public int getNegativeVotes() {
    return negativeVotes;
  }

  public void setNegativeVotes(final int negativeVotes) {
    this.negativeVotes = negativeVotes;
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
    return clickCount;
  }

  public void setClickCount(final int clickCount) {
    this.clickCount = clickCount;
  }

  public int getClickTrend() {
    return clickTrend;
  }

  public void setClickTrend(final int clickTrend) {
    this.clickTrend = clickTrend;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Station station = (Station) o;

    if (!id.equals(station.id)) return false;
    if (!name.equals(station.name)) return false;
    return country != null ? country.equals(station.country) : station.country == null;

  }

  @Override public int hashCode() {
    int result = id.hashCode();
    result = 31 * result + name.hashCode();
    return result;
  }

  public String makeSubtitle(String format) {
    List<String> result = new ArrayList<>();

    for (char prop : format.toCharArray()) {
      String value = null;
      switch (prop) {
        case 'C': // Country
          value = this.getCountry();
          break;
        case 'G': // Genre
          final Collection<Genre> genres = this.getGenres();
          final List<String> genresAsString = new ArrayList<>();
          for (Genre genre : genres) {
            genresAsString.add(genre.getName());
          }
          value = StringUtils.join(genresAsString, ", ");
          break;
        case 'T': // Tag
          value = StringUtils.join(getTagNames(), ", ");
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

    // return getClickTrend() + ", " + getClickCount() + ", " + getSummedVotes();
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

  public static class ClickTrendAndCountComparator extends SummedVoteComparator {
    @Override public int compare(Station station1, Station station2) {
      int result = station2.getClickTrend() - station1.getClickTrend();
      if (result != 0) return result;

      result = station2.getClickCount() - station1.getClickCount();
      if (result != 0) return result;

      return super.compare(station1, station2);
    }
  }

  public static class PopularityComparator extends NameComparator {
    @Override public int compare(Station station1, Station station2) {
      int result = getPopularityValue(station2) - getPopularityValue(station1);
      if (result != 0) return result;

      return super.compare(station1, station2);
    }

    private int getPopularityValue(Station station) {
      return station.getSummedVotes() + station.getClickTrend();
    }
  }

  public static Comparator<Station> getDefaultStationListComparator() {
    return new PopularityComparator();
  }
}

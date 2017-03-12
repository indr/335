/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.radio.radioBrowserInfo;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Station;

public class StationUtils {

  private static DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

  private StationUtils() {
  }

  public static String makeSubtitle(Station station, String props) {
    List<String> result = new ArrayList<>();

    for (char prop : props.toCharArray()) {
      String value = null;
      switch (prop) {
        case 'C': // Country
          value = station.getCountry();
          break;
        case 'G': // Genre
        case 'T': // Tag
          value = StringUtils.join(station.getTags(), ", ");
          break;
        case 'L': // Languages
          value = station.getLanguage();
          break;
        case 'U': // Updated
          value = dateFormat.format(station.getLastChangeTime());
          break;
      }

      if (StringUtils.isNotEmpty(value)) {
        result.add(value);
      }
    }

    return StringUtils.join(result, ", ");
  }

  public static String makeDescription(Station station, String props) {
    return station.getName() + ", " + makeSubtitle(station, props);
  }
}

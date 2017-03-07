/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.radio.radioBrowserInfo.api.json;

import java.util.Comparator;

public class StationCountComparator implements Comparator<Tag> {
  @Override public int compare(Tag tag1, Tag tag2) {
    return tag2.getStationCount() - tag1.getStationCount();
  }
}



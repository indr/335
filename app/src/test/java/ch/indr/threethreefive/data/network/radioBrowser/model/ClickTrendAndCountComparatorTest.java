/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser.model;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import ch.indr.threethreefive.Fake;
import ch.indr.threethreefive.TtfRobolectricTestCase;

public class ClickTrendAndCountComparatorTest extends TtfRobolectricTestCase {

  @Test
  public void compare_returnsDifferenceBetweenClickTrend() throws Exception {
    final Station station1 = new Station();
    station1.setClickTrend(-5);
    final Station station2 = new Station();
    station2.setClickTrend(10);

    final Station.ClickTrendAndCountComparator clickTrendAndCountComparator = new Station.ClickTrendAndCountComparator();

    final int compare = clickTrendAndCountComparator.compare(station1, station2);

    assertEquals(15, compare);
  }

  @Test
  public void CollectionSort_sortsByClickTrendDesc() throws Exception {
    final List<Station> stations = Fake.stations(10);
    Collections.sort(stations, new Station.ClickTrendAndCountComparator());

    int previousClickTrend = Integer.MAX_VALUE;
    for (Station station : stations) {
      final int clickTrend = station.getClickTrend();

      assertTrue(previousClickTrend >= clickTrend);
      previousClickTrend = station.getClickTrend();
    }
  }

  @Test
  public void CollectionSort_sortsByClickTrendDescClickCountDesc() throws Exception {
    final List<Station> stations = Fake.stations(10);
    stations.forEach(s -> s.setClickTrend(1));
    Collections.sort(stations, new Station.ClickTrendAndCountComparator());

    int previousClickCount = Integer.MAX_VALUE;
    for (Station station : stations) {
      final int clickCount = station.getClickCount();

      assertTrue(previousClickCount >= clickCount);
      previousClickCount = station.getClickCount();
    }
  }

  @Test
  public void CollectionSort_sortsByClickTrendDescClickCountDescSummedVoteDesc() throws Exception {
    final List<Station> stations = Fake.stations(10);
    stations.forEach(s -> s.setClickTrend(0));
    stations.forEach(s -> s.setClickCount(0));
    Collections.sort(stations, new Station.ClickTrendAndCountComparator());

    int previousSummedVote = Integer.MAX_VALUE;
    for (Station station : stations) {
      final int summedVotes = station.getSummedVotes();

      assertTrue(previousSummedVote >= summedVotes);
      previousSummedVote = station.getSummedVotes();
    }
  }
}
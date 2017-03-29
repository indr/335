/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.indr.threethreefive.data.network.radioBrowser.model.Station;

public class Fake {

  public static List<Station> stations(int number) {
    Random random = new Random();
    final List<Station> stations = new ArrayList<>();
    for (int i = 0; i < number; i++) {
      final Station station = new Station(i);

      station.setPositiveVotes(random.nextInt(50));
      station.setNegativeVotes(random.nextInt(50));
      station.setClickTrend(random.nextInt(100));
      station.setClickCount(random.nextInt(1000));

      stations.add(station);
    }
    return stations;
  }
}

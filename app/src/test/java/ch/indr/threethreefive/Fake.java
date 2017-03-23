/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive;

import java.util.ArrayList;
import java.util.List;

import ch.indr.threethreefive.data.network.radioBrowser.model.Station;

public class Fake {

  public static Station[] stations(int number) {
    final List<Station> stations = new ArrayList<>();
    for (int i = 0; i < number; i++) {
      stations.add(new Station(i));
    }
    return stations.toArray(new Station[0]);
  }
}

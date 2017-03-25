/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser.model;

import org.junit.Test;

import java.util.List;

import ch.indr.threethreefive.TtfRobolectricTestCase;

public class StationTests extends TtfRobolectricTestCase {

  @Test
  public void getTagNames_withNull_returnsEmptyList() {
    final TestStation station = new TestStation();
    station.setTagNames(null);

    final List<String> tagNames = station.getTagNames();

    assertEquals(true, tagNames.isEmpty());
  }

  @Test
  public void getTagNames_withEmptyString_returnsEmptyList() {
    final TestStation station = new TestStation();
    station.setTagNames("");

    final List<String> tagNames = station.getTagNames();

    assertEquals(true, tagNames.isEmpty());
  }

  @Test
  public void getTagNames_withCommaSeparatedNames_returnsList() {
    final TestStation station = new TestStation();
    station.setTagNames("Jazz, Blues, , Hard rock,");

    final List<String> tagNames = station.getTagNames();

    assertEquals(3, tagNames.size());
    assertEquals(tagNames.get(0), "Jazz");
    assertEquals(tagNames.get(1), "Blues");
    assertEquals(tagNames.get(2), "Hard rock");
  }

  public class TestStation extends Station {

    public void setTagNames(String tagNames) {
      this._tags = tagNames;
      this.tags = null;
    }
  }
}
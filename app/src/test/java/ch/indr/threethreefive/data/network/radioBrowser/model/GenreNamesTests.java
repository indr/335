/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser.model;

import org.junit.Before;
import org.junit.Test;

import ch.indr.threethreefive.TtfRobolectricTestCase;

public class GenreNamesTests extends TtfRobolectricTestCase {

  @Before
  @Override public void setUp() throws Exception {
    super.setUp();

    GenreNames.load(context().getResources());
  }

  @Test
  public void getName_unknownGenre_returnsWordCapitalized() {
    String name = GenreNames.getName("an unknown genre name");

    assertEquals("An Unknown Genre Name", name);
  }

  @Test
  public void getName_knownGenre_returnsTranslation() {
    String name = GenreNames.getName("bbc");

    assertEquals("BBC", name);
  }
}
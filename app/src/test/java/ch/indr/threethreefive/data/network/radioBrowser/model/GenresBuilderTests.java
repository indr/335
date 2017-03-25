/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser.model;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import ch.indr.threethreefive.TtfRobolectricTestCase;

public class GenresBuilderTests extends TtfRobolectricTestCase {

  @Before
  @Override public void setUp() throws Exception {
    super.setUp();

    GenreMaps.load(context().getResources());
  }

  @Test
  public void add_tagWithoutParentGenre_addsTagAsGenre() throws Exception {
    final GenresBuilder genresBuilder = new GenresBuilder();
    final Tag tag = new Tag("NoParent", 123);

    genresBuilder.add(tag);

    final List<Genre> genres = genresBuilder.getGenres();
    assertEquals(1, genres.size());
    assertEquals("noparent", genres.get(0).getId());
    assertEquals("Noparent", genres.get(0).getName());
    assertEquals(123, genres.get(0).getStationCount());
  }

  @Test
  public void add_tagsWithParentGenre_addsTagsToParentGenre() throws Exception {
    final GenresBuilder genresBuilder = new GenresBuilder();
    final Tag tag1 = new Tag("1980s", 11);
    final Tag tag2 = new Tag("80er", 22);

    genresBuilder.add(tag1);
    genresBuilder.add(tag2);

    final List<Genre> genres = genresBuilder.getGenres();
    assertEquals(1, genres.size());
    assertEquals(2, genres.get(0).getTags().size());
    assertEquals(33, genres.get(0).getStationCount());
  }
}
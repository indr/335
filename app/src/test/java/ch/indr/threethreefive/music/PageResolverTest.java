/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.music;

import android.net.Uri;

import org.junit.Test;

import ch.indr.threethreefive.TtfRobolectricTestCase;
import ch.indr.threethreefive.music.pages.IndexPage;
import ch.indr.threethreefive.navigation.PageMeta;

public class PageResolverTest extends TtfRobolectricTestCase {

  @Test
  public void resolving_slash_music_should_return_index_page() {
    // Arrange
    MusicResolver sut = new MusicResolver();

    // Act
    PageMeta pageMeta = sut.resolve(Uri.parse("/music"));

    // Assert
    assertEquals(IndexPage.class, pageMeta.getClazz());
    assertEquals(Uri.parse("//ch.indr.threethreefive/music"), pageMeta.getUri());
    assertTrue(pageMeta.getBundle().isEmpty());
  }
}

/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.pages.music;

import android.os.Bundle;

import org.junit.Test;

import java.util.List;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.TtfRobolectricTestCase;
import ch.indr.threethreefive.data.db.music.model.Album;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.PageUris;

import static org.mockito.Mockito.when;

public class AlbumPageTests extends TtfRobolectricTestCase {

  private static final String ALBUM_ID = "1";

  @Test
  public void onCreate_addsPageItems() {
    final Album album = new Album(ALBUM_ID, "Name", "Artist", "artistId", 3);
    when(musicStore.getAlbumById(ALBUM_ID)).thenReturn(album);

    final AlbumPage page = createPage();

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals("Play all Songs", pageItems.get(0).getTitle());
  }

  @Test
  public void onCreate_whenAlbumDoesNotExist_addsAlbumNotFoundError() {
    when(musicStore.getAlbumById(ALBUM_ID)).thenReturn(null);

    final AlbumPage page = createPage();

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(1, pageItems.size());
    assertEquals(getString(R.string.album_not_found_error, ALBUM_ID), pageItems.get(0).getTitle());
  }

  private AlbumPage createPage() {
    final AlbumPage albumPage = new AlbumPage(environment());
    albumPage.onCreate(context(), PageUris.musicAlbum(ALBUM_ID), new Bundle());
    return albumPage;
  }
}
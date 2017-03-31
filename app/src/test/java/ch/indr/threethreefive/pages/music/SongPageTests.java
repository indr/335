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
import ch.indr.threethreefive.data.db.music.model.Song;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.PageUris;

import static org.mockito.Mockito.when;

public class SongPageTests extends TtfRobolectricTestCase {

  private static final String SONG_ID = "1";

  @Test
  public void onCreate_addsPageItems() {
    final Song song = new Song(SONG_ID, "Name", "Artist", "artistId", "Album", "albumId", "data", 1, null);
    when(musicStore.getSongById(SONG_ID)).thenReturn(song);

    final SongPage page = createPage();

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals("Press to Play", pageItems.get(0).getTitle());
  }

  @Test
  public void onCreate_whenSongDoesNotExist_addsSongNotFoundError() {
    when(musicStore.getSongById(SONG_ID)).thenReturn(null);

    final SongPage page = createPage();

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(1, pageItems.size());
    assertEquals(getString(R.string.song_not_found_error, SONG_ID), pageItems.get(0).getTitle());
  }

  private SongPage createPage() {
    final SongPage songPage = new SongPage(environment());
    songPage.onCreate(context(), PageUris.musicSong(SONG_ID), new Bundle());
    return songPage;
  }
}
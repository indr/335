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
import ch.indr.threethreefive.data.db.music.model.Artist;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.PageUris;

import static org.mockito.Mockito.when;

public class ArtistPageTests extends TtfRobolectricTestCase {

  private static final String ARTIST_ID = "1";

  @Test
  public void onCreate_addsPageItems() {
    final Artist artist = new Artist(ARTIST_ID, "Name", 2, 3);
    when(musicStore.getArtistById(ARTIST_ID)).thenReturn(artist);

    final ArtistPage page = createPage();

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals("Play all Albums", pageItems.get(0).getTitle());
  }

  @Test
  public void onCreate_whenArtistDoesNotExist_addsArtistNotFoundError() {
    when(musicStore.getArtistById(ARTIST_ID)).thenReturn(null);

    final ArtistPage page = createPage();

    final List<PageItem> pageItems = page.getPageItems();
    assertEquals(1, pageItems.size());
    assertEquals(getString(R.string.artist_not_found_error, ARTIST_ID), pageItems.get(0).getTitle());
  }

  private ArtistPage createPage() {
    final ArtistPage artistPage = new ArtistPage(environment());
    artistPage.onCreate(context(), PageUris.musicArtist(ARTIST_ID), new Bundle());
    return artistPage;
  }
}
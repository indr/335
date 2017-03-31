/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.db.music.model;

import static ch.indr.threethreefive.data.db.music.MusicStoreUtils.sanitize;

public class Artist {

  private final String id;
  private final String name;
  private final int numberOfAlbums;
  private final int numberOfTracks;

  public Artist(String id, String name, int numberOfAlbums, int numberOfTracks) {
    this.id = id;
    this.name = sanitize(name);
    this.numberOfAlbums = numberOfAlbums;
    this.numberOfTracks = numberOfTracks;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getNumberOfAlbums() {
    return numberOfAlbums;
  }

  public int getNumberOfTracks() {
    return numberOfTracks;
  }
}

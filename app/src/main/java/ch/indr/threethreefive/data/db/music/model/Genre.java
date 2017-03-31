/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.db.music.model;

import static ch.indr.threethreefive.data.db.music.MusicStoreUtils.sanitize;

public class Genre {

  private final String id;
  private final String name;

  public Genre(long id, String name) {
    this.id = String.valueOf(id);
    this.name = sanitize(name);
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}

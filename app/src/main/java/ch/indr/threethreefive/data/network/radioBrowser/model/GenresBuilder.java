/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenresBuilder {

  private final Map<String, String> tagToGenreMap;
  private Map<String, Genre> genres = new HashMap<>();

  public GenresBuilder() {
    this.tagToGenreMap = GenreMaps.getTagToGenreMap();
  }

  public void add(final @NonNull Tag tag) {
    // Get genre key. If there is no genre associated to tag, then tag is its own genre.
    String genreId = tagToGenreMap.get(tag.getId());
    if (genreId == null) genreId = tag.getId();

    // Get or create genre instance and add tag.
    Genre genre = genres.get(genreId);
    if (genre == null) {
      genre = new Genre(genreId, tag.getStationCount());
      genres.put(genre.getId(), genre);
    } else {
      genre.add(tag);
    }
  }

  @NonNull public List<Genre> getGenres() {
    return new ArrayList<>(genres.values());
  }

  @NonNull public static List<Genre> getGenres(final @NonNull Collection<String> tagNames) {
    final GenresBuilder genresBuilder = new GenresBuilder();
    for (String tagName : tagNames) {
      genresBuilder.add(Tag.fromName(tagName));
    }
    return genresBuilder.getGenres();
  }

  @NonNull public static List<Genre> getGenres(final @NonNull Tag[] tags) {
    final GenresBuilder genresBuilder = new GenresBuilder();
    for (Tag tag : tags) {
      genresBuilder.add(tag);
    }
    return genresBuilder.getGenres();
  }

  @NonNull public static Genre getGenre(final @NonNull String genreId) {
    final Map<String, List<String>> genreToTagsMap = GenreMaps.getGenreToTagsMap();

    List<String> tagIds = genreToTagsMap.get(genreId);
    if (tagIds == null) {
      return new Genre(genreId, 0);
    }

    tagIds.add(genreId);
    return new Genre(genreId, tagIds);
  }
}

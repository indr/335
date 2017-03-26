/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network.radioBrowser.model;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.utils.StringUtils;
import timber.log.Timber;

public class GenreMaps {

  private static Map<String, String> tagToGenreMap = new HashMap<>();
  private static Map<String, List<String>> genreToTagsMap = new HashMap<>();

  public static Map<String, String> getTagToGenreMap() {
    return tagToGenreMap;
  }

  public static Map<String, List<String>> getGenreToTagsMap() {
    return genreToTagsMap;
  }

  public static void load(@NonNull Resources resources) {
    tagToGenreMap.clear();
    genreToTagsMap.clear();

    try {
      InputStreamReader inputStreamReader = new InputStreamReader(resources.openRawResource(R.raw.tag_genre_map));
      try {
        buildTagToGenreMap(inputStreamReader);
        buildGenreToTagsMap();
      } finally {
        inputStreamReader.close();
      }
    } catch (Exception ex) {
      Timber.e(ex, "Error loading genre map.");
    }
  }

  private static void buildTagToGenreMap(InputStreamReader inputStreamReader) throws IOException {
    BufferedReader reader = new BufferedReader(inputStreamReader);
    try {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] strings = StringUtils.split(line, ',');
        if (strings == null || strings.length != 2) continue;
        tagToGenreMap.put(strings[0], strings[1]);
      }
    } finally {
      reader.close();
    }
  }

  private static void buildGenreToTagsMap() {
    for (Map.Entry<String, String> each : tagToGenreMap.entrySet()) {
      List<String> tags = genreToTagsMap.get(each.getValue());
      if (tags == null) tags = new ArrayList<>();
      tags.add(each.getKey());
      genreToTagsMap.put(each.getValue(), tags);
    }
  }
}

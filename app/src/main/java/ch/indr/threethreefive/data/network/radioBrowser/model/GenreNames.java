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
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import ch.indr.threethreefive.R;
import ch.indr.threethreefive.libs.utils.StringUtils;
import ch.indr.threethreefive.libs.utils.WordUtils;
import timber.log.Timber;

public class GenreNames {

  private static Map<String, String> genreNames = new HashMap<>();

  public static void load(@NonNull Resources resources) {
    genreNames.clear();

    try {
      InputStreamReader inputStreamReader = new InputStreamReader(resources.openRawResource(R.raw.genre_names));
      try {
        BufferedReader reader = new BufferedReader(inputStreamReader);
        try {
          String line;
          while ((line = reader.readLine()) != null) {
            String[] strings = StringUtils.split(line, ',');
            if (strings == null || strings.length != 2) continue;
            genreNames.put(strings[0], strings[1]);
          }
        } finally {
          reader.close();
        }
      } finally {
        inputStreamReader.close();
      }
    } catch (Exception ex) {
      Timber.e(ex, "Error loading genre names.");
    }
  }

  public static @NonNull String getName(final @NonNull String nameOrId) {
    String name = genreNames.get(nameOrId);
    return name != null ? name : WordUtils.capitalize(nameOrId);
  }

  public static @NonNull String getNameById(String id) {
    return getName(id);
  }
}

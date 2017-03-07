/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.content.SharedPreferences;

import ch.indr.threethreefive.libs.preferences.FloatPreferenceType;
import ch.indr.threethreefive.libs.preferences.IntPreferenceType;

public interface PreferencesType {

  IntPreferenceType appLaunchCounter();

  IntPreferenceType autoRepeatMode();

  FloatPreferenceType textSize();

  IntPreferenceType uiMode();

  IntPreferenceType uiModeButtonsLaunchCounter();

  IntPreferenceType uiModeListLaunchCounter();

  void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener);

  void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener);
}


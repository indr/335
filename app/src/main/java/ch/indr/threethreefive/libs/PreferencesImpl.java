/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import ch.indr.threethreefive.libs.preferences.FloatPreference;
import ch.indr.threethreefive.libs.preferences.FloatPreferenceType;
import ch.indr.threethreefive.libs.preferences.IntPreference;
import ch.indr.threethreefive.libs.preferences.IntPreferenceType;

public class PreferencesImpl implements Preferences {

  private final SharedPreferences sharedPreferences;

  public PreferencesImpl(final @NonNull SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  @Override public IntPreferenceType appLaunchCounter() {
    return new IntPreference(sharedPreferences, "app_launch_counter");
  }

  @Override public IntPreferenceType autoRepeatMode() {
    return new IntPreference(sharedPreferences, "auto_repeat_mode");
  }

  @Override public IntPreferenceType uiModeButtonsLaunchCounter() {
    return new IntPreference(sharedPreferences, "ui_mode_buttons_launch_counter");
  }

  @Override public IntPreferenceType uiModeListLaunchCounter() {
    return new IntPreference(sharedPreferences, "ui_mode_list_launch_counter");
  }

  @Override public FloatPreferenceType textSize() {
    return new TextSizePreference(sharedPreferences, "text_size");
  }

  @Override public IntPreferenceType uiMode() {
    return new IntPreference(sharedPreferences, "ui_mode");
  }

  @Override public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
    sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
  }

  @Override public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
    sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
  }
}

class TextSizePreference extends FloatPreference {

  private final float MIN_VALUE = 18;

  TextSizePreference(@NonNull SharedPreferences sharedPreferences, @NonNull String key) {
    super(sharedPreferences, key, 60);
  }

  @Override public void set(float value) {
    value = Math.max(value, MIN_VALUE);
    super.set(value);
  }

  @Override public float get() {
    return Math.max(MIN_VALUE, super.get());
  }
}

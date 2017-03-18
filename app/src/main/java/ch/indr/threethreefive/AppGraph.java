/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.navigation.SpiceBasePage;
import ch.indr.threethreefive.pages.HomePage;
import ch.indr.threethreefive.pages.PreferencesPage;
import ch.indr.threethreefive.radio.pages.StationPage;
import ch.indr.threethreefive.ui.activities.ButtonGuideActivity;
import ch.indr.threethreefive.ui.activities.ListGuideActivity;
import ch.indr.threethreefive.ui.activities.NowPlayingProxyActivity;
import ch.indr.threethreefive.ui.activities.StartActivity;
import ch.indr.threethreefive.ui.activities.UiSelectionActivity;
import ch.indr.threethreefive.ui.fragments.ButtonPlaybackFragment;

public interface AppGraph {

  Environment environment();

  void inject(ThreeThreeFiveApp __);

  void inject(ButtonGuideActivity __);

  void inject(ButtonPlaybackFragment __);

  void inject(HomePage __);

  void inject(ListGuideActivity __);

  void inject(NowPlayingProxyActivity __);

  void inject(PreferencesPage preferencesPage);

  void inject(SpiceBasePage __);

  void inject(StartActivity __);

  void inject(StationPage __);

  void inject(UiSelectionActivity __);

}

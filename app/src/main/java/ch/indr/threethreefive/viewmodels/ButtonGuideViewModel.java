/*
 * Copyright (c) 2016 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.viewmodels;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.PageLink;
import ch.indr.threethreefive.libs.preferences.IntPreferenceType;
import ch.indr.threethreefive.libs.utils.ObjectUtils;
import ch.indr.threethreefive.libs.utils.UriUtils;
import ch.indr.threethreefive.navigation.Page;
import ch.indr.threethreefive.services.SpeakerType;
import ch.indr.threethreefive.ui.activities.ButtonGuideActivity;
import ch.indr.threethreefive.viewmodels.inputs.ButtonGuideViewModelInputs;
import ch.indr.threethreefive.viewmodels.outputs.ButtonGuideViewModelOutputs;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static ch.indr.threethreefive.libs.rx.transformers.Transfomers.takeWhen;

public class ButtonGuideViewModel extends PageActivityViewModel<ButtonGuideActivity> implements ButtonGuideViewModelInputs, ButtonGuideViewModelOutputs {

  private final SpeakerType speaker;
  private final IntPreferenceType uiModelaunchCounter;
  private boolean ignoreNextTitle = false;

  // INPUTS
  private final PublishSubject<View> bumpInto = PublishSubject.create();
  private final PublishSubject<View> goHome = PublishSubject.create();

  // OUTPUTS
  private final BehaviorSubject<PageItem> pageItem = BehaviorSubject.create();
  private final BehaviorSubject<String> activityTitle = BehaviorSubject.create();

  public ButtonGuideViewModel(@NonNull Environment environment) {
    super(environment);

    this.speaker = environment.speaker();
    this.uiModelaunchCounter = environment.preferences().uiModeButtonsLaunchCounter();
  }

  @Override protected void onCreate(@NonNull Context context, @Nullable Bundle savedInstanceState) {
    super.onCreate(context, savedInstanceState);

    speaker.start();
    if (uiModelaunchCounter.get() <= 1) {
      speaker.instructions().play();
      ignoreNextTitle = true;
    }

    // Selected page item
    page.switchMap(Page::pageItem)
        .compose(bindToLifecycle())
        .subscribe(pageItem);

    // Bump into focused page item
    pageItem.compose(takeWhen(bumpInto))
        .filter(ObjectUtils::isNotNull)
        .compose(bindToLifecycle())
        .subscribe(this::executePageItem);

    // Go home if not already on home page
    pageUriFromIntent.compose(takeWhen(goHome))
        .filter(UriUtils::isNotEmpty)
        .filter(uri -> !PageLink.HomePage.getUri().equals(uri))
        .map(uri -> PageLink.HomePage)
        .compose(bindToLifecycle())
        .subscribe(showPage);

    // Speak activity title if already on home page
    activityTitle.compose(takeWhen(pageUriFromIntent.compose(takeWhen(goHome))
        .filter(uri -> UriUtils.isEmpty(uri) || PageLink.HomePage.getUri().equals(uri))))
        .compose(bindToLifecycle())
        .subscribe(this::speakTitle);

    // Activity title, when page item is null ("Loading...")
    pageTitle.compose(takeWhen(pageItem.filter(ObjectUtils::isNull)))
        .map(title -> "Loading " + title)
        .compose(bindToLifecycle())
        .map(title -> {
          Timber.d("Activity title changed (page item is null) title %s, %s", title, this.toString());
          speakTitle(title);
          return title;
        })
        .subscribe(activityTitle);

    // Acitivity title, when page items name emits first value
    pageItem.filter(ObjectUtils::isNotNull)
        .switchMap((pageItem1) -> pageItem1.title().first())
        .compose(bindToLifecycle())
        .map(title -> {
          Timber.d("Activity title changed (page item change) title %s, %s", title, this.toString());
          speakTitle(title);
          return title;
        })
        .subscribe(activityTitle);

    // Activity title, when page items name observable changed
    final Observable<String> activityTitleSoft =
        Observable.combineLatest(pageTitle, pageItem
            .filter(ObjectUtils::isNotNull).switchMap((pageItem1) -> pageItem1.title().skip(1)), Pair::create)
            .map(this::makeActivityTitle)
            .distinctUntilChanged();

    activityTitleSoft
        .compose(bindToLifecycle())
        .subscribe(activityTitle);
  }

  private void speakTitle(String title) {
    if (ignoreNextTitle) {
      Timber.d("Ignoring title utterance");
      ignoreNextTitle = false;
      return;
    }
    speaker.sayUrgent(title);
  }

  private String makeActivityTitle(@NonNull Pair<String, String> pageAndItemTitle) {
    return makeActivityTitle(pageAndItemTitle.first, pageAndItemTitle.second);
  }

  private String makeActivityTitle(@Nullable String pageName, @Nullable String itemName) {
    if (itemName != null) {
      return itemName;
    } else {
      return pageName != null ? "Loading " + pageName : null;
    }
  }

  public final ButtonGuideViewModelInputs inputs = this;

  public final ButtonGuideViewModelOutputs outputs = this;

  // INPUTS
  @Override public void enter() {
    bumpInto.onNext(null);
  }

  @Override public void home() {
    goHome.onNext(null);
  }

  @Override public void location() {

    StringBuilder sb = new StringBuilder();
    for (Page page : pageStack) {

      // Skip home page
      if (page.getIsRootPage()) {
        continue;
      }

      String title = page.getTitle();
      sb.append(title).append(", ");
    }

    PageItem pageItem = this.pageItem.getValue();
    if (pageItem != null) {
      sb.append(pageItem.getTitle());
    }

    speaker.sayUrgent(sb.toString());
  }

  @Override public void stepLeft() {
    Page page = this.page.getValue();
    if (page != null) {
      page.selectPreviousPageItem();
    }
  }

  @Override public void stepRight() {
    Page page = this.page.getValue();
    if (page != null) {
      page.selectNextPageItem();
    }
  }

  // OUTPUTS
  @Override public Observable<String> activityTitle() {
    return activityTitle;
  }

  @Override public Observable<Boolean> canGoUp() {
    return canGoUp;
  }

  @Override public Observable<PageItem> pageItem() {
    return pageItem;
  }

  @Override public Observable<PageLink> showPage() {
    return showPage;
  }
}

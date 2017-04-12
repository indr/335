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
import android.view.View;

import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.PageLink;
import ch.indr.threethreefive.libs.pages.Page;
import ch.indr.threethreefive.libs.pages.Transition;
import ch.indr.threethreefive.libs.preferences.IntPreferenceType;
import ch.indr.threethreefive.libs.utils.ObjectUtils;
import ch.indr.threethreefive.libs.utils.UriUtils;
import ch.indr.threethreefive.services.AccessibilityServices;
import ch.indr.threethreefive.services.Speaker;
import ch.indr.threethreefive.ui.activities.ButtonGuideActivity;
import ch.indr.threethreefive.viewmodels.inputs.ButtonGuideViewModelInputs;
import ch.indr.threethreefive.viewmodels.outputs.ButtonGuideViewModelOutputs;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static ch.indr.threethreefive.libs.rx.transformers.Transfomers.takeWhen;

public class ButtonGuideViewModel extends PageActivityViewModel<ButtonGuideActivity> implements ButtonGuideViewModelInputs, ButtonGuideViewModelOutputs {

  private final Speaker speaker;
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
    speakInstructions(context);

    // Selected page item
    page.switchMap(Page::pageItem)
        .compose(bindToLifecycle())
        .subscribe(pageItem);

    // Bump into focused page item
    pageItem.compose(takeWhen(bumpInto))
        .filter(ObjectUtils::isNotNull)
        .compose(bindToLifecycle())
        .subscribe(this::executePageItem);

    // Show home page if not already on home page
    page.map(Page::getPageUri)
        .compose(takeWhen(goHome))
        .filter(uri -> !PageLink.HomePage.getUri().equals(uri))
        .map(uri -> PageLink.HomePage)
        .compose(bindToLifecycle())
        .subscribe(showPage);

    // Select first item if already on home page
    pageItem
        .filter(ObjectUtils::isNotNull)
        .compose(takeWhen(page.map(Page::getPageUri).compose(takeWhen(goHome))
            .filter(uri -> UriUtils.isEmpty(uri) || PageLink.HomePage.getUri().equals(uri))))
        .withLatestFrom(page, (__, p) -> p)
        .filter(ObjectUtils::isNotNull)
        .compose(bindToLifecycle())
        .subscribe(Page::selectFirstPageItem);

    // Activity title
    pageTitle
        .compose(takeWhen(pageItem.filter(ObjectUtils::isNull)))
        .map(t -> "Loading " + t)
        .compose(bindToLifecycle())
        .doOnNext(this::speakTitle)
        .mergeWith(pageItem.filter(ObjectUtils::isNotNull).switchMap(PageItem::title))
        .compose(bindToLifecycle())
        .subscribe(activityTitle);

    // Speak page item description
    pageItem
        .filter(ObjectUtils::isNotNull)
        .switchMap(pageItem -> pageItem.description().first())
        .compose(bindToLifecycle())
        .subscribe(this::speakTitle);
  }

  @Override protected void onBeforeTransition(@NonNull Transition transition) {
    // This is indented to prevent double title speech like "Loa Loading Nepal"
    // when a transition is made because of an empty dataset.
    this.ignoreNextTitle = transition.getReplace();
  }

  private void speakInstructions(final @NonNull Context context) {
    Timber.d("speakInstructions %s", this.toString());

    final AccessibilityServices accessibilityServices = AccessibilityServices.newInstance(context);
    final boolean accessibilitiesEnabled = accessibilityServices.isTouchExplorationEnabled() || accessibilityServices.isSpokenFeedbackEnabled();
    Timber.d("Accessibilities enabled %s, %s", accessibilitiesEnabled, this.toString());

    if (uiModelaunchCounter.get() <= 1) {
      speaker.instructions().playUsage();
      ignoreNextTitle = true;
    } else if (accessibilitiesEnabled) {
      speaker.instructions().playAccessibilityServicesDetected();
      ignoreNextTitle = true;
    }
  }

  private void speakTitle(String title) {
    Timber.d("speakTitle %s, %s", title, this.toString());
    if (ignoreNextTitle) {
      Timber.d("Ignoring title utterance");
      ignoreNextTitle = false;
      return;
    }
    speaker.sayUrgent(title);
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

  @Override public void up() {
    // Up button should not close app, instead act like a press on home
    if (pageStack.size() == 1) {
      home();
    } else {
      super.up();
    }
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

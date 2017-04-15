/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.libs.pages;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Pair;

import java.util.List;

import ch.indr.threethreefive.AppComponent;
import ch.indr.threethreefive.ThreeThreeFiveApp;
import ch.indr.threethreefive.commands.ToggleFavorite;
import ch.indr.threethreefive.data.db.favorites.FavoritesStore;
import ch.indr.threethreefive.libs.Description;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageItem;
import ch.indr.threethreefive.libs.PageItemsBuilder;
import ch.indr.threethreefive.libs.PageLink;
import ch.indr.threethreefive.libs.utils.CollectionUtils;
import ch.indr.threethreefive.libs.utils.StringUtils;
import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static ch.indr.threethreefive.libs.rx.transformers.Transfomers.takeWhen;

public abstract class Page implements PageType {

  private Context context;
  private Bundle bundle;

  private Environment environment;

  // Page lifecycle state
  private State state = State.New;

  private Uri pageUri;

  private final BehaviorSubject<Description> description = BehaviorSubject.create(Description.EMPTY);

  private Uri iconUri;

  private BehaviorSubject<Boolean> isFavorable = BehaviorSubject.create();
  private BehaviorSubject<Boolean> isFavorite = BehaviorSubject.create();

  // Set default value to `null`, which indicates the page is loading items
  private final BehaviorSubject<List<PageItem>> pageItems = BehaviorSubject.create((List<PageItem>) null);

  private final BehaviorSubject<Integer> pageItemIdx = BehaviorSubject.create(0);
  private final BehaviorSubject<PageItem> pageItem = BehaviorSubject.create();

  // Set default parent page link to HomePage
  private final BehaviorSubject<PageLink> parentPageLink = BehaviorSubject.create(PageLink.HomePage);

  private final PublishSubject<Object> selectNextPageItem = PublishSubject.create();

  private final PublishSubject<Object> selectPreviousPageItem = PublishSubject.create();

  private final PublishSubject<PageTransition> transitionTo = PublishSubject.create();

  private Pair<Integer, Integer> firstVisibleItem;

  public Page(Environment environment) {
    this.environment = environment;
  }

  protected PageLink getCurrentPageLink() {
    return new PageLink(getPageUri(), getDescription(), getIconUri(), 0);
  }

  @Override public boolean getIsRootPage() {
    return getParentPageUri() == null;
  }

  @Override @NonNull public Uri getPageUri() {
    return pageUri;
  }

  @Override public Uri getParentPageUri() {
    final PageLink value = parentPageLink.getValue();
    return value == null ? null : value.getUri();
  }

  @Nullable public List<PageItem> getPageItems() {
    return pageItems.getValue();
  }

  @Override public Observable<List<PageItem>> pageItems() {
    return pageItems;
  }

  @Override public Observable<PageItem> pageItem() {
    return pageItem;
  }

  @Override public Observable<PageLink> parentPageLink() {
    return parentPageLink;
  }

  public void selectFirstPageItem() {
    pageItemIdx.onNext(0);
  }

  public void selectNextPageItem() {
    selectNextPageItem.onNext(null);
  }

  public void selectPreviousPageItem() {
    selectPreviousPageItem.onNext(null);
  }

  protected void setParentPageLink(final @Nullable PageLink link) {
    parentPageLink.onNext(link);
  }

  public Observable<Description> description() {
    return description;
  }

  @NonNull Description getDescription() {
    return description.getValue();
  }

  public void setDescription(final @Nullable CharSequence title) {
    setDescription(StringUtils.getString(title, ""));
  }

  public void setDescription(final @NonNull String title) {
    setDescription(new Description(title));
  }

  public void setDescription(final @NonNull String title, final @Nullable String subtitle) {
    setDescription(new Description(title, subtitle));
  }

  public void setDescription(final @NonNull String title, final @Nullable String subtitle,
                             final @Nullable String contentDescription) {
    setDescription(new Description(title, subtitle, contentDescription));
  }

  public void setDescription(final @NonNull Description description) {
    this.description.onNext(description);
  }

  public final @Nullable String getTitle() {
    return getDescription().getTitle();
  }

  public final @Nullable String getSubtitle() {
    return getDescription().getSubtitle();
  }

  protected void setSubtitle(final @Nullable String subtitle) {
    description.onNext(description.getValue().setSubtitle(subtitle));
  }

  public final @Nullable String getContentDescription() {
    return getDescription().getContentDescription();
  }

  protected void setContentDescription(final @Nullable String contentDescription) {
    description.onNext(description.getValue().setContentDescription(contentDescription));
  }

  public final @Nullable Uri getIconUri() {
    return iconUri;
  }

  protected void setIconUri(final @Nullable Uri iconUri) {
    this.iconUri = iconUri;
  }

  protected boolean transitionTo(final @NonNull PageTransition pageTransition) {
    // If the transition intends to replace the current page, there won't be a way
    // for the user the remove the favorite.
    if (pageTransition.getReplace() && environment.favoritesStore().isFavorite(getPageUri())) {
      return false;
    }

    transitionTo.onNext(pageTransition);
    return true;
  }

  public Observable<PageTransition> transitionTo() {
    return transitionTo;
  }

  public void onCreate(@NonNull Context context, @NonNull Uri uri, Bundle bundle) {
    if (state != State.New) {
      throw new IllegalStateException("Page is not in state new");
    }
    setState(State.Created);

    this.context = context;
    this.pageUri = uri;
    this.bundle = bundle;

    // Current/focused page item
    Observable.combineLatest(pageItems, pageItemIdx, (items, idx) -> {
      if (items == null || idx < 0 || idx >= items.size()) return null;
      return items.get(idx);
    }).subscribe(pageItem);

    // Step right, focus next page item
    pageItemIdx
        .compose(takeWhen(selectNextPageItem))
        .map(idx -> idx + 1)
        .withLatestFrom(pageItems, (idx, items) -> {
          if (items == null) return 0;
          return idx > items.size() - 1 ? 0 : idx;
        })
        .subscribe(pageItemIdx);

    // Step left, focus previous page item
    pageItemIdx.compose(takeWhen(selectPreviousPageItem))
        .map(idx -> idx - 1)
        .withLatestFrom(pageItems, (idx, items) -> {
          if (items == null) return 0;
          return idx < 0 ? items.size() - 1 : idx;
        })
        .subscribe(pageItemIdx);
  }

  public void onStart() {
    if (state != State.Created) {
      throw new IllegalStateException("Page is not in state created: " + state);
    }
    setState(State.Started);
  }

  public void onStop() {
    if (state != State.Started && state != State.Paused) {
      throw new IllegalStateException("Page is not in state started, paused: " + state);
    }
    setState(State.Stopped);
  }

  @CallSuper
  public void onResume() {
    if (state != State.Started && state != State.Paused) {
      throw new IllegalStateException("Page is not in state started, paused: " + state);
    }
    setState(State.Resumed);
  }

  @CallSuper
  public void onPause() {
    if (state != State.Resumed) {
      throw new IllegalStateException("Page is not in state resumed: " + state);
    }
    setState(State.Paused);
  }

  public void onDestroy() {
    if (state != State.Stopped) {
      throw new IllegalStateException("Page is not in state stopped: " + state);
    }
    setState(State.Destroyed);
  }

  protected @NonNull ThreeThreeFiveApp application() {
    return (ThreeThreeFiveApp) context.getApplicationContext();
  }

  protected @NonNull AppComponent component() {
    return application().component();
  }

  protected Context context() {
    return context;
  }

  protected Resources resources() {
    return context.getResources();
  }

  protected String getString(@StringRes int id) {
    return resources().getString(id);
  }

  protected String getString(@StringRes int id, Object... formatArgs) {
    return resources().getString(id, formatArgs);
  }

  protected String getUriParam(@NonNull String key) {
    if (!bundle.containsKey(key))
      throw new IllegalArgumentException("Bundle does not contain key " + key);
    return bundle.getString(key);
  }

  protected PageItemsBuilder pageItemsBuilder() {
    return new PageItemsBuilder(this.context(), this.environment);
  }

  protected void setPageItems(final @NonNull PageItemsBuilder builder) {
    setPageItems(builder.build());
  }

  protected void setPageItems(final @NonNull List<PageItem> items) {
    setFavorite(items);

    boolean setPageItemIdx = pageItem.getValue() == null;
    pageItems.onNext(items);

    if (setPageItemIdx) {
      // Set page item index (selected page item) to first non "Add to Favorite" item
      int newItemIdx = 0;
      for (int i = 0; i < items.size(); i++) {
        PageItem pageItem = items.get(i);
        if (!ToggleFavorite.class.isAssignableFrom(pageItem.getClass())) {
          newItemIdx = i;
          break;
        }
      }
      pageItemIdx.onNext(newItemIdx);
    }
  }

  /**
   * Looks for a ToggleFavorite item and sets isFavorite based on its value.
   */
  private void setFavorite(List<PageItem> items) {
    if (items == null) {
      setFavorable(false);
      setFavorite(false);
      return;
    }

    final List<PageItem> filtered = CollectionUtils.filter(items, item -> item.getClass().isAssignableFrom(ToggleFavorite.class));
    if (filtered.size() <= 0) {
      setFavorable(false);
      setFavorite(false);
      return;
    }

    final ToggleFavorite toggleFavorite = (ToggleFavorite) filtered.get(0);
    final boolean favorite = toggleFavorite.isFavorite();
    setFavorable(true);
    setFavorite(favorite);
  }

  protected void handle(final @NonNull Exception ex) {
    Timber.e(ex, ex.getMessage());
    setError(ex.getMessage());
    ex.printStackTrace();
  }

  protected void handle(final @NonNull String message) {
    Timber.e(message);
    setError(message);
  }

  private void setError(final @NonNull String message) {
    final PageItemsBuilder builder = pageItemsBuilder();

    final FavoritesStore favoritesStore = environment.favoritesStore();
    if (favoritesStore.isFavorite(getPageUri())) {
      builder.addRemoveFavorite(getCurrentPageLink());
    }

    builder.addText(message);
    final List<PageItem> items = builder.build();

    setPageItems(items);
  }

  private void setState(State state) {
    Timber.d("New state " + state.name());
    this.state = state;
  }

  public State getState() {
    return state;
  }

  public @Nullable Pair<Integer, Integer> getFirstVisibleItem() {
    return this.firstVisibleItem;
  }

  public void setFirstVisibleItem(final @Nullable Pair<Integer, Integer> firstVisibleItem) {
    this.firstVisibleItem = firstVisibleItem;
  }

  protected void resetFirstVisibleItem() {
    this.firstVisibleItem = null;
  }

  public Observable<Boolean> isFavorable() {
    return isFavorable;
  }

  public void setFavorable(boolean favorable) {
    isFavorable.onNext(favorable);
    if (favorable) {
      setFavorite(environment.favoritesStore().isFavorite(getPageUri()));
    }
  }

  public Observable<Boolean> isFavorite() {
    return isFavorite;
  }

  public void setFavorite(boolean favorite) {
    isFavorite.onNext(favorite);
  }

  public enum State {
    New,
    Created,
    Started,
    Resumed,
    Paused,
    Stopped,
    Destroyed
  }
}

package ch.indr.threethreefive.commands;

import android.support.annotation.NonNull;

import ch.indr.threethreefive.favorites.FavoritesStore;
import ch.indr.threethreefive.favorites.model.Favorite;
import ch.indr.threethreefive.libs.Environment;
import ch.indr.threethreefive.libs.PageCommand;
import ch.indr.threethreefive.libs.PageLink;
import rx.subjects.BehaviorSubject;

public class ToggleFavorite extends PageCommand {

  private BehaviorSubject<Boolean> isFavorite = BehaviorSubject.create();

  private Favorite favorite;

  public ToggleFavorite(@NonNull FavoritesStore favoritesStore, @NonNull PageLink pageLink) {

    this.favorite = new Favorite(0, pageLink.getName(), pageLink.getUri().toString(), null);

    this.isFavorite.onNext(favoritesStore.isFavorite(pageLink));

    this.isFavorite
        .map(v -> v ? "Remove from Favorites" : "Add to Favorites")
        .subscribe(this.name);
  }

  @Override public @NonNull String getName() {
    return this.isFavorite.getValue() ? "Remove from Favorites" : "Add to Favorites";
  }

  @Override public String getDescription() {
    return getName();
  }

  @Override public void execute(@NonNull Environment environment) {
    FavoritesStore favoritesStore = environment.favoritesStore();

    if (!favoritesStore.isFavorite(favorite.getPageUri())) {
      addFavorite(environment, favoritesStore);
    } else {
      removeFavorite(environment, favoritesStore);
    }
  }

  private void addFavorite(@NonNull Environment environment, FavoritesStore favoritesStore) {
    favoritesStore.add(this.favorite);
    isFavorite.onNext(true);
    environment.toastManager().toast("Favorite added: " + favorite.getTitle());
    environment.speaker().command().favoriteAdded();
  }

  private void removeFavorite(@NonNull Environment environment, FavoritesStore favoritesStore) {
    favoritesStore.remove(favorite.getPageUri());
    isFavorite.onNext(false);
    environment.toastManager().toast("Favorite removed: " + favorite.getTitle());
    environment.speaker().command().favoriteRemoved();
  }
}
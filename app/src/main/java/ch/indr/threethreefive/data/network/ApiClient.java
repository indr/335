/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.indr.threethreefive.data.network.radioBrowser.CountriesRequest;
import ch.indr.threethreefive.data.network.radioBrowser.LanguagesRequest;
import ch.indr.threethreefive.data.network.radioBrowser.StationRequest;
import ch.indr.threethreefive.data.network.radioBrowser.StationsRequest;
import ch.indr.threethreefive.data.network.radioBrowser.StationsSearchRequest;
import ch.indr.threethreefive.data.network.radioBrowser.TagsRequest;
import ch.indr.threethreefive.data.network.radioBrowser.model.Country;
import ch.indr.threethreefive.data.network.radioBrowser.model.Genre;
import ch.indr.threethreefive.data.network.radioBrowser.model.GenreMaps;
import ch.indr.threethreefive.data.network.radioBrowser.model.GenreNames;
import ch.indr.threethreefive.data.network.radioBrowser.model.GenresBuilder;
import ch.indr.threethreefive.data.network.radioBrowser.model.Language;
import ch.indr.threethreefive.data.network.radioBrowser.model.Station;
import ch.indr.threethreefive.data.network.radioBrowser.model.Tag;
import ch.indr.threethreefive.data.network.radioBrowser.transformers.StationsToGenreTransformer;
import ch.indr.threethreefive.data.network.radioBrowser.transformers.TagsToGenresTransformer;
import ch.indr.threethreefive.data.network.radioBrowser.transformers.Transformers;
import ch.indr.threethreefive.data.network.radioBrowser.transformers.UnionTransformer;
import ch.indr.threethreefive.libs.net.RobospiceManager;
import timber.log.Timber;

import static ch.indr.threethreefive.data.network.radioBrowser.RadioBrowserInfoRequest.ORDER_ASC;
import static ch.indr.threethreefive.data.network.radioBrowser.RadioBrowserInfoRequest.ORDER_DESC;

public class ApiClient {

  private RobospiceManager robospiceManager;

  public ApiClient(@NonNull Context context) {
    GenreNames.load(context.getResources());
    GenreMaps.load(context.getResources());
  }

  public void setRobospiceManager(@NonNull RobospiceManager robospiceManager) {
    this.robospiceManager = robospiceManager;
  }

  public void getCountries(@NonNull RequestListener<Country[]> listener) {
    robospiceManager.execute(new CountriesRequest(), listener);
  }

  public void getGenres(RequestListener<Collection<Genre>> listener) {
    robospiceManager.execute(new TagsRequest(), new TagsToGenresTransformer(listener));
  }

  public void getGenresByCountry(@NonNull String countryId, @NonNull RequestListener<List<Genre>> listener) {
    robospiceManager.execute(StationsRequest.byCountry(countryId), new StationsToGenreTransformer(listener));
  }

  public void getLanguages(RequestListener<Language[]> listener) {
    robospiceManager.execute(new LanguagesRequest(), listener);
  }

  public void getNewStations(int number, RequestListener<List<Station>> listener) {
    robospiceManager.execute(StationsRequest.recent(number), Transformers.ArrayToList(listener));
  }

  public void getStation(String stationId, RequestListener<Station[]> listener) {
    robospiceManager.execute(new StationRequest(stationId), listener);
  }

  public void getStationsByCountry(String country, RequestListener<List<Station>> listener) {
    robospiceManager.execute(StationsRequest.byCountry(country), Transformers.ArrayToList(listener));
  }

  public void getStationsByCountryAndGenre(String countryId, String genreId, RequestListener<List<Station>> listener) {
    final Genre genre = GenresBuilder.getGenre(genreId);
    final List<Tag> tags = genre.getTags();
    if (tags.size() == 0) {
      Timber.w("Genre %s contains no tags, %s", genre.toString(), this.toString());
      listener.onRequestSuccess(new ArrayList<>());
    } else {
      RequestListener<Station[]> unionTransformer = new UnionTransformer<>(tags.size(), listener);
      for (Tag tag : tags) {
        final StationsSearchRequest request = new StationsSearchRequest()
            .where("country", countryId)
            .where("countryExact", "true")
            .where("tag", tag.getId())
            .where("tagExact", "true")
            .order("name", ORDER_ASC);
        robospiceManager.execute(request, unionTransformer);
      }
    }
  }

  public void getStationsByLanguage(String language, RequestListener<List<Station>> listener) {
    robospiceManager.execute(StationsRequest.byLanguage(language), Transformers.ArrayToList(listener));
  }

  public void getStationsByGenre(Genre genre, RequestListener<List<Station>> listener) {
    final List<Tag> tags = genre.getTags();
    if (tags.size() == 0) {
      Timber.w("Genre %s contains no tags, %s", genre.toString(), this.toString());
      listener.onRequestSuccess(new ArrayList<>());
    } else {
      RequestListener<Station[]> unionTransformer = new UnionTransformer<>(tags.size(), listener);
      for (Tag tag : tags) {
        robospiceManager.execute(StationsRequest.byTag(tag.getId()), unionTransformer);
      }
    }
  }

  public void getTrendingStations(RequestListener<List<Station>> listener) {
    final StationsSearchRequest request = new StationsSearchRequest()
        .expiresIn(DurationInMillis.ONE_MINUTE)
        .order("clicktrend", ORDER_DESC)
        .limit(50);
    robospiceManager.execute(request, Transformers.ArrayToList(listener));
  }
}

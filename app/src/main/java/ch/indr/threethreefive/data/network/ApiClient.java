/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.indr.threethreefive.data.network.radioBrowser.CountriesRequest;
import ch.indr.threethreefive.data.network.radioBrowser.LanguagesRequest;
import ch.indr.threethreefive.data.network.radioBrowser.StationRequest;
import ch.indr.threethreefive.data.network.radioBrowser.StationsRequest;
import ch.indr.threethreefive.data.network.radioBrowser.StationsSearchRequest;
import ch.indr.threethreefive.data.network.radioBrowser.TagsRequest;
import ch.indr.threethreefive.data.network.radioBrowser.model.Country;
import ch.indr.threethreefive.data.network.radioBrowser.model.Genre;
import ch.indr.threethreefive.data.network.radioBrowser.model.GenreMaps;
import ch.indr.threethreefive.data.network.radioBrowser.model.GenresBuilder;
import ch.indr.threethreefive.data.network.radioBrowser.model.Language;
import ch.indr.threethreefive.data.network.radioBrowser.model.Station;
import ch.indr.threethreefive.data.network.radioBrowser.model.Tag;
import ch.indr.threethreefive.data.network.radioBrowser.transformers.ArrayToListTransformer;
import ch.indr.threethreefive.data.network.radioBrowser.transformers.StationsToGenreTransformer;
import ch.indr.threethreefive.data.network.radioBrowser.transformers.TagsToGenresTransformer;
import ch.indr.threethreefive.data.network.radioBrowser.transformers.UnionTransformer;
import ch.indr.threethreefive.libs.net.RobospiceManager;
import timber.log.Timber;

public class ApiClient {

  private RobospiceManager robospiceManager;

  public ApiClient(@NonNull Context context) {
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

  public void getNewStations(int number, RequestListener<Station[]> listener) {
    robospiceManager.execute(StationsRequest.recent(number), listener);
  }

  public void getStation(String stationId, RequestListener<Station[]> listener) {
    robospiceManager.execute(new StationRequest(stationId), listener);
  }

  public void getStationsByCountry(String country, RequestListener<List<Station>> listener) {
    robospiceManager.execute(StationsRequest.byCountry(country), new ArrayToListTransformer<>(listener));
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
        final Map<String, String> params = new HashMap<>();
        params.put("country", countryId);
        params.put("countryExact", "true");
        params.put("tag", tag.getId());
        params.put("tagExact", "true");
        robospiceManager.execute(new StationsSearchRequest(params), unionTransformer);
      }
    }
  }

  public void getStationsByLanguage(String language, RequestListener<List<Station>> listener) {
    robospiceManager.execute(StationsRequest.byLanguage(language), new ArrayToListTransformer<>(listener));
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
}

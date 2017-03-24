/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network;

import android.support.annotation.NonNull;

import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

import ch.indr.threethreefive.data.network.radioBrowser.CountriesRequest;
import ch.indr.threethreefive.data.network.radioBrowser.LanguagesRequest;
import ch.indr.threethreefive.data.network.radioBrowser.StationRequest;
import ch.indr.threethreefive.data.network.radioBrowser.StationsRequest;
import ch.indr.threethreefive.data.network.radioBrowser.TagsRequest;
import ch.indr.threethreefive.data.network.radioBrowser.model.Country;
import ch.indr.threethreefive.data.network.radioBrowser.model.Genre;
import ch.indr.threethreefive.data.network.radioBrowser.model.Language;
import ch.indr.threethreefive.data.network.radioBrowser.model.Station;
import ch.indr.threethreefive.data.network.radioBrowser.transformers.StationsToGenreTransformer;
import ch.indr.threethreefive.data.network.radioBrowser.transformers.TagsToGenresTransformer;
import ch.indr.threethreefive.libs.net.RobospiceManager;

public class ApiClient {

  private RobospiceManager robospiceManager;

  public ApiClient() {
  }

  public void setRobospiceManager(@NonNull RobospiceManager robospiceManager) {
    this.robospiceManager = robospiceManager;
  }

  public void getCountries(@NonNull RequestListener<Country[]> listener) {
    robospiceManager.execute(new CountriesRequest(), listener);
  }

  public void getGenres(RequestListener<List<Genre>> listener) {
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

  public void getStationsByCountry(String country, RequestListener<Station[]> listener) {
    robospiceManager.execute(StationsRequest.byCountry(country), listener);
  }

  public void getStationsByLanguage(String language, RequestListener<Station[]> listener) {
    robospiceManager.execute(StationsRequest.byLanguage(language), listener);
  }

  public void getStationsByGenre(String genre, RequestListener<Station[]> listener) {
    robospiceManager.execute(StationsRequest.byGenre(genre), listener);
  }
}

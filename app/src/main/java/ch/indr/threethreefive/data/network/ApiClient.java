/*
 * Copyright (c) 2017 Reto Inderbitzin (mail@indr.ch)
 *
 * For the full copyright and license information, please view
 * the LICENSE file that was distributed with this source code.
 */

package ch.indr.threethreefive.data.network;

import android.support.annotation.NonNull;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.indr.threethreefive.libs.net.RobospiceManager;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.CountriesRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.StationsRequest;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Country;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Station;
import ch.indr.threethreefive.radio.radioBrowserInfo.api.json.Tag;

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

  public void getGenresByCountry(@NonNull String countryId, @NonNull RequestListener<List<Tag>> listener) {
    robospiceManager.execute(StationsRequest.byCountry(countryId), new ResponseTransformer<Station[], List<Tag>>(listener) {
      @Override public void onRequestSuccess(Station[] stations) {
        final Map<String, Integer> map = new HashMap<String, Integer>();
        for (Station station : stations) {
          final String[] tags = station.getTags();
          for (String tag : tags) {
            if (map.containsKey(tag)) {
              map.put(tag, map.get(tag) + 1);
            } else {
              map.put(tag, 1);
            }
          }
        }

        List<Tag> result = new ArrayList<>(map.size());
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
          result.add(new Tag(entry.getKey(), entry.getValue()));
        }
        listener.onRequestSuccess(result);
      }
    });
  }


  private abstract class ResponseTransformer<TResult, TResponse> implements RequestListener<TResult> {
    private final RequestListener<TResponse> listener;

    ResponseTransformer(RequestListener<TResponse> listener) {
      this.listener = listener;
    }

    @Override public void onRequestFailure(SpiceException spiceException) {
      listener.onRequestFailure(spiceException);
    }
  }
}

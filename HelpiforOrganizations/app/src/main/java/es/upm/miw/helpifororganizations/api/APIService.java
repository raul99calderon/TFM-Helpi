package es.upm.miw.helpifororganizations.api;

import es.upm.miw.helpifororganizations.models.position_stack.PositionStack;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {
    @GET("/v1/forward")
    Call<PositionStack> getGeocode(@Query("access_key") String access_key, @Query("query") String query);
}

package is.hbv501g.lootapp.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// ApiClient.java
public class ApiClient {
    private static final String BASE_URL = "https://vefthjonusta-magicgathering-db.vercel.app/api/";
    private static ApiService apiService;
    private static String authToken;

    public static ApiService getApiService() {
        if (apiService == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // Add auth interceptor
            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();

                if (authToken != null) {
                    requestBuilder.header("Authorization", "Bearer " + authToken);
                }

                requestBuilder.header("Accept", "application/json");
                requestBuilder.method(original.method(), original.body());

                return chain.proceed(requestBuilder.build());
            });

            // Add logging interceptor for debugging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            apiService = retrofit.create(ApiService.class);
        }

        return apiService;
    }

    public static void setAuthToken(String token) {
        authToken = token;
    }
}
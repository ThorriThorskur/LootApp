package is.hbv501g.lootapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import is.hbv501g.lootapp.adapter.WishlistAdapter;
import is.hbv501g.lootapp.api.ApiClient;
import is.hbv501g.lootapp.models.WishlistCard;
import is.hbv501g.lootapp.models.api.WishlistResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView recyclerViewWishlist;
    private WishlistAdapter wishlistAdapter;
    private List<WishlistCard> wishlistList;
    private ProgressBar progressBar;
    private TextView textViewEmptyWishlist;
    private Button buttonHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        buttonHome = findViewById(R.id.buttonHome);
        recyclerViewWishlist = findViewById(R.id.recyclerViewWishlist);
        progressBar = findViewById(R.id.progressBar);
        textViewEmptyWishlist = findViewById(R.id.textViewEmptyWishlist);

        wishlistList = new ArrayList<>();
        wishlistAdapter = new WishlistAdapter(wishlistList, this);
        recyclerViewWishlist.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewWishlist.setAdapter(wishlistAdapter);

        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WishlistActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loadWishlist();
    }

    private void loadWishlist() {
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getApiService().getWishlist().enqueue(new Callback<WishlistResponse>() {
            @Override
            public void onResponse(Call<WishlistResponse> call, Response<WishlistResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    wishlistList.clear();
                    wishlistList.addAll(response.body().getWishlist());
                    wishlistAdapter.notifyDataSetChanged();

                    if (wishlistList.isEmpty()) {
                        textViewEmptyWishlist.setVisibility(View.VISIBLE);
                    } else {
                        textViewEmptyWishlist.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(WishlistActivity.this, "Failed to load wishlist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WishlistResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(WishlistActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
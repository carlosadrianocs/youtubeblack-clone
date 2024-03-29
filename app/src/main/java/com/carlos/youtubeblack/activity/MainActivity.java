package com.carlos.youtubeblack.activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import com.carlos.youtubeblack.R;
import com.carlos.youtubeblack.adapter.AdapterVideo;
import com.carlos.youtubeblack.api.YoutubeService;
import com.carlos.youtubeblack.helper.RetrofitConfig;
import com.carlos.youtubeblack.helper.YoutubeConfig;
import com.carlos.youtubeblack.listener.RecyclerItemClickListener;
import com.carlos.youtubeblack.model.Item;
import com.carlos.youtubeblack.model.Resultado;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/*
 -----------CARLOS ADRIANO-------------
*/

public class MainActivity extends AppCompatActivity {


    private RecyclerView recyclerVideos;
    private MaterialSearchView searchView;

    private List<Item> videos = new ArrayList<>();
    private Resultado resultado;
    private AdapterVideo adapterVideo;


    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        // TODO - Configurações iniciais
        retrofit = RetrofitConfig.getRetrofit();


        // TODO - Configurando a toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("YOUTUBE BLACK");
        setSupportActionBar( toolbar );

        // TODO - Recupera vídeos
        recuperarVideos("");

        // TODO - Configura métodos para SearchView
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                recuperarVideos( query );
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                recuperarVideos("");
            }
        });

    }


    // TODO - Inicializar componentes

    private void initViews(){

        recyclerVideos = findViewById(R.id.recyclerVideos);
        searchView = findViewById(R.id.searchView);


    }

    private void recuperarVideos(String pesquisa){

        String q = pesquisa.replaceAll(" ", "+");
        YoutubeService youtubeService = retrofit.create( YoutubeService.class );

        youtubeService.recuperarVideos(
            "snippet", "date", "20",
                YoutubeConfig.CHAVE_YOUTUBE_API, YoutubeConfig.CANAL_ID, q
        ).enqueue(new Callback<Resultado>() {
            @Override
            public void onResponse(Call<Resultado> call, Response<Resultado> response) {
                Log.d("resultado", "resultado: " + response.toString() );
                if( response.isSuccessful() ){
                    resultado = response.body();
                    videos = resultado.items;
                    configurarRecyclerView();
                }
            }

            @Override
            public void onFailure(Call<Resultado> call, Throwable t) {

            }
        });

    }

    public void configurarRecyclerView(){
        adapterVideo = new AdapterVideo(videos, this);
        recyclerVideos.setHasFixedSize( true );
        recyclerVideos.setLayoutManager(new LinearLayoutManager(this));
        recyclerVideos.setAdapter( adapterVideo );

        // TODO - Configura evento de clique
        recyclerVideos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerVideos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Item video = videos.get(position);
                                String idVideo = video.id.videoId;

                                Intent i = new Intent(MainActivity.this, PlayerActivity.class);
                                i.putExtra("idVideo", idVideo );
                                startActivity(i);

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        searchView.setMenuItem( item );

        return true;
    }
}

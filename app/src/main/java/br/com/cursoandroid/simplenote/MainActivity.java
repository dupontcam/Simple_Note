package br.com.cursoandroid.simplenote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import br.com.cursoandroid.simplenote.adapter.Adapter;
import br.com.cursoandroid.simplenote.database.NoteDatabase;
import br.com.cursoandroid.simplenote.model.Note;

public class MainActivity extends AppCompatActivity {

    private boolean isGridLayout = false;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.materialToolbar);
        setSupportActionBar(toolbar);

        NoteDatabase db = new NoteDatabase(this);
        List<Note> notes = db.getNotes();
        if (notes == null) {
            notes = new ArrayList<>();
        }

        // Encontra o FloatingActionButton pelo ID
        FloatingActionButton fab = findViewById(R.id.fabAdd);

        // Define o OnClickListener para o FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inicia a Activity AddNote
                startActivity(new Intent(MainActivity.this, AddNote.class));
            }
        });

        recyclerView = findViewById(R.id.listOfNotes);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isGridLayout = sharedPreferences.getBoolean("isGridLayout", false); // false é o valor padrão

        adapter = new Adapter(notes, isGridLayout);

        recyclerView.setAdapter(adapter);

        // Define o layout inicial como lista
        setLayoutManager();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Obtenha os itens do menu
        MenuItem itemList = menu.findItem(R.id.list);
        MenuItem itemGrid = menu.findItem(R.id.grid);

        // Verifique o tema atual e defina a cor apropriada
        int iconColor = getResources().getColor(R.color.black, getTheme()); // Tema claro
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            iconColor = getResources().getColor(R.color.white, getTheme()); // Tema escuro
        }

        // Aplique a cor aos ícones usando AppCompatResources para garantir compatibilidade
        Drawable iconList = AppCompatResources.getDrawable(this, R.drawable.baseline_view_list_24);
        Drawable iconGrid = AppCompatResources.getDrawable(this, R.drawable.baseline_grid_view_24);

        if (iconList != null && iconGrid != null) {
            iconList.setTint(iconColor);
            iconGrid.setTint(iconColor);
            itemList.setIcon(iconList);
            itemGrid.setIcon(iconGrid);
        }

        search(menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == R.id.list) {
            isGridLayout = false;
            invalidateOptionsMenu(); // Força a atualização do menu
            setLayoutManager();
        } else if (item.getItemId() == R.id.grid) {
            isGridLayout = true;
            invalidateOptionsMenu(); // Força a atualização do menu
            setLayoutManager();
        }
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isGridLayout", isGridLayout);
        editor.apply();
        return super.onOptionsItemSelected(item);

    }

    public void search(Menu menu) {

        // Referência ao item Grid e Lista
        MenuItem gridItem = menu.findItem(R.id.grid);
        MenuItem listItem = menu.findItem(R.id.list);



        // Referência ao item de pesquisa
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);

        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setQueryHint("Pesquise anotações...");

            // Listener para capturar o texto de busca
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    performSearch(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    performSearch(newText);
                    return true;
                }
            });

            // Listener para quando a pesquisa é iniciada
            searchView.setOnSearchClickListener(v -> {
                // Ocultar os ícones de list/grid
                gridItem.setVisible(false);
                listItem.setVisible(false);
            });

            // Listener para quando a pesquisa é finalizada (opcional)
            searchView.setOnCloseListener(() -> {
                // Recrie o menu para mostrar os ícones de list/grid, se necessário
                gridItem.setVisible(true);
                listItem.setVisible(true);
                invalidateOptionsMenu();
                return false;
            });
        }
    }

    private void performSearch(String query) {

        // Obter as anotações do banco de dados com base na consulta
        NoteDatabase db = new NoteDatabase(this);
        List<Note> searchResults = db.searchNotes(query);

        // Atualizar o Adapter com os resultados da pesquisa
        adapter.setNotes(searchResults);
        adapter.notifyDataSetChanged();
    }

    // Atualizar o menu com o ícone correto visível
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.grid).setVisible(!isGridLayout);
        menu.findItem(R.id.list).setVisible(isGridLayout);
        return super.onPrepareOptionsMenu(menu);
    }

    // Método para definir o layout do RecyclerView
    private void setLayoutManager() {
        if (isGridLayout) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        adapter.setGridLayout(isGridLayout);
        recyclerView.getRecycledViewPool().clear(); // Limpar o cache (opcional)
    }
}
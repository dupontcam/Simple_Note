package br.com.cursoandroid.simplenote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
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
    private RecyclerView recyclerView;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        EdgeToEdge.enable(this);
        // Aplica o tema antes de criar a Activity
        applyThemeFromPreferences();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupUIElements();
        loadNotesAndSetupRecyclerView();
    }

    private void applyThemeFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String themePreference = sharedPreferences.getString("theme", "DefaultTheme");

        switch (themePreference) {
            case "BlueTheme":
                setTheme(R.style.Theme_Blue);
                break;
            case "GreenTheme":
                setTheme(R.style.Theme_Green);
                break;
            case "BlackTheme":
                setTheme(R.style.Theme_Black);
                break;
            case "YellowTheme":
                setTheme(R.style.Theme_Yellow);
                break;
            case "OrangeTheme":
                setTheme(R.style.Theme_Orange);
                break;
            case "PurpleTheme":
                setTheme(R.style.Theme_Purple);
                break;
            case "DeepPurpleTheme":
                setTheme(R.style.Theme_DeepPurple);
                break;
            case "IndigoTheme":
                setTheme(R.style.Theme_Indigo);
                break;
            case "DeepOrangeTheme":
                setTheme(R.style.Theme_DeepOrange);
                break;
            case "TealTheme":
                setTheme(R.style.Theme_Teal);
                break;
            default:
                setTheme(R.style.Theme_SimpleNote);
        }
    }

    private void setupUIElements() {
        Toolbar toolbar = findViewById(R.id.materialToolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AddNote.class)));

        recyclerView = findViewById(R.id.listOfNotes);
    }

    private void loadNotesAndSetupRecyclerView() {
        NoteDatabase db = new NoteDatabase(this);
        List<Note> notes = db.getNotes();
        if (notes == null) {
            notes = new ArrayList<>();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isGridLayout = sharedPreferences.getBoolean("isGridLayout", false);

        adapter = new Adapter(notes, isGridLayout);
        recyclerView.setAdapter(adapter);
        setLayoutManager();
    }

    private void setLayoutManager() {
        if (isGridLayout) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        adapter.setGridLayout(isGridLayout);
        recyclerView.getRecycledViewPool().clear(); // Limpar o cache (opcional)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);


        // Obtenha os itens do menu
        MenuItem itemList = menu.findItem(R.id.list);
        MenuItem itemGrid = menu.findItem(R.id.grid);
        MenuItem itemTemas = menu.findItem(R.id.submenu_themes);

        // Verifique o tema atual e defina a cor apropriada
        int iconColor = getResources().getColor(R.color.black, getTheme()); // Tema claro
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            iconColor = getResources().getColor(R.color.white, getTheme()); // Tema escuro
        }

        // Aplique a cor aos ícones usando AppCompatResources para garantir compatibilidade
        Drawable iconList = AppCompatResources.getDrawable(this, R.drawable.baseline_view_list_24);
        Drawable iconGrid = AppCompatResources.getDrawable(this, R.drawable.baseline_grid_view_24);
        Drawable iconTemas = AppCompatResources.getDrawable(this, R.drawable.baseline_color_lens_24);

        if (iconList != null && iconGrid != null && iconTemas != null) {
            iconList.setTint(iconColor);
            iconGrid.setTint(iconColor);
            iconTemas.setTint(iconColor);
            itemList.setIcon(iconList);
            itemGrid.setIcon(iconGrid);
            itemTemas.setIcon(iconTemas);
        }

        search(menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.submenu_themes) {
            // Crie o PopupMenu usando o contexto da Activity (tema padrão)
            PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.submenu_themes));

            // Infle o submenu do arquivo dedicado
            popupMenu.getMenuInflater().inflate(R.menu.submenu_themes, popupMenu.getMenu());

            // Adicione ações para cada item, se necessário
            popupMenu.setOnMenuItemClickListener(subMenuitem -> {
                int itemId = subMenuitem.getItemId();

                if (itemId == R.id.theme_blue) {
                    saveThemePreference("BlueTheme");
                    applyThemeFromPreferences();
                    recreate();
                    return true;
                } else if (itemId == R.id.theme_green) {
                    saveThemePreference("GreenTheme");
                    applyThemeFromPreferences();
                    recreate();
                    return true;
                } else if (itemId == R.id.theme_black) {
                    saveThemePreference("BlackTheme");
                    applyThemeFromPreferences();
                    recreate();
                    return true;
                } else if (itemId == R.id.theme_yellow) {
                    saveThemePreference("YellowTheme");
                    applyThemeFromPreferences();
                    recreate();
                    return true;
                } else if (itemId == R.id.theme_orange) {
                    saveThemePreference("OrangeTheme");
                    applyThemeFromPreferences();
                    recreate();
                    return true;
                } else if (itemId == R.id.theme_purple) {
                    saveThemePreference("PurpleTheme");
                    applyThemeFromPreferences();
                    recreate();
                    return true;
                } else if (itemId == R.id.theme_deepPurple) {
                    saveThemePreference("DeepPurpleTheme");
                    applyThemeFromPreferences();
                    recreate();
                    return true;
                } else if (itemId == R.id.theme_indigo) {
                    saveThemePreference("IndigoTheme");
                    applyThemeFromPreferences();
                    recreate();
                    return true;
                } else if (itemId == R.id.theme_deepOrange) {
                    saveThemePreference("DeepOrangeTheme");
                    applyThemeFromPreferences();
                    recreate();
                    return true;
                } else if (itemId == R.id.theme_teal) {
                    saveThemePreference("TealTheme");
                    applyThemeFromPreferences();
                    recreate();
                    return true;
                } else if (itemId == R.id.theme_default) {
                    saveThemePreference("default");
                    applyThemeFromPreferences();
                    recreate();
                    return true;
                }
                return true;
            });

            // Exiba o PopupMenu
            popupMenu.show();
            return true;

        } else if (item.getItemId() == R.id.list) {
            isGridLayout = false;
            invalidateOptionsMenu(); // Força a atualização do menu
            setLayoutManager();
            saveLayoutPreference(isGridLayout);
            return true;

        } else if (item.getItemId() == R.id.grid) {
            isGridLayout = true;
            invalidateOptionsMenu(); // Força a atualização do menu
            setLayoutManager();
            saveLayoutPreference(isGridLayout);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveLayoutPreference(boolean isGridLayout) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isGridLayout", isGridLayout);
        editor.apply();
    }

    private void search(Menu menu) {

        // Referência ao item Grid e Lista
        MenuItem gridItem = menu.findItem(R.id.grid);
        MenuItem listItem = menu.findItem(R.id.list);
        MenuItem temasItem = menu.findItem(R.id.submenu_themes);



        // Referência ao item de pesquisa
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);

        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setQueryHint("Pesquise anotações...");

            // Listener para capturar o texto de busca
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    adapter.filter(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.filter(newText);
                    return true;
                }
            });

            // Listener para quando a pesquisa é iniciada
            searchView.setOnSearchClickListener(v -> {
                // Ocultar os ícones de list/grid
                gridItem.setVisible(false);
                listItem.setVisible(false);
                temasItem.setVisible(false);
            });

            // Listener para quando a pesquisa é finalizada (opcional)
            searchView.setOnCloseListener(() -> {
                // Recrie o menu para mostrar os ícones de list/grid, se necessário
                gridItem.setVisible(true);
                listItem.setVisible(true);
                temasItem.setVisible(true);
                invalidateOptionsMenu();
                return false;
            });
        }
    }

    private void saveThemePreference(String theme) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("theme", theme);
        editor.apply();
    }

    // Atualizar o menu com o ícone correto visível
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.grid).setVisible(!isGridLayout);
        menu.findItem(R.id.list).setVisible(isGridLayout);
        return super.onPrepareOptionsMenu(menu);
    }
}
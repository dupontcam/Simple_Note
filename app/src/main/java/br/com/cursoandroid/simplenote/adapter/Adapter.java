package br.com.cursoandroid.simplenote.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.cursoandroid.simplenote.R;
import br.com.cursoandroid.simplenote.UpdateNote;
import br.com.cursoandroid.simplenote.model.Note;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static List<Note> notes;
    private boolean isGridLayout;

    public Adapter(List<Note> notes, boolean isGridLayout) {
        this.isGridLayout = isGridLayout;
        this.notes = notes;
    }

    public void setGridLayout(boolean isGridLayout) {
        this.isGridLayout = isGridLayout;
        notifyDataSetChanged(); // Atualiza a exibição
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (isGridLayout) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_grid_view, parent, false);
            return new GridViewHolder(view); // Criar GridViewHolder para grade
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_view, parent, false);
            return new ListViewHolder(view); // Criar ListViewHolder para lista
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof GridViewHolder) {
            GridViewHolder gridHolder = (GridViewHolder) holder;
            gridHolder.noteTitle.setText(notes.get(position).getTitle());
            gridHolder.noteDescription.setText(notes.get(position).getDescription());
        } else if (holder instanceof ListViewHolder) {
            ListViewHolder listHolder = (ListViewHolder) holder;
            listHolder.noteTitle.setText(notes.get(position).getTitle());
            listHolder.nDate.setText(notes.get(position).getDate());
            listHolder.nTime.setText(notes.get(position).getTime());
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<Note> searchResults) {
        this.notes = searchResults;
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView noteTitle, nDate, nTime;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.noteTitle);
            nDate = itemView.findViewById(R.id.nDate);
            nTime = itemView.findViewById(R.id.nTime);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Note clickedNote = notes.get(position);
                Intent intent = new Intent(view.getContext(), UpdateNote.class);
                intent.putExtra("noteId", clickedNote.getID()); // Passe o ID ou outros dados da anotação
                intent.putExtra("noteTitle", clickedNote.getTitle());
                intent.putExtra("noteDescription", clickedNote.getDescription());
                view.getContext().startActivity(intent);
            }
        }
    }

    public static class GridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView noteTitle;
        public TextView noteDescription;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.noteTitle); // Inicializar noteTitle
            noteDescription = itemView.findViewById(R.id.noteDescription); // Inicializar noteDescription

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Note clickedNote = notes.get(position);
                Intent intent = new Intent(view.getContext(), UpdateNote.class);
                intent.putExtra("noteId", clickedNote.getID()); // Passe o ID ou outros dados da anotação
                intent.putExtra("noteTitle", clickedNote.getTitle());
                intent.putExtra("noteDescription", clickedNote.getDescription());
                view.getContext().startActivity(intent);
            }
        }
    }

    public void filter(String text) {

        List<Note> filteredNotes = new ArrayList<>();
        for (Note note : notes) {  // Mantenha uma lista original para referência
            if (note.getTitle().contains(text) || note.getDescription().contains(text)) {
                filteredNotes.add(note);
            }
        }
        updateList(filteredNotes);
    }

    public void updateList(List<Note> newNotes) {

        notes.clear();
        notes.addAll(newNotes);
        notifyDataSetChanged();
    }

}

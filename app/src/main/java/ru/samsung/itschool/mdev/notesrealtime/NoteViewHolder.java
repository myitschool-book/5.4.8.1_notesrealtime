package ru.samsung.itschool.mdev.notesrealtime;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ru.samsung.itschool.mdev.notesrealtime.models.Note;


public class NoteViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public TextView bodyView;

    public NoteViewHolder(View itemView) {
        super(itemView);
        titleView = itemView.findViewById(R.id.postTitle);
        authorView = itemView.findViewById(R.id.postAuthor);
        bodyView = itemView.findViewById(R.id.postBody);
    }

    public void bindToNote(Note note) {
        titleView.setText(note.title);
        authorView.setText(note.author);
        bodyView.setText(note.body);
    }
}

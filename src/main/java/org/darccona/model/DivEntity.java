package org.darccona.model;

public class DivEntity {
    private boolean ShowNote = false;
    private boolean EditNote = false;
    private boolean EditFolder = false;
    private boolean AddNote = false;
    private boolean AddFolder = false;
    private boolean Search = false;

    private boolean Folder = true;
    private boolean Note = true;
    private boolean Input = true;

    public DivEntity(String div) {
        switch (div) {
            case "ShowNote": this.ShowNote = true; break;
            case "EditNote": this.EditNote = true; break;
            case "EditFolder": this.EditFolder = true; break;
            case "AddNote": this.AddNote = true; break;
            case "AddFolder":
                this.AddFolder = true;
                this.Note = false;
                break;
            case "Search":
                this.Search = true;
                this.Input = false;
                break;
        }
    }

    public boolean getShowNote() {
        return ShowNote;
    }
    public boolean getEditNote() {
        return EditNote;
    }
    public boolean getEditFolder() {
        return EditFolder;
    }
    public boolean getAddNote() {
        return AddNote;
    }
    public boolean getAddFolder() {
        return AddFolder;
    }
    public boolean getSearch() {
        return Search;
    }

    public boolean getFolder() {
        return Folder;
    }
    public boolean getNote() {
        return Note;
    }
    public boolean getInput() {
        return Input;
    }

    public void setFolder() {
        this.Folder = false;
    }
    public void setNote() {
        this.Note = false;
    }
    public void setInput() {
        this.Input = false;
    }
}


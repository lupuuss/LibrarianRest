package pl.lodz.pas.librarianrest.services.pages;

import java.util.List;

public class Page<T> {

    private final int number;
    private final List<T> content;

    public Page(int id, List<T> content) {
        this.number = id;
        this.content = content;
    }

    public int getNumber() {
        return number;
    }

    public List<T> getContent() {
        return content;
    }
}

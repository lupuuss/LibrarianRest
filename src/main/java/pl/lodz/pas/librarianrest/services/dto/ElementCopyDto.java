package pl.lodz.pas.librarianrest.services.dto;

public class ElementCopyDto {

    private final String id;

    private final  int number;
    private final  ElementDto element;

    public enum State {
        NEW, GOOD, USED, NEED_REPLACEMENT, DAMAGED
    }

    private State state;

    public ElementCopyDto(String id, int number,ElementDto element,  State state) {
        this.id = id;
        this.element = element;
        this.number = number;
        this.state = state;
    }

    public ElementDto getElement() {
        return element;
    }

    public int getNumber() {
        return number;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }
}

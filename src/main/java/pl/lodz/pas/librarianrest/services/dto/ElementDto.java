package pl.lodz.pas.librarianrest.services.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public abstract class ElementDto implements Comparable<ElementDto> {

    @NotBlank
    private String title;

    @NotBlank
    private String publisher;

    public ElementDto(String title, String publisher) {
        this.title = title;
        this.publisher = publisher;
    }

    public ElementDto() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public int compareTo(ElementDto o) {

        if (getClass() == o.getClass()) {
            return 0;
        } else if (this instanceof BookDto) {
            return 1;
        } else {
            return -1;
        }
    }
}

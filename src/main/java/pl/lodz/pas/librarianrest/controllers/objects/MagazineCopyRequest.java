package pl.lodz.pas.librarianrest.controllers.objects;

import pl.lodz.pas.librarianrest.services.dto.ElementCopyDto;

public class MagazineCopyRequest {
    private String issn;
    private int issue;
    private ElementCopyDto.State state;

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public int getIssue() {
        return issue;
    }

    public void setIssue(int issue) {
        this.issue = issue;
    }

    public ElementCopyDto.State getState() {
        return state;
    }

    public void setState(ElementCopyDto.State state) {
        this.state = state;
    }
}

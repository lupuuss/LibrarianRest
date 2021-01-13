package pl.lodz.pas.librarianrest;

import java.time.LocalDateTime;

public interface DateProvider {

    LocalDateTime now();
}

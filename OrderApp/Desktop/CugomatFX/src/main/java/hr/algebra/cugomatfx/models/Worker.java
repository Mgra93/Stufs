package hr.algebra.cugomatfx.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Worker {
    private int id;
    private Client client;
    private User user;

    @Override
    public String toString() {
        return user.getUsername();
    }
}

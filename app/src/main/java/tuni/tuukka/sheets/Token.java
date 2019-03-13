package tuni.tuukka.sheets;

import java.util.Optional;

public class Token {
    private static Optional<String> token;
    private static boolean tokenSet = false;

    Optional<String> getToken() {
        return token;
    }

    void setToken(String token) {
        this.token = Optional.of(token);
    }
}

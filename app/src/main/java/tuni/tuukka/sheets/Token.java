package tuni.tuukka.sheets;

import java.util.Optional;

public class Token {
    private static Optional<String> token;
    private static boolean tokenSet = false;

    static Optional<String> getToken() {
        return token;
    }

    static void setToken(String newToken) {
        token = Optional.of(newToken);
    }
}

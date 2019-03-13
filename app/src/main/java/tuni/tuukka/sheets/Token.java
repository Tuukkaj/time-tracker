package tuni.tuukka.sheets;

public class Token {
    private static String token;
    private static boolean tokenSet = false;

    String getToken() {
        return token;
    }

    void setToken(String token) {
        this.token = token;
        tokenSet = true;
    }

    public boolean isTokenSet() {
        return tokenSet;
    }
}

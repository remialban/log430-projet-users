package ca.log430.users.domain.model;

public class TokenResponse {
    private String token;
    private String expiresAt;

    public TokenResponse(String token, String expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }
}

package com.study.security;

public class Jwt {
  private final String issuer;

  private final String clientSecret;

  private final int expirySeconds;

  private final Algorithm algorithm;

  private final JWTVerifier jwtVerifier;

  public Jwt(String issuer, String clientSecret, int expirySeconds) {
    this.issuer = issuer;
    this.clientSecret = clientSecret;
    this.expirySeconds = expirySeconds;
    this.algorithm = Algorithm.HMAC512(clientSecret);
    this.jwtVerifier = com.auth0.jwt.JWT.require(algorithm)
        .withIssuer(issuer)
        .build();
  }

    @Override
    public String toString() {
      return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
          .append("userKey", userKey)
          .append("name", name)
          .append("email", email)
          .append("roles", Arrays.toString(roles))
          .append("iat", iat)
          .append("exp", exp)
          .toString();
    }
  }
}

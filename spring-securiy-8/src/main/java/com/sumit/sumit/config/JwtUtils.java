package com.sumit.sumit.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component 
public class JwtUtils {
	private  String jwtSigningKey = "secret";

	  public String extractUsername(String token) {
	    return extractClaim(token, Claims::getSubject);
	  }

	  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
	    final Claims claims = extractAllClaims(token);
	    return claimsResolver.apply(claims);
	  }
	  
	  public String generateToken(UserDetails userDetails) {
		  Map<String, Object> claims=new HashMap<>();
		  return createToken(claims, userDetails);
	  }

	  public String generateToken(Map<String , Object> claims, UserDetails userDetails) {
	    return createToken(claims, userDetails);
	  }

	  public String createToken(
	      Map<String, Object> claims,
	      UserDetails userDetails
	  ) {
	    return Jwts
	        .builder()
	        .setClaims(claims)
	        .setSubject(userDetails.getUsername())
	        .claim("authorities", userDetails.getAuthorities())
	        .setIssuedAt(new Date(System.currentTimeMillis()))
	        .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24)))
	        .signWith(SignatureAlgorithm.HS256,jwtSigningKey).compact();
	  }

	  public boolean isTokenValid(String token, UserDetails userDetails) {
	    final String username = extractUsername(token);
	    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
	  }

	  private boolean isTokenExpired(String token) {
	    return extractExpiration(token).before(new Date());
	  }

	  private Date extractExpiration(String token) {
	    return extractClaim(token, Claims::getExpiration);
	  }

	  private Claims extractAllClaims(String token) {
	    return Jwts
	        .parser()
	        .setSigningKey(jwtSigningKey)
	        .parseClaimsJws(token)
	        .getBody();
	  }
	  
	  public boolean hasClaim(String token, String claimName) {
		  final Claims claims=extractAllClaims(token);
		  return claims.get(claimName)!=null;
	  }

	
}

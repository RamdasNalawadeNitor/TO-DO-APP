package com.example.demo.services.jwt;

import com.example.demo.entities.Users;
import com.example.demo.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;


//import java.util.stream.Collectors;



@Service
public class JwtService {
    private static final Logger logger = Logger.getLogger(JwtService.class.getName());

    @Autowired
    private UserRepository userRepository;

    private final String SECRET_KEY="7b2598c3b4faa9969c0a24c9dc57cd482b9f8a48c984ba22df35c3377aebada3";

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public boolean isValid(String token,UserDetails user) {
        String username=extractUsername(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }


    public <T> T extractClaims(String token,Function<Claims, T> resolver){
        Claims claims=extractAllClaims(token);
        return resolver.apply(claims);
    }


    private Claims extractAllClaims(String token) {
        return  Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        // Collect all roles and put them into the claims
//        String roles = user.getAuthorities().stream()
//                .map(authority -> authority.getAuthority())
//                .collect(Collectors.joining(","));
        //claims.put("roles", roles);

        System.out.println(user);

//        if (user instanceof Employee) {
//            Employee employee = (Employee) user;
//            claims.put("employeeId", employee.getId());
//            System.out.println("EmployeeId id->   "+employee.getId());// Add employee ID to claims
//        }
        Optional<Users> optionalEmployee= userRepository.findByEmail(user.getUsername());
        Users user1=optionalEmployee.get();
        claims.put("userId",user1.getId()); // Need to check
        //claims.put("UserName",user1.getUsername());
        logger.info("Generating claims:{}"+user1.getAuthorities());

        return createToken(claims, user.getUsername());
    }

    public String createToken(Map<String, Object>claims, String subject) {
        String token=Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+24*60*60*1000))
                .signWith(getSignInKey())
                .compact();
        return token;
    }




    private SecretKey getSignInKey() {
        byte[] keyBytes=Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
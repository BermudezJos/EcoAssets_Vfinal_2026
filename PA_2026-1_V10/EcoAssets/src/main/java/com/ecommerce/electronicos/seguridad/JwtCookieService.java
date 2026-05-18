package com.ecommerce.electronicos.seguridad;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class JwtCookieService {

    public void addJwtCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(
            JwtAuthenticationFilter.JWT_COOKIE_NAME,
            token
        )
                .httpOnly(true)
                .secure(false) //secure(false): permite probar en
                                //local con HTTP. En producción debe cambiarse a true si
                                //se usa HTTPS.
                                //Preguntar por esto luego!!!!!!!
                .sameSite("Lax")
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();
        
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void clearJwtCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(
            JwtAuthenticationFilter.JWT_COOKIE_NAME,
            ""
        )
                .httpOnly(true)
                .secure(false) //lo mismo aqui, preguntar luego!!!!!!!
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
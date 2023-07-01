package com.dh.usersservice.configuration.feign;

import feign.RequestInterceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;


public class  OAuthFeignConfig {

  //nombre del client que registro en el properties, podria ser cualquier nombre (spring.security.oauth2.client.registration.MICLIENTEID
  public static final String CLIENT_REGISTRATION_ID = "keycloak";

  private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
  private final ClientRegistrationRepository clientRegistrationRepository;

  public OAuthFeignConfig(OAuth2AuthorizedClientService oAuth2AuthorizedClientService,
                          ClientRegistrationRepository clientRegistrationRepository) {
    this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
    this.clientRegistrationRepository = clientRegistrationRepository;
  }

//realiza una accion sobre la request luego de que se hace una peticion, la intercepta y manipula
  public RequestInterceptor requestInterceptor() {
    ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(CLIENT_REGISTRATION_ID);
    OAuthClientCredentialsFeignManager clientCredentialsFeignManager =
        new OAuthClientCredentialsFeignManager(authorizedClientManager(), clientRegistration);

//le agrega la key Authorization y llamo al metodo que obtiene el token
    return requestTemplate -> {
      requestTemplate.header("Authorization", "Bearer " + clientCredentialsFeignManager.getAccessToken());
    };
  }


  OAuth2AuthorizedClientManager authorizedClientManager() {
    OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
        .clientCredentials()
        .build();

    AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
        new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, oAuth2AuthorizedClientService);
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
    return authorizedClientManager;
  }

}

package com.dh.keycloak;


import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@SpringBootApplication
public class KeycloakApplication implements CommandLineRunner {

	private final Keycloak keycloak;
	@Value("${dh.keycloak.realm}")
	private String realm;
	@Value("${dh.keycloak.gatewayUrl}")
	private String adminUrl;
	@Value("${dh.keycloak.redirectUri}")
	private String redirectUri;
	@Value("${dh.keycloak.company.realm}")
	private String companyRealm;

	@Autowired
	public KeycloakApplication(Keycloak keycloak) {
		this.keycloak = keycloak;
	}

	public static void main(String[] args) {
		SpringApplication.run(KeycloakApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {


		// Creo el reino ecommerce
		RealmRepresentation realmRepresentation = new RealmRepresentation();
		realmRepresentation.setRealm(companyRealm);
		realmRepresentation.setEnabled(true);
		realmRepresentation.setRegistrationAllowed(true);
		keycloak.realms().create(realmRepresentation);

		//Creacion de cliente gateway
		List<String> webOrigins = new ArrayList<>();
		webOrigins.add(adminUrl);
		List<String> redirectUris = new ArrayList<>();
		redirectUris.add(redirectUri);
		ClientRepresentation gatewayClient = new ClientRepresentation();

		gatewayClient.setClientId("api-gateway-client");
		gatewayClient.setName("Cliente para gateway");
		gatewayClient.setServiceAccountsEnabled(true);
		gatewayClient.setStandardFlowEnabled(true);
		gatewayClient.setSecret("nbP8ZuJT0FgPd3xHohpv0QnHnXMg3J8r");
		gatewayClient.setAdminUrl(adminUrl);
		gatewayClient.setRootUrl(adminUrl);
		gatewayClient.setWebOrigins(webOrigins);
		gatewayClient.setRedirectUris(redirectUris);
		keycloak.realm(companyRealm).clients().create(gatewayClient);

		ClientRepresentation gateway = keycloak.realm(companyRealm).clients().findByClientId("api-gateway-client").get(0);

		//Creacion cliente para microservicios backend
		ClientRepresentation msBillsClient = new ClientRepresentation();
		msBillsClient.setClientId("backend");
		msBillsClient.setName("Cliente para microservicios");
		msBillsClient.setServiceAccountsEnabled(true);
		msBillsClient.setStandardFlowEnabled(true);
		msBillsClient.setSecret("9zfKLLyI3wAOkmWuQLU6FHjl7l9lQQqI");

		keycloak.realm(companyRealm).clients().create(msBillsClient);

		ClientRepresentation backend = keycloak.realm(companyRealm).clients().findByClientId("backend").get(0);

		//Creacion de client-scopes para el realm
		ClientScopeRepresentation clientScope = new ClientScopeRepresentation();
		clientScope.setName("user_groups");
		clientScope.setDescription("user_groups");
		clientScope.setProtocol("openid-connect");
		keycloak.realm(companyRealm).clientScopes().create(clientScope);

		//Recupero el scope creado
		ClientScopeRepresentation clientScopeResource = keycloak.realm(companyRealm).clientScopes().findAll().stream().filter(scope -> scope.getName().equals("user_groups")).findFirst().get();

		//creacion de mappers y atributos y asignacion
		List<ProtocolMapperRepresentation> protocolMapperRepresentations = new ArrayList<>();
		ProtocolMapperRepresentation protocolMapper = new ProtocolMapperRepresentation();
		protocolMapper.setProtocolMapper("oidc-group-membership-mapper");
		protocolMapper.setProtocol("openid-connect");
		protocolMapper.setName("user_groups");
		Map<String, String> config = new HashMap<>();
		config.put("access.token.claim", "true");
		config.put("claim.name", "user_groups");
		config.put("full.path", "true");
		config.put("id.token.claim", "true");
		config.put("userinfo.token.claim", "true");
		protocolMapper.setConfig(config);
		protocolMapperRepresentations.add(protocolMapper);

		Map<String, String> attributes = new HashMap<>();
		attributes.put("consent.screen.text", "");
		attributes.put("gui.order", "");
		attributes.put("display.on.consent.screen", "true");
		attributes.put("include.in.token.scope", "true");

		clientScopeResource.setAttributes(attributes);
		clientScopeResource.setProtocolMappers(protocolMapperRepresentations);

		keycloak.realm(companyRealm).clientScopes().get(clientScopeResource.getId()).getProtocolMappers().createMapper(protocolMapper);

		//Agrego el scope al client
		ClientResource clientResource = keycloak.realm(companyRealm).clients().get(gateway.getId());
		clientResource.addDefaultClientScope(clientScopeResource.getId());

		//Creacion de Roles de reino y de clients
		RoleRepresentation role = new RoleRepresentation();
		role.setName("USER");
		role.setComposite(true);

		keycloak.realm(companyRealm).roles().create(role);
		keycloak.realm(companyRealm).clients().get(gateway.getId()).roles().create(role);
		keycloak.realm(companyRealm).clients().get(backend.getId()).roles().create(role);

		//Creacion de usuarios

		UserRepresentation user2 = new UserRepresentation();
		user2.setUsername("testuser");
		user2.setEnabled(true);
		CredentialRepresentation credentialRepresentation2 = new CredentialRepresentation();
		credentialRepresentation2.setType(CredentialRepresentation.PASSWORD);
		credentialRepresentation2.setValue("password");
		credentialRepresentation2.setTemporary(false);
		user2.setCredentials(Collections.singletonList(credentialRepresentation2));
		keycloak.realm(companyRealm).users().create(user2);

		UserRepresentation user3 = new UserRepresentation();
		user3.setUsername("proveedor1");
		user3.setEnabled(true);
		user3.setCredentials(Collections.singletonList(credentialRepresentation2));
		keycloak.realm(companyRealm).users().create(user3);

		UserRepresentation user4 = new UserRepresentation();
		user4.setUsername("proveedor2");
		user4.setEnabled(true);
		user4.setCredentials(Collections.singletonList(credentialRepresentation2));
		keycloak.realm(companyRealm).users().create(user4);

		UserRepresentation user5 = new UserRepresentation();
		user5.setUsername("proveedor3");
		user5.setEnabled(true);
		user5.setCredentials(Collections.singletonList(credentialRepresentation2));
		keycloak.realm(companyRealm).users().create(user5);


		//Creacion grupo PROVIDERS
		GroupRepresentation groupRepresentation = new GroupRepresentation();
		groupRepresentation.setName("PROVIDERS");
		keycloak.realm(companyRealm).groups().add(groupRepresentation);

		//asignacion de grupo a los usuarios proveedores
		UserRepresentation userProvider1 = keycloak.realm(companyRealm).users().search("proveedor1").get(0);
		UserResource userResource = keycloak.realm(companyRealm).users().get(userProvider1.getId());
		UserRepresentation userProvider2 = keycloak.realm(companyRealm).users().search("proveedor2").get(0);
		UserResource userResource1 = keycloak.realm(companyRealm).users().get(userProvider2.getId());
		UserRepresentation userProvider3 = keycloak.realm(companyRealm).users().search("proveedor3").get(0);
		UserResource userResource2 = keycloak.realm(companyRealm).users().get(userProvider3.getId());

		GroupRepresentation groupProvider = keycloak.realm(companyRealm).groups().groups().get(0);
		userResource.joinGroup(groupProvider.getId());
		userResource1.joinGroup(groupProvider.getId());
		userResource2.joinGroup(groupProvider.getId());

		//agrego a testuser el rol USER
		UserRepresentation userUser = keycloak.realm(companyRealm).users().search("testuser").get(0);
		UserResource userResource3 = keycloak.realm(companyRealm).users().get(userUser.getId());

		RoleRepresentation userRole = keycloak.realm(companyRealm).clients().get(gateway.getId()).roles().get("USER").toRepresentation();
		// Asignar el rol al usuario
		userResource3.roles().clientLevel(gateway.getId()).add(List.of(userRole));

		//Configuracion del permiso para ver usuarios de reino
		UserRepresentation serviceAccountBackendUser = keycloak.realm(companyRealm).clients().get(backend.getId()).getServiceAccountUser();
		String clientRealmManagementId = keycloak.realm(companyRealm).clients().findByClientId("realm-management").get(0).getId();
		UserResource userBackendResource = keycloak.realm(companyRealm).users().get(serviceAccountBackendUser.getId());

		RoleRepresentation viewUsersRole = keycloak.realm(companyRealm).clients().get(clientRealmManagementId).roles().get("view-users").toRepresentation();
		RoleScopeResource roleResource = userBackendResource.roles().clientLevel(clientRealmManagementId);

		roleResource.add(Collections.singletonList(viewUsersRole));
		userBackendResource.update(serviceAccountBackendUser);


		System.exit(0);
	}

}

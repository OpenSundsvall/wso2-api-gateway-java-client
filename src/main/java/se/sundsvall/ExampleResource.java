package se.sundsvall;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.FeedbackSettings;
import org.openapitools.client.model.InlineResponse200;
import org.openapitools.client.model.InlineResponse2001;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

/**
 * A few examples to demonstrate requests to API's exposed in Sundsvalls kommuns
 * API Gateway - WSO2.
 * 
 * System properties used in this class: access.token, client.id, client.secret
 * 
 * @author <a href="mailto:dennis.nilsson@sundsvall.se">Dennis Nilsson</a>
 * 
 *
 */

@Path("/")
public class ExampleResource {

	@Inject
	private Logger log;
	private ApiClient apiClient;
	
	private static final String ACCESS_TOKEN = "access.token";
	private static final String THE_ACCESS_TOKEN_EXPIRED = "The access token expired";
	
	private String clientId = System.getProperty("client.id");
	private String clientSecret = System.getProperty("client.secret");

	/**
	 * Example method to demonstrate a GET request.
	 * 
	 */
	@GET
	@Path("feedbacksettings")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSettings(@QueryParam(value = "emailAddress") String emailAddress,
			@QueryParam(value = "mobileNumber") String mobileNumber, @QueryParam(value = "personId") String personId)
			throws ApiException, IOException, InterruptedException, ExecutionException {

		List<InlineResponse200> response = null;

		int count = 0;
		int maxRetries = 1;

		while (true) {
			try {
				response = getDefaultApi().settingsGet(emailAddress, mobileNumber, personId);
				closeClient();
				break;
			} catch (ApiException e) {
				if (count++ == maxRetries) {
					return apiExceptionResponse(e);
				} else {
					if (e.getResponseHeaders().toString().contains(THE_ACCESS_TOKEN_EXPIRED)) {
						renewAccessToken();
					} else {
						return apiExceptionResponse(e);
					}
				}
			}
		}

		return Response.ok(response).build();
	}

	/**
	 * Example method to demonstrate a POST request.
	 * 
	 */
	@POST
	@Path("feedbacksettings")
	@Produces(MediaType.APPLICATION_JSON)
	public Response postSettings(FeedbackSettings feedbackSettings)
			throws ApiException, IOException, InterruptedException, ExecutionException {

		InlineResponse2001 response = null;

		int count = 0;
		int maxRetries = 1;

		while (true) {
			try {
				response = getDefaultApi().settingsPost(feedbackSettings);
				closeClient();
				break;
			} catch (ApiException e) {
				if (count++ == maxRetries) {
					return apiExceptionResponse(e);
				} else {
					if (e.getResponseHeaders().toString().contains(THE_ACCESS_TOKEN_EXPIRED)) {
						renewAccessToken();
					} else {
						return apiExceptionResponse(e);
					}
				}
			}
		}

		return Response.ok(response).build();

	}

	/**
	 * Get new access token and set system property: access.token
	 * 
	 */
	private void renewAccessToken() throws IOException, InterruptedException, ExecutionException {

		final OAuth20Service service = new ServiceBuilder(clientId).apiSecret(clientSecret)
//              .defaultScope("read,write")
				.build(Oauth2Api.instance());

		final OAuth2AccessToken accessToken = service.getAccessTokenClientCredentialsGrant();

		String newAccessToken = accessToken.getAccessToken();

		System.setProperty(ACCESS_TOKEN, newAccessToken);

		log.info("The access token has been updated!");
	}

	/**
	 * @return new DefaultApi configured with ApiClient and access token.
	 * 
	 */
	private DefaultApi getDefaultApi() throws IOException, InterruptedException, ExecutionException {
		// Close the old client if it exists
		closeClient();

		apiClient = new ApiClient();
		apiClient.setAccessToken(getAccessToken());

		return new DefaultApi(apiClient);
	}

	/**
	 * Get access token from system property, if it doesn't exists, get a new one.
	 * 
	 * @return accessToken
	 * 
	 */
	private String getAccessToken() throws IOException, InterruptedException, ExecutionException {
		if (System.getProperty(ACCESS_TOKEN) == null) {
			renewAccessToken();
		}

		return System.getProperty(ACCESS_TOKEN);
	}

	/**
	 * Closes the apiClient.getHttpClient() if it exists.
	 */
	private void closeClient() {
		if (apiClient != null && apiClient.getHttpClient() != null) {
			apiClient.getHttpClient().close();
		}
	}

	/**
	 * @return Response object with ApiException code and response body.
	 */
	private Response apiExceptionResponse(ApiException e) {
		return Response.status(e.getCode()).entity(e.getResponseBody()).build();
	}
}
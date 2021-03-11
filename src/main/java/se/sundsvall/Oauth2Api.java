package se.sundsvall;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth2.bearersignature.BearerSignature;
import com.github.scribejava.core.oauth2.bearersignature.BearerSignatureURIQueryParameter;
import com.github.scribejava.core.oauth2.clientauthentication.ClientAuthentication;
import com.github.scribejava.core.oauth2.clientauthentication.RequestBodyAuthenticationScheme;

public class Oauth2Api extends DefaultApi20 {

	private static final String BASE_URL = "https://api-test.sundsvall.se";

	protected Oauth2Api() {
	}

	private static class InstanceHolder {

		private static final Oauth2Api INSTANCE = new Oauth2Api();
	}

	public static Oauth2Api instance() {
		return InstanceHolder.INSTANCE;
	}

	@Override
	public Verb getAccessTokenVerb() {
		return Verb.POST;
	}

	@Override
	public String getAccessTokenEndpoint() {
		return BASE_URL + "/token";
	}

	@Override
	protected String getAuthorizationBaseUrl() {
		return null;
	}

	@Override
	public BearerSignature getBearerSignature() {
		return BearerSignatureURIQueryParameter.instance();
	}

	@Override
	public ClientAuthentication getClientAuthentication() {
		return RequestBodyAuthenticationScheme.instance();
	}
}

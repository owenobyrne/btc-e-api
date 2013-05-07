package com.owenobyrne.btce;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.owenobyrne.btce.api.model.Depth;
import com.owenobyrne.btce.api.model.BTCEInfo;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@Component
public class BTCE {
	static final Logger logger = Logger.getLogger(BTCE.class.getName());
	private String apiPrivateEndpoint = "https://btc-e.com/tapi";
	private String apiPublicEndpoint = "https://btc-e.com/api/2/";

	private @Value("${btcekey}")
	String apiKey;
	private @Value("${btcesecret}")
	String apiSecret;

	public BTCEInfo getInfo() throws BTCEException {

		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add("method", "getInfo");
		ClientResponse response = doPostPrivateAPI(formData);

		BTCEInfo i = response.getEntity(new GenericType<BTCEInfo>() {
		});

		logger.info("" + i.getReturn().getFunds().get("btc"));
		return i;
	}

	public Depth getMarketDepth(String currencyPair) throws BTCEException {
		ClientResponse response = doGetPublicAPI(currencyPair + "/depth");
		Depth md = response.getEntity(new GenericType<Depth>(){});
        logger.info("" +  md.getBids().get(0).get(1) + " BTC @ " + md.getBids().get(0).get(0) + " EUR");
        return md;
    }

	public ClientResponse doGetPublicAPI(String endpoint) throws BTCEException {

		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put("com.sun.jersey.api.json.POJOMappingFeature", Boolean.TRUE);
		// pain in the arse - BTC-E returns text/html instead of
		// application/json so I have to capture this
		// in a custom reader.
		clientConfig.getClasses().add(HTMLButActuallyJSONMessageReader.class);

		Client client = Client.create(clientConfig);

		WebResource webResource = client.resource(apiPublicEndpoint + endpoint);
		webResource.addFilter(new com.sun.jersey.api.client.filter.LoggingFilter());
		
		ClientResponse response = (ClientResponse) webResource
				.header("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31")
				.accept(MediaType.APPLICATION_JSON_TYPE)
				.get(ClientResponse.class);

		if (response.getStatus() != 200)
			throw new BTCEException(new StringBuilder().append("Failed : HTTP error code : ").append(response.getStatus()).toString());

		return response;

	}

	public ClientResponse doPostPrivateAPI(MultivaluedMap<String, String> formData) throws BTCEException {
		logger.info("Testing " + apiKey);

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		try {

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {

			logger.log(Level.SEVERE, null, e);
		}

		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put("com.sun.jersey.api.json.POJOMappingFeature", Boolean.TRUE);
		// pain in the arse - BTC-E returns text/html instead of
		// application/json so I have to capture this
		// in a custom reader.
		clientConfig.getClasses().add(HTMLButActuallyJSONMessageReader.class);

		Client client = Client.create(clientConfig);

		WebResource webResource = client.resource(apiPrivateEndpoint);
		webResource.addFilter(new com.sun.jersey.api.client.filter.LoggingFilter());

		formData.add("nonce", String.valueOf(System.nanoTime() / 1000000));

		ClientResponse response = (ClientResponse) webResource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.header("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31")
				.header("Key", apiKey).header("Sign", signRequest(formData)).post(ClientResponse.class, formData);

		if (response.getStatus() != 200)
			throw new BTCEException((new StringBuilder()).append("Failed : HTTP error code : ").append(response.getStatus()).toString());

		return response;
	}

	public String signRequest(MultivaluedMap<String, String> formData) {

		SecretKey secretKey = new SecretKeySpec(apiSecret.getBytes(), "HmacSHA512");
		Mac mac;
		try {
			mac = Mac.getInstance("HmacSHA512");
			mac.init(secretKey);

			String result = new String();
			for (String hashkey : formData.keySet()) {
				if (result.length() > 0) {
					result += '&';
				}
				result += URLEncoder.encode(hashkey, "UTF-8") + "=" + URLEncoder.encode(formData.get(hashkey).get(0), "UTF-8");

			}
			// result = path + "\0" + result;
			logger.info("To Sign: " + result);

			mac.update(result.getBytes());
			return Hex.encodeHexString(mac.doFinal()).trim();

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}

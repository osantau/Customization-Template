package oct.soft.util;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class CertificatesUtil {

	public static TrustManager[] trustAllCerts() {
		TrustManager[] trustCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };
		return trustCerts;
	}
}

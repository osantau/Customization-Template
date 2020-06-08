package oct.soft.helper;

import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;

import org.compiere.model.MCurrency;
import org.compiere.model.Query;
import org.compiere.process.SvrProcess;

import oct.soft.process.util.currency.DataSet;
import oct.soft.process.util.currency.LTCube;
import oct.soft.process.util.currency.LTCube.Rate;

public class GeneralHelper extends SvrProcess {

	public static void main(String[] args) throws Exception{
		TrustManager[] trustAllCerts = new TrustManager[]{
			    new X509TrustManager() {
			        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			            return null;
			        }
			        public void checkClientTrusted(
			            java.security.cert.X509Certificate[] certs, String authType) {
			        }
			        public void checkServerTrusted(
			            java.security.cert.X509Certificate[] certs, String authType) {
			        }
			    }
			};
		
		 SSLContext sc = SSLContext.getInstance("SSL");
		 sc.init(null, trustAllCerts, new java.security.SecureRandom());
		 HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		
		JAXBContext jaxbContext = JAXBContext.newInstance(DataSet.class);
		DataSet ds = (DataSet) jaxbContext.createUnmarshaller().unmarshal(new URL("https://www.bnr.ro/nbrfxrates.xml"));
		LTCube cube = ds.getBody().getCube().get(0); 
		for(LTCube.Rate rate : cube.getRate()) {
			System.out.println(rate.getCurrency());
		}
	}
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String doIt() throws Exception {		
		
		return "Ok";
	}

}

package oct.soft.process.util.currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.Timestamp;
import java.util.GregorianCalendar;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.xml.bind.JAXBContext;

import org.compiere.model.MClient;
import org.compiere.model.MConversionRate;
import org.compiere.model.MConversionType;
import org.compiere.model.MCurrency;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;

import oct.soft.util.CertificatesUtil;

public class DownloadCurrencyRateRON extends SvrProcess {
String ISO_CODE = null;
String BNR_URL = null;
MCurrency baseCurrency = null;
	@Override
	protected void prepare() {
		for (ProcessInfoParameter para : getParameter())
		{
			String name = para.getParameterName();
			if (para.getParameter() == null)
				;
			else if (name.equals("ISO_Code"))
			{
				ISO_CODE = para.getParameterAsString();
			}
			
			else if(name.equals("URL"))
			{
				BNR_URL = para.getParameterAsString();
			}			
		}
		
		baseCurrency = MCurrency.get(getCtx(), MClient.get(getCtx()).getC_Currency_ID());
	}

	@Override
	protected String doIt() throws Exception {
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, CertificatesUtil.trustAllCerts(), new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		JAXBContext jaxbContext = JAXBContext.newInstance(DataSet.class);
		
		DataSet ds = (DataSet) jaxbContext.createUnmarshaller().unmarshal(new URL(BNR_URL));
		LTCube cube = ds.getBody().getCube().get(0); 
		GregorianCalendar gc = cube.getDate().toGregorianCalendar();
		Timestamp validDate = new Timestamp(gc.getTimeInMillis());
		
	System.out.println(baseCurrency);
		for(LTCube.Rate rate : cube.getRate()) {
			MCurrency cry = MCurrency.get(getCtx(), rate.getCurrency());
			if(cry !=null) 
			{
				MConversionRate mcr = new MConversionRate(getCtx(), 0, get_TrxName());
				mcr.setC_Currency_ID(cry.getC_Currency_ID());
				mcr.setC_Currency_ID_To(baseCurrency.getC_Currency_ID());
				mcr.setValidFrom(validDate);
				mcr.setValidTo(validDate);
				mcr.setC_ConversionType_ID(MConversionType.TYPE_SPOT);
				if(rate.getMultiplier()!=null)
				{
					BigDecimal divisor = BigDecimal.valueOf(rate.getMultiplier().longValueExact());
					BigDecimal br = rate.getValue().divide(divisor, RoundingMode.HALF_UP);
					mcr.setMultiplyRate(br);
				} else {
				mcr.setMultiplyRate(rate.getValue());
				}
				mcr.saveEx();
			}
			
		} 
		return "OK";
	}

}

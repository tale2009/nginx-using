package com.nginx.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.core.io.ClassPathResource;
import sun.security.x509.CertificateValidity;
import sun.security.x509.X500Name;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;

public class CertUtils {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 生成私钥
     * @param pemfilesourceurl
     * @return
     */
    public static PrivateKey generatePrivate(String pemfilesourceurl)
    {
        try
        {
            //使用PemReader类读取
            PemReader pemReader = new PemReader(new InputStreamReader(new ClassPathResource(pemfilesourceurl).getInputStream()));
            //获取encoded的pem信息
            PemObject pemObject = pemReader.readPemObject();
            byte[] pemcontent = pemObject.getContent();
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            //当证书提供方提供的是一个pem文件时候如何通过java生成对应的PrivateKey类
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(pemcontent));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 创建证书基本信息
     * @param CN comonName
     * @param OU organizationalUnitName
     * @param O organizationName
     * @param C countryName
     * @param L localityName
     * @param ST stateOrProvinceName
     * @return
     * @throws IOException
     */
    public static X500Name createSubject(String CN,String OU,String O,String C,String L,String ST) throws IOException {
        X500Name subject = new X500Name(new StringBuilder()
                .append("CN=").append(CN)
                .append(",OU=").append(OU)
                .append(",O=").append(O)
                .append(",C=").append(C)
                .append(",L=").append(L)
                .append(",ST=").append("abc").toString());
        return subject;
    }

    public static CertificateValidity createValidity(Integer startYear, Integer blank) throws Exception {
        if(blank<1)
            throw new Exception("间隔不能少于1");
        Calendar calendar = Calendar.getInstance();
        calendar.set(startYear,0,1,0,0);
        Date firstDate = calendar.getTime();
        calendar.set(startYear+blank,0,1,0,0);
        Date lastDate = calendar.getTime();
        return new CertificateValidity(firstDate, lastDate);
    }

    public static X509Certificate createX509Certificate(String fileurl) throws IOException, CertificateException {
        //获取证书类
        InputStream is = new ClassPathResource(fileurl).getInputStream();
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate issueCertificate = (X509Certificate) cf.generateCertificate(is);
        is.close();
        return issueCertificate;
    }

}

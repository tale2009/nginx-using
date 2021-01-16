package com.nginx;


import com.nginx.util.CertUtils;
import sun.security.x509.*;

import java.io.File;
import java.io.FileOutputStream;
import java.security.*;
import java.security.cert.X509Certificate;

public class CAdemoApplication {

    public static void main(String args[])
    {
        String pemurl = "test.pem";
        String crt="test.crt";
        String finalStorePath = "/Users/kirra/Desktop/project/nginx/CA-Demo";
        File finalPath = new File(finalStorePath);
        if (!finalPath.exists()) finalPath.mkdirs();
        String userPFXfilePath = finalStorePath + File.separator  + "user.p12";
        try
        {
            //privateKey类
            PrivateKey privateKey = CertUtils.generatePrivate(pemurl);
            //证书类
            X509Certificate x509Certificate = CertUtils.createX509Certificate(crt);
            //私钥/公钥生成类
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048, new SecureRandom());
            KeyPair subjectKeyPair = kpg.generateKeyPair();
            //有效时间类
            CertificateValidity validity = CertUtils.createValidity(2020, 2);
            //创建证书主题信息
            X500Name subject = CertUtils.createSubject("test",
                    "personal",
                    "personal",
                    "CHINA",
                    "HONGKONG",
                    "HONGKONG");
            X509CertInfo info = new X509CertInfo();
            info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
            info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(new java.util.Random().nextInt() & 0x7fffffff));
            info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(AlgorithmId.get("SHA256withRSA")));
            info.set(X509CertInfo.SUBJECT, subject);
            info.set(X509CertInfo.KEY, new CertificateX509Key(subjectKeyPair.getPublic()));
            info.set(X509CertInfo.VALIDITY, validity);
            //签发的信息要和CA证书一致才能通过认证
            info.set(X509CertInfo.ISSUER, x509Certificate.getIssuerDN());
            //使用根证书签名使用户证书有证书效果
            X509CertImpl cert = new X509CertImpl(info);
            cert.sign(privateKey, "SHA256withRSA");

            X509Certificate certificate = (X509Certificate) cert;
            X509Certificate[] X509Certificates = new X509Certificate[]{certificate};
            // 生成用户安装证书并输出到指定路径
            KeyStore keyStore = KeyStore.getInstance("pkcs12");
            keyStore.load(null, null);
            keyStore.setKeyEntry("test",
                    subjectKeyPair.getPrivate(),
                    "123456".toCharArray(),
                    X509Certificates);
            File file = new File(userPFXfilePath);
            if (file.exists()) file.delete();
            FileOutputStream fos = new FileOutputStream(userPFXfilePath);
            keyStore.store(fos, "123456".toCharArray());
            fos.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}

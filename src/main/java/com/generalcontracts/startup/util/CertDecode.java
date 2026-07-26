package com.generalcontracts.startup.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.util.Enumeration;
public class CertDecode {
    public static void decodeCertificate(InputStream is) {
        try {
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            String password = "";
            keystore.load(is, password.toCharArray());
            Enumeration<String> enumeration = keystore.aliases();
            while(enumeration.hasMoreElements()) {
                String alias = enumeration.nextElement();
                System.out.println("alias name: " + alias);
                Certificate certificate = keystore.getCertificate(alias);
                System.out.println(certificate.toString());
            }
        } catch (java.security.cert.CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            if(null != is)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }}
}
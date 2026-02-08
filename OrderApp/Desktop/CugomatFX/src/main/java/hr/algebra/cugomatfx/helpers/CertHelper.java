package hr.algebra.cugomatfx.helpers;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.security.KeyStore;
import java.security.SecureRandom;

public class CertHelper {
    private static final String KEYSTORE_PATH = AppConfig.get("cert.keystore.path");
    private static final String KEYSTORE_PASSWORD = AppConfig.get("cert.keystore.password");
    private static final String KEYSTORE_TYPE = AppConfig.get("cert.keystore.type");
    private static final String TRUSTSTORE_PATH = AppConfig.get("cert.truststore.path");
    private static final String TRUSTSTORE_PASSWORD = AppConfig.get("cert.truststore.password");
    private static final String TRUSTSTORE_TYPE = AppConfig.get("cert.truststore.type");
    private static final String SSL_PROTOCOL = AppConfig.get("connection.protocol");

    private final HttpClient client;

    public CertHelper() throws Exception {
        if (!isCertificateAvailable()) {
            throw new RuntimeException("Keystore/Truststore missing");
        }
        this.client = createSSLClient();
    }

    public static boolean isCertificateAvailable() {
        try (InputStream keyStoreStream = CertHelper.class.getResourceAsStream(KEYSTORE_PATH);
             InputStream trustStoreStream = CertHelper.class.getResourceAsStream(TRUSTSTORE_PATH)) {
            return keyStoreStream != null && trustStoreStream != null;
        } catch (Exception e) {
            return false;
        }
    }

    private HttpClient createSSLClient() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        try (InputStream keyStoreStream = getResourceStream(KEYSTORE_PATH)) {
            keyStore.load(keyStoreStream, KEYSTORE_PASSWORD.toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, KEYSTORE_PASSWORD.toCharArray());

        KeyStore trustStore = KeyStore.getInstance(TRUSTSTORE_TYPE);
        try (InputStream trustStream = getResourceStream(TRUSTSTORE_PATH)) {
            trustStore.load(trustStream, TRUSTSTORE_PASSWORD.toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance(SSL_PROTOCOL);
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

        return HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();
    }

    private InputStream getResourceStream(String path) throws Exception {
        InputStream stream = CertHelper.class.getResourceAsStream(path);
        if (stream == null) {
            throw new RuntimeException("Resource not found: " + path);
        }
        return stream;
    }

    public HttpClient getClient() {
        return client;
    }
}
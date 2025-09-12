package nl.radiantrealm.myshop;

import nl.radiantrealm.library.http.client.CertificateLevel;
import nl.radiantrealm.library.http.client.Client;
import nl.radiantrealm.myshop.cache.ShopCategoryCache;
import nl.radiantrealm.myshop.cache.ShopMaterialCache;
import nl.radiantrealm.myshop.cache.ShopProductCache;

public class Main {
    public static final Client client;

    public static final ShopCategoryCache shopCategoryCache = new ShopCategoryCache();
    public static final ShopMaterialCache shopMaterialCache = new ShopMaterialCache();
    public static final ShopProductCache shopProductCache = new ShopProductCache();

    static {
        try {
            client = new Client(CertificateLevel.INTERNAL_CA) {};
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
    }
}

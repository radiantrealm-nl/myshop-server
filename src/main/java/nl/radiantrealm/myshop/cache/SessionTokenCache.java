package nl.radiantrealm.myshop.cache;

import com.google.gson.JsonObject;
import nl.radiantrealm.library.cache.CacheRegistry;
import nl.radiantrealm.library.http.HttpMethod;
import nl.radiantrealm.library.http.client.Client;
import nl.radiantrealm.library.utils.JsonUtils;
import nl.radiantrealm.myshop.Main;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

public class SessionTokenCache extends CacheRegistry<UUID, UUID> {
    private final Client client = Main.client;

    public SessionTokenCache() {
        super(Duration.ofMinutes(30));
    }

    @Override
    protected UUID load(UUID uuid) throws Exception {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("session_uuid", uuid.toString());

        HttpResponse<String> response = client.sendRequest("replaceit", HttpMethod.GET, requestBody);

        if (response.statusCode() != 200) {
            return null;
        }

        JsonObject responseBody = JsonUtils.getJsonObject(response.body());
        return JsonUtils.getJsonUUID(responseBody, "player_uuid");
    }
}

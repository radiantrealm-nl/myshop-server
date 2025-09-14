package nl.radiantrealm.myshop.server.external;

import com.google.gson.JsonObject;
import nl.radiantrealm.library.http.StatusCode;
import nl.radiantrealm.library.http.server.ApplicationRouter;
import nl.radiantrealm.library.utils.FormatUtils;
import nl.radiantrealm.library.utils.Result;
import nl.radiantrealm.myshop.Main;
import nl.radiantrealm.myshop.cache.SessionTokenCache;

import java.net.HttpCookie;
import java.util.UUID;

public class MinecraftWebserverAPI extends ApplicationRouter {
    private final SessionTokenCache sessionTokenCache = Main.sessionTokenCache;

    public MinecraftWebserverAPI() {
        super(69420);
    }

    private void registerPublic(String path, PublicRequestHandler handler) {
        register(path, request -> {
            HttpCookie cookie = request.getCookie("csrf");

            if (cookie == null) {
                request.sendStatusResponse(StatusCode.UNAUTHORIZED);
                return;
            }

            UUID sessionUUID = Result.nullFunction(() -> FormatUtils.formatUUID(cookie.getValue()));

            if (sessionUUID == null) {
                request.sendStatusResponse(StatusCode.UNAUTHORIZED);
                return;
            }

            UUID playerUUID = sessionTokenCache.get(sessionUUID);

            if (playerUUID == null) {
                request.sendStatusResponse(StatusCode.UNAUTHORIZED);
                return;
            }

            JsonObject object = Result.nullFunction(request::getRequestBodyAsJson);

            if (object == null) {
                request.sendStatusResponse(StatusCode.BAD_REQUEST, "Missing JSON body.");
                return;
            }

            handler.handle(request, playerUUID, object);
        });
    }
}

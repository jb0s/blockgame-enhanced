package dev.jb0s.blockgameenhanced.manager.update;

import com.google.gson.Gson;
import dev.jb0s.blockgameenhanced.BlockgameEnhanced;
import dev.jb0s.blockgameenhanced.manager.Manager;
import net.fabricmc.loader.api.FabricLoader;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateManager extends Manager {
    private static final String USER_AGENT = "BlockgameEnhancedClient";
    private static final String LATEST_RELEASE_ENDPOINT_URL = "https://api.github.com/repos/jb0s/blockgame-enhanced/releases/latest";

    /**
     * Determines whether an update is available using GitHub API.
     * @return
     */
    public GitHubRelease checkForUpdates() {
        GitHubRelease latestVersion = getLatestRelease();
        String modVersion = "v" + FabricLoader.getInstance().getModContainer("blockgameenhanced").get().getMetadata().getVersion().getFriendlyString();

        // If latest version and current version do not match, return latest version.
        if(!latestVersion.tag_name.equals(modVersion)) {
            return latestVersion;
        }

        // Latest version and current version match, return null.
        return null;
    }

    /**
     * Sends an API request to GitHub to get the latest release of the mod.
     * @return Object containing data about the latest release, null if an error occurred fetching the data.
     */
    public GitHubRelease getLatestRelease() {
        try {
            URL url = new URL(LATEST_RELEASE_ENDPOINT_URL);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            // Send request
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            // Handle response
            int responseCode = con.getResponseCode();
            if(responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                // We got data, parse and return it
                return new Gson().fromJson(response.toString(), GitHubRelease.class);
            }

            // GitHub did not return 200 OK, something went wrong.
            return null;
        }
        catch (Exception e) {
            BlockgameEnhanced.LOGGER.warn("Failed to fetch latest release data from GitHub: " + e.getMessage());
            return null;
        }
    }
}

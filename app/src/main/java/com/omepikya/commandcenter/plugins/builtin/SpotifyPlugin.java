package com.omepikya.commandcenter.plugins.builtin;

import android.content.Context;
import android.net.Uri;

import com.omepikya.commandcenter.core.CommandContext;

public final class SpotifyPlugin
        extends UriPlugin {

    public SpotifyPlugin(
            Context c
    ) {

        super(c);
    }

    @Override
    public String getId() {
        return "spotify";
    }

    @Override
    public String getName() {
        return "Spotify";
    }

    @Override
    protected String[] keywords() {

        return new String[]{
                "spotify",
                "spotify search",
                "spotify music"
        };
    }

    @Override
    protected String uri() {

        return "https://open.spotify.com";
    }

    @Override
    protected String uriForCommand(
            CommandContext c
    ) {

        String query =
                extractQuery(c);

        if (query.isEmpty()) {
            return uri();
        }

        return "https://open.spotify.com/search/"
                + Uri.encode(query);
    }
}
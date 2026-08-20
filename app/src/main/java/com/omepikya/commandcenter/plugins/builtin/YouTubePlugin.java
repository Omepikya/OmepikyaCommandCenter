package com.omepikya.commandcenter.plugins.builtin;

import android.content.Context;
import android.net.Uri;

import com.omepikya.commandcenter.core.CommandContext;

public final class YouTubePlugin
        extends UriPlugin {

    public YouTubePlugin(
            Context c
    ) {

        super(c);
    }

    @Override
    public String getId() {
        return "youtube";
    }

    @Override
    public String getName() {
        return "YouTube";
    }

    @Override
    protected String[] keywords() {

        return new String[]{
                "youtube",
                "youtube search",
                "youtube video"
        };
    }

    @Override
    protected String uri() {

        return "https://www.youtube.com";
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

        return "https://www.youtube.com/results?search_query="
                + Uri.encode(query);
    }
}
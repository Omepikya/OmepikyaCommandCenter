package com.omepikya.commandcenter.plugins.builtin;

import android.content.Context;
import android.net.Uri;

import com.omepikya.commandcenter.core.CommandContext;

public final class BrowserPlugin
        extends UriPlugin {

    public BrowserPlugin(
            Context c
    ) {

        super(c);
    }

    @Override
    public String getId() {
        return "browser";
    }

    @Override
    public String getName() {
        return "Browser";
    }

    @Override
    protected String[] keywords() {

        return new String[]{
                "browser",
                "open browser",
                "search google",
                "search the web",
                "search for"
        };
    }

    @Override
    protected String uri() {

        return "https://www.google.com";
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

        return "https://www.google.com/search?q="
                + Uri.encode(query);
    }
}
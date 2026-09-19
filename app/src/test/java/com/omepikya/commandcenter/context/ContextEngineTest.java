package com.omepikya.commandcenter.context;

import android.content.Context;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ContextEngineTest {

    @Test
    public void storesAndReturnsCommandContextValues() {
        Context context =
                org.mockito.Mockito.mock(Context.class);
        org.mockito.Mockito.when(
                context.getApplicationContext())
                .thenReturn(context);

        ContextEngine engine =
                new ContextEngine(context);

        engine.setLastCommand("open calculator");
        engine.setLastIntent("OPEN_APP");
        engine.setLastEntity("calculator");
        engine.setLastAction("open_app");
        engine.setLastResult(true, "opened");

        assertEquals(
                "open calculator",
                engine.getLastCommand());
        assertEquals(
                "OPEN_APP",
                engine.getLastIntent());
        assertEquals(
                "calculator",
                engine.getLastEntity());
        assertEquals(
                "open_app",
                engine.getLastAction());
        assertTrue(engine.isFresh());
        assertFalse(engine.hasRecentFailure(10000));
    }

    @Test
    public void clearsExpiredContext() throws Exception {
        Context context =
                org.mockito.Mockito.mock(Context.class);
        org.mockito.Mockito.when(
                context.getApplicationContext())
                .thenReturn(context);

        ContextEngine engine =
                new ContextEngine(context);
        engine.setTimeout(1);
        engine.setLastCommand("temporary");

        Thread.sleep(5);

        assertTrue(engine.isExpired());
        assertEquals(null, engine.getLastCommand());
        assertTrue(engine.snapshot().isEmpty());
    }
}

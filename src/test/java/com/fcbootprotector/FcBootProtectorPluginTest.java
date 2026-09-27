package com.fcbootprotector;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class FcBootProtectorPluginTest
{
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(FcBootProtectorPlugin.class);
        RuneLite.main(args);
    }
}
package com.fcbootprotector;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.KeyCode;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuOpened;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
    name = "FC Boot Protector",
    description = "Stops you from accidentally kicking players from Friends Chat or Clan Channels.",
    tags = {"clan", "chat", "friends", "kick", "protector", "fc"}
)
public class FcBootProtectorPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private FcBootProtectorConfig config;

    @Provides
    FcBootProtectorConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(FcBootProtectorConfig.class);
    }

    @Subscribe
    public void onMenuOpened(MenuOpened event)
    {
        if (!config.protectFriendsChat())
        {
            return;
        }

        // Bypass protection if Shift is held and enabled in config
        if (config.requireShift() && client.isKeyPressed(KeyCode.KC_SHIFT))
        {
            return;
        }

        // Check if player is currently in a Friends Chat or a Clan Channel
        boolean inFriendsChat = client.getFriendsChatManager() != null;
        boolean inClanChannel = client.getClanChannel() != null;

        if (!inFriendsChat && !inClanChannel)
        {
            return;
        }

        MenuEntry[] entries = event.getMenuEntries();
        List<MenuEntry> filteredEntries = new ArrayList<>(entries.length);
        boolean modified = false;

        for (MenuEntry entry : entries)
        {
            if (isKickOption(entry.getOption()))
            {
                modified = true;
                continue; // Omit kick entries
            }
            filteredEntries.add(entry);
        }

        if (modified)
        {
            client.getMenu().setMenuEntries(filteredEntries.toArray(new MenuEntry[0]));
        }
    }

    private boolean isKickOption(String option)
    {
        if (option == null || option.isEmpty())
        {
            return false;
        }

        String cleanOption = Text.removeTags(option).trim().toLowerCase();
        return cleanOption.equals("kick") || cleanOption.startsWith("kick ");
    }
}
package com.mist.ethoscoloredsigns;

import java.util.Locale;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.mist.ethoscoloredsigns.commands.CommandEditSign;
import com.mist.ethoscoloredsigns.manager.ConfigurationManager;
import com.mist.ethoscoloredsigns.utility.VersionUtility;
import com.mist.ethoscoloredsigns.utility.ModernUtility;
import com.mist.ethoscoloredsigns.utility.LegacyUtility;

public final class EthosColoredSigns extends JavaPlugin {
        private final ConfigurationManager configurationManager;

        public EthosColoredSigns() {
            this.configurationManager = new ConfigurationManager(this);
        }

        @Override
        public void onLoad() {
            ConfigurationManager configurationManager = getConfigurationManager();
            configurationManager.saveDefault("config.yml");
            configurationManager.saveDefault("language.yml");
        }

        @Override
        public void onEnable() {
            registerCommand();
            residenceCheck();
            //registerListeners();
            broadcastEnableMessage();
        }

        @Override
        public void onDisable() {
            broadcastDisableMessage();
        }

        @Override
        public void saveDefaultConfig() {
            ConfigurationManager configurationManager = getConfigurationManager();
            configurationManager.saveDefault("config.yml");
        }

        @Override
        public void reloadConfig() {
            ConfigurationManager configurationManager = getConfigurationManager();
            configurationManager.reload("config.yml");
        }

        @Override
        public YamlConfiguration getConfig() {
            ConfigurationManager configurationManager = getConfigurationManager();
            return configurationManager.get("config.yml");
        }

        @Override
        public void saveConfig() {
            ConfigurationManager configurationManager = getConfigurationManager();
            configurationManager.save("config.yml");
        }

        public ConfigurationManager getConfigurationManager() {
            return this.configurationManager;
        }

        public void sendDebugMessage(String message) {
            ConfigurationManager configurationManager = getConfigurationManager();
            YamlConfiguration configuration = configurationManager.get("config.yml");
            if(!configuration.getBoolean("debug-mode", false)) return;

            Logger logger = getLogger();
            String logMessage = String.format(Locale.US,"[Debug] %s", message);
            logger.info(logMessage);
        }

        public String defaultFullColor(String message) {
            int minorVersion = VersionUtility.getMinorVersion();
            if(minorVersion > 16) {
                message = ModernUtility.replaceHexColors('&', message);
            }

            return LegacyUtility.replaceAll('&', message);
        }

        private void registerCommand() {
            PluginCommand pluginCommand = getCommand("edit-sign");
            TabExecutor commandExecutor = new CommandEditSign(this);
            pluginCommand.setExecutor(commandExecutor);
            pluginCommand.setTabCompleter(commandExecutor);
        }

        /*private void registerListeners() {
            new ListenerLegacyColors(this).register();

            int minorVersion = VersionUtility.getMinorVersion();
            if(minorVersion >= 16) {
                new ListenerHexColors(this).register();
            }
        }*/

        private void broadcastEnableMessage() {
            ConfigurationManager configurationManager = getConfigurationManager();
            YamlConfiguration configuration = configurationManager.get("config.yml");
            if(!configuration.getBoolean("broadcast-enabled", true)) return;

            YamlConfiguration language = configurationManager.get("language.yml");
            String message = language.getString("broadcast-enabled");
            if(message == null || message.isEmpty()) return;

            String messageColored = defaultFullColor(message);
            Bukkit.broadcastMessage(messageColored);
        }

        private void broadcastDisableMessage() {
            ConfigurationManager configurationManager = getConfigurationManager();
            YamlConfiguration configuration = configurationManager.get("config.yml");
            if(!configuration.getBoolean("broadcast-disabled", true)) return;

            YamlConfiguration language = configurationManager.get("language.yml");
            String message = language.getString("broadcast-disabled");
            if(message == null || message.isEmpty()) return;

            String messageColored = defaultFullColor(message);
            Bukkit.broadcastMessage(messageColored);
        }

        private void residenceCheck() {
            PluginManager pm = getServer().getPluginManager();
            Plugin p = pm.getPlugin("Residence");
            if(p!=null)
            {
                if(!p.isEnabled())
                {
                    System.out.println(" - Manually Enabling Residence!");
                    pm.enablePlugin(p);
                }
            }
            else
            {
                System.out.println(" - Residence NOT Installed, DISABLED!");
                this.setEnabled(false);
            }
        }
    }

package yetanotherx.bukkitplugin.Minestalker;

//Bukkit imports
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

//Java imports
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

//Permissions imports
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/*
 * Minestalker Version 1.0 - Check last time user was online
 * Copyright (C) 2011 Yetanotherx <yetanotherx -a--t- gmail -dot- com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @author yetanotherx
 * 
 */
public class Minestalker extends JavaPlugin {
	
	/**
	 * Hooks the listener
	 */
	private final MinestalkerPlayerListener playerListener = new MinestalkerPlayerListener();

	/**
	 * Configuration classes
	 */
	public static Configuration seenconfig = null;
	public static Configuration optoutconfig = null;
	
	/**
	 * Logger magic
	 */
	public static final Logger log = Logger.getLogger("Minecraft");
	
	/**
	 * Permission plugin
	 */
	public static PermissionHandler Permissions = null;

	/**
	 * Outputs a message when disabled
	 */
	public void onDisable() {
		log.info(this.getDescription().getName() + " version " + this.getDescription().getVersion() + "disabled.");
	}
	
	/**
	 * 
	 * Checks that Permissions is installed.
	 * 
	 */
	public void setupPermissions() {
		
		Plugin perm_plugin = this.getServer().getPluginManager().getPlugin("Permissions");
		PluginDescriptionFile pdfFile = this.getDescription();
		
		if( Minestalker.Permissions == null ) {
			if( perm_plugin != null ) {
				//Permissions found, enable it now
				this.getServer().getPluginManager().enablePlugin( perm_plugin );
				Minestalker.Permissions = ( (Permissions) perm_plugin ).getHandler();
			}
			else {
				//Permissions not found. Disable plugin
				log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + "not enabled. Permissions not detected");
				this.getServer().getPluginManager().disablePlugin(this);
			}
		}
	}
	
	/**
	 * 
	 * Setup Permissions plugin & config files
	 * Hook the events into the plugin manager
	 * 
	 */
	public void onEnable() {
		
		setupPermissions();
		setupConfig();
		
		PluginManager pm = getServer().getPluginManager();
		
		//Event updates the database file on quit
		pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Monitor, this);
		
		//Print that the plugin has been enabled!
		log.info( this.getDescription().getName() + " version " + this.getDescription().getVersion() + " is enabled!" );		
	}

	/**
	 * 
	 * Loads the two config files
	 * 
	 */
	private void setupConfig() {
		
		//Make the Minestalker directory if it doesn't exist
		new File("plugins" + File.separator + "Minestalker" + File.separator).mkdirs();

		
		//Loads the main DB file
		seenconfig  = new Configuration(new File("plugins" + File.separator + "Minestalker", "players.yml"));
		seenconfig.load();
		if( !(new File("plugins" + File.separator + "Minestalker", "players.yml").exists())) {
			seenconfig.setProperty("1234567890", (long) 0 );
			seenconfig.save();
			seenconfig.load();
		}
		
		//Loads the opt-out configuration
		optoutconfig  = new Configuration(new File("plugins" + File.separator + "Minestalker", "optout.yml"));
		optoutconfig.load();
		if( !(new File("plugins" + File.separator + "Minestalker", "players.yml").exists())) {
			optoutconfig.setProperty("1234567890", false );
			optoutconfig.save();
			optoutconfig.load();
		}
		
	}

	/**
	 * Called when a user performs a command
	 */
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
			
		String[] split = args;
		String commandName = command.getName().toLowerCase();
		
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (commandName.equals("stalk")) {
				
				if (split.length == 1 && split[0].equalsIgnoreCase("help")) {
					
					player.sendMessage(ChatColor.RED + "/stalk [username]" + ChatColor.WHITE + "  -  Get info about a user's last login");
					player.sendMessage(ChatColor.RED + "/stalk -optout" + ChatColor.WHITE + "  -  Prevent users from checking your login time");
					player.sendMessage(ChatColor.RED + "/stalk -optin" + ChatColor.WHITE + "  -  Allow users to check your login time (after opting out)");
				} 
				else if (split.length == 1 && split[0].equalsIgnoreCase("-optin") && Permissions.has(player, "minestalker.optin")) {
					
					optoutconfig.load();
					optoutconfig.setProperty(player.getName(), false );
					optoutconfig.save();
					optoutconfig.load();
					player.sendMessage( ChatColor.WHITE + "You have opted in to login stalking");
					
				}
				else if (split.length == 1 && split[0].equalsIgnoreCase("-optout") && Permissions.has(player, "minestalker.optout")) {
					
					optoutconfig.load();
					optoutconfig.setProperty(player.getName(), true );
					optoutconfig.save();
					optoutconfig.load();
					player.sendMessage( ChatColor.WHITE + "You have opted out of login stalking");
					
				}
				else if (split.length == 1 && Permissions.has(player, "minestalker.use") ) {
					
					Player target = this.getServer().getPlayer( split[0] );
					
					if( target != null && target.isOnline() ) {
						player.sendMessage( ChatColor.WHITE + split[0] + " is online right now!");
					}
					else if( optoutconfig.getBoolean(split[0], false ) ) {
						player.sendMessage( ChatColor.WHITE + split[0] + " has chosen not to be checked!");
					}
					else {
						
						Minestalker.seenconfig.load();
						Object timestamp = Minestalker.seenconfig.getProperty( split[0] );
					
						if( timestamp == null ) {
							player.sendMessage( ChatColor.WHITE + split[0] + " has never been on this server.");
							return true;
						}
						
						DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
						String date = dateFormatter.format(timestamp);
						DateFormat timeFormatter = new SimpleDateFormat("hh:mm:ss a");
						String time = timeFormatter.format(timestamp);
						
						player.sendMessage( ChatColor.WHITE + split[0] + " was last seen on " + date + " at " + time + ".");
					}
					
				 
				} 
				else {
					return false;
				}
				
				return true;
			}
		}
		return false;
	}
}

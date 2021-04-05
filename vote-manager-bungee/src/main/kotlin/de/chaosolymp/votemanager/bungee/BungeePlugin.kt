package de.chaosolymp.votemanager.bungee

import de.chaosolymp.votemanager.bungee.config.DatabaseConfiguration
import de.chaosolymp.votemanager.bungee.config.MessageConfiguration
import de.chaosolymp.votemanager.bungee.listener.VoteListener
import de.chaosolymp.votemanager.bungee.command.VoteCommand
import de.chaosolymp.votemanager.bungee.command.VotesCommand
import de.chaosolymp.votemanager.bungee.command.TopVotersCommand
import de.chaosolymp.votemanager.bungee.dispatcher.AchievementDispatcher
import de.chaosolymp.votemanager.bungee.dispatcher.TNEDispatcher
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import java.io.File
import kotlin.properties.Delegates

class BungeePlugin : Plugin() {

    lateinit var tneDispatcher: TNEDispatcher
    lateinit var databaseConfig: DatabaseConfiguration
    lateinit var achievementDatabaseConfig: DatabaseConfiguration
    lateinit var databaseManager: DatabaseManager
    lateinit var messageConfiguration: MessageConfiguration
    lateinit var achievementDispatcher: AchievementDispatcher
    var money by Delegates.notNull<Double>()

    override fun onEnable() {
        val startTime = System.currentTimeMillis()
        this.logger.info("Plugin warmup started")
        this.tneDispatcher = TNEDispatcher(this)
        this.initGeneralConfig()
        this.initMessageConfig()
        this.initDatabaseConfig()
        this.initAchievementDatabaseConfig()
        this.databaseManager = DatabaseManager(this)
        this.databaseManager.createTable()
        this.achievementDispatcher =
            AchievementDispatcher(this)
        this.proxy.pluginManager.registerListener(this, VoteListener(this))
        this.proxy.pluginManager.registerCommand(this, VoteCommand(this))
        this.proxy.pluginManager.registerCommand(this, VotesCommand(this))
        this.proxy.pluginManager.registerCommand(this, TopVotersCommand(this))
        this.logger.info("Plugin warmup finished (Took ${System.currentTimeMillis() - startTime}ms)!")
    }

    private fun initMessageConfig() {
        val config = File(this.dataFolder, "messages.yml")
        val clazz =  YamlConfiguration::class.java
        val provider = ConfigurationProvider.getProvider(clazz)
        if(!this.dataFolder.exists()) {
            this.dataFolder.mkdir()
            this.logger.info("Created plugin data folder ${dataFolder.name}")
        }
        if(!config.exists()) {
            if(config.createNewFile()) {
                val defaultConfig = MessageConfiguration.getDefaultConfiguration()
                provider.save(defaultConfig, config)
                this.messageConfiguration = MessageConfiguration(defaultConfig)
                this.logger.info("Created default configuration file ${config.name}")
            }
        } else {
            this.messageConfiguration = MessageConfiguration(provider.load(config))
            this.logger.info("Loaded configuration file ${config.name}")
        }
    }

    private fun initGeneralConfig() {
        val config = File(this.dataFolder, "config.yml")
        val clazz =  YamlConfiguration::class.java
        val provider = ConfigurationProvider.getProvider(clazz)
        if(!this.dataFolder.exists()) {
            this.dataFolder.mkdir()
            this.logger.info("Created plugin data folder ${dataFolder.name}")
        }
        if(!config.exists()) {
            if(config.createNewFile()) {
                val defaultConfig = Configuration()
                val defaultMoney = 50.0
                defaultConfig.set("money", defaultMoney)
                provider.save(defaultConfig, config)
                this.money = defaultMoney
                this.logger.info("Created default configuration file ${config.name}")
           }
        } else {
            val yamlConfig = provider.load(config)
            if(yamlConfig.contains("money")) {
                this.money = yamlConfig.getDouble("money")
            } else {
                this.logger.severe("Error whilst loading configuration file")
            }
            this.logger.info("Loaded configuration file ${config.name}")
        }
    }

    private fun initDatabaseConfig() {
        val config = File(this.dataFolder, "database.yml")
        val clazz =  YamlConfiguration::class.java
        val provider = ConfigurationProvider.getProvider(clazz)
        if(!this.dataFolder.exists()) {
            this.dataFolder.mkdir()
            this.logger.info("Created plugin data folder ${dataFolder.name}")
        }
        if(!config.exists()) {
            if(config.createNewFile()) {
                val defaultConfig = Configuration()
                val defaultJdbc = "jdbc:mysql://localhost:3306/votes"
                val defaultUsername = "root"
                val defaultPassword = "password"
                defaultConfig.set("jdbc", defaultJdbc)
                defaultConfig.set("username", defaultUsername)
                defaultConfig.set("password", defaultPassword)
                provider.save(defaultConfig, config)
                this.databaseConfig = DatabaseConfiguration(defaultJdbc, defaultUsername, defaultPassword)
                this.logger.info("Created default configuration file ${config.name}")
                this.logger.warning("Please edit your database settings - Password \"password\" is not secure enough.")
            }
        } else {
            val yamlConfig = provider.load(config)
            if(yamlConfig.contains("jdbc") && yamlConfig.contains("username") && yamlConfig.contains("password")) {
                this.databaseConfig = yamlConfig.getString("jdbc")?.let {
                    DatabaseConfiguration(
                        it,
                        yamlConfig.getString("username")!!,
                        yamlConfig.getString("password")!!
                    )
                }!!
            } else {
                this.logger.severe("Error whilst loading configuration file")
            }
            this.logger.info("Loaded configuration file ${config.name}")
        }
    }

    private fun initAchievementDatabaseConfig() {
        val config = File(this.dataFolder, "achievements.yml")
        val clazz =  YamlConfiguration::class.java
        val provider = ConfigurationProvider.getProvider(clazz)
        if(!this.dataFolder.exists()) {
            this.dataFolder.mkdir()
            this.logger.info("Created plugin data folder ${dataFolder.name}")
        }
        if(!config.exists()) {
            if(config.createNewFile()) {
                val defaultConfig = Configuration()
                val defaultJdbc = "jdbc:mysql://localhost:3306/achievements"
                val defaultUsername = "root"
                val defaultPassword = "password"
                defaultConfig.set("jdbc", defaultJdbc)
                defaultConfig.set("username", defaultUsername)
                defaultConfig.set("password", defaultPassword)
                provider.save(defaultConfig, config)
                this.achievementDatabaseConfig = DatabaseConfiguration(defaultJdbc, defaultUsername, defaultPassword)
                this.logger.info("Created default configuration file ${config.name}")
                this.logger.warning("Please edit your database settings - Password \"password\" is not secure enough.")
            }
        } else {
            val yamlConfig = provider.load(config)
            if(yamlConfig.contains("jdbc") && yamlConfig.contains("username") && yamlConfig.contains("password")) {
                this.achievementDatabaseConfig = yamlConfig.getString("jdbc")?.let {
                    DatabaseConfiguration(
                        it,
                        yamlConfig.getString("username")!!,
                        yamlConfig.getString("password")!!
                    )
                }!!
            } else {
                this.logger.severe("Error whilst loading configuration file")
            }
            this.logger.info("Loaded configuration file ${config.name}")
        }
    }
}
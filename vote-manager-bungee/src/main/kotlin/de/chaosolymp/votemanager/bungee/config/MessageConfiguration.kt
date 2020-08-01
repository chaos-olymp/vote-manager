package de.chaosolymp.votemanager.bungee.config

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.config.Configuration

class MessageConfiguration(private val config: Configuration) {

    companion object {
        fun getDefaultConfiguration(): Configuration {
            val config = Configuration()
            config.set("variables.prefix", "&8[&e&l!&r&8] &6&lVote&r &8» &r")
            config.set("messages.vote.default", "%prefix% &e{player} &bhat gevotet")
            config.set("messages.vote.special", "%prefix% &e{player} &dhat zum &e{votes}. &dMal gevotet")
            config.set("messages.vote.reward", "%prefix% &aDu hast für dein Vote &e{amount} Lumen &aerhalten.")

            config.set("messages.command.votes.self", "%prefix% &7Du hast aktuell &6{votes} &7und bist auf Platz &a{rank}")
            config.set("messages.command.votes.other", "%prefix% &e{player} &7hat aktuell &6{votes} &7und ist auf Platz &a{rank}")
            config.set("messages.command.top.heading", "%prefix% &eTop 10 der letzten 30 Tage:")
            config.set("messages.command.top.element", "%prefix% &8[&e{rank}&8] &a{player} &8» &6{votes} Votes")
            config.set("messages.command.vote-list", "%prefix% &eAktuelle Links:\n\n&8[&e1&8]&6 www.example.com \n&8[&e2&8]&6 www.example.com \n&8[&e3&8]&6 www.example.com \n\n")

            config.set("messages.error.no-permission", "%prefix% &cKeine Rechte")
            config.set("messages.error.no-stats", "%prefix% &cDieser Spieler ist nicht in unseren Voteaufzeichnungen verzeichnet.")
            config.set("messages.error.player-not-found", "%prefix% &cDieser Spieler konnte nicht gefunden werden.")
            config.set("messages.error.not-a-player", "%prefix% &cDu bist kein Spieler.")
            config.set("messages.error.database-error", "%prefix% &cDatenbankfehler")
            config.set("messages.error.syntax", "%prefix% &cMeintest du &o{syntax}&r&c?")

            return config
        }
    }

    fun getMessage(key: String, replacements: Array<Replacement>): Array<BaseComponent> = TextComponent.fromLegacyText(this.getLanguageElement("messages.$key", replacements), ChatColor.WHITE)
    private fun getVariable(key: String, replacements: Array<Replacement>) = this.getLanguageElement("variables.$key", replacements)
    private fun getAllVariableKeys(): MutableCollection<String>? {
        val variableSection = config.getSection("variables")
        return variableSection.keys
    }

    private fun getLanguageElement(key: String, replacements: Array<Replacement>): String {
        var string = this.config.getString(key)!!
        string = ChatColor.translateAlternateColorCodes('&', string)
        for(replacement in replacements) {
            string = string.replace("{${replacement.key}}", replacement.value.toString())
        }
        for(variable in this.getAllVariableKeys()!!) {
            if (string.contains("%$variable%")) {
                string = string.replace("%$variable%", this.getVariable(variable, emptyArray()))
            }
        }

        return string
    }


}

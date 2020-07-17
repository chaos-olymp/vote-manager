package de.chaosolymp.votemanager.bungee

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import java.util.regex.Pattern

object ComponentUtil {

    private val URL = Pattern.compile("^(?:(https?)://)?([-\\w_.]{2,}\\.[a-z]{2,4})(/\\S*)?$")

    fun fromLegacyText(message: String, color: ChatColor): Array<BaseComponent> {
        val components = mutableListOf<BaseComponent>()
        var builder = StringBuilder()
        var component = TextComponent()
        val matcher = URL.matcher(message)

        for (ind in message.indices) {
            var i = ind
            var char = message[i]
            if (char == ChatColor.COLOR_CHAR) {

                if ((i + 1) >= message.length) {
                    break
                }
                char = message[i + 1]
                if (char in 'A'..'Z') {
                    char += 32
                }

                var format: ChatColor
                if (char == 'x' && i + 12 < message.length) {
                    val hex = java.lang.StringBuilder("#")
                    for (j in 0 until 6) {
                        hex.append(i + 2 + (j * 2))
                    }

                    format = ChatColor.of(hex.toString())

                    i += 12
                } else {
                    format = ChatColor.getByChar(char)
                }
                if (format == null) {
                    continue
                }
                if (builder.isNotEmpty()) {
                    val old = component
                    component = TextComponent(old)
                    old.text = builder.toString()
                    components.add(old)
                }

                when (format) {
                    ChatColor.BOLD -> {
                        component.isBold = true
                    }
                    ChatColor.ITALIC -> {
                        component.isItalic = true
                    }
                    ChatColor.UNDERLINE -> {
                        component.isUnderlined = true
                    }
                    ChatColor.STRIKETHROUGH -> {
                        component.isStrikethrough = true
                    }
                    ChatColor.MAGIC -> {
                        component.isObfuscated = true
                    }
                    ChatColor.RESET -> {
                        format = color
                        component = TextComponent()
                        component.color = format
                    }
                    else -> {
                        component = TextComponent()
                        component.color = format
                    }
                }
                continue
            }

            var pos = message.indexOf(' ', i)
            if (pos == -1) {
                pos = message.length
            }
            if (matcher.region(i, pos).find()) {
                if (builder.isNotEmpty()) {
                    val old = component
                    component = TextComponent(old)
                    old.text = builder.toString()
                    builder = java.lang.StringBuilder()
                    components.add(old)
                }

                val old = component
                component = TextComponent(old)
                val url = message.substring(i, pos)
                component.text = url
                component.clickEvent = ClickEvent(
                    ClickEvent.Action.OPEN_URL,
                    if (url.startsWith("http") || url.startsWith("https")) url else "http://$url"
                )
                components.add(component)
                i += pos - i - 1
                component = old
                continue
            }
            builder.append(char)
        }


        component.text = builder.toString()
        components.add(component)

        return components.toTypedArray()
    }
}


package com.willfp.libreforge.integrations.vault

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigViolation
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import net.milkbowl.vault.permission.Permission
import org.bukkit.entity.Player
import java.util.*

class EffectGivePermission(
    private val handler: Permission
) : Effect("give_permission") {
    private val permissions = mutableMapOf<UUID, MutableMap<UUID, String>>()

    override fun handleEnable(
        player: Player,
        config: Config,
        identifiers: Identifiers
    ) {
        val activePermissions = permissions[player.uniqueId] ?: mutableMapOf()
        val uuid = identifiers.uuid
        val perm = config.getString("permission")

        handler.playerAdd(player, perm)

        activePermissions[uuid] = perm
        permissions[player.uniqueId] = activePermissions
    }

    override fun handleDisable(
        player: Player,
        identifiers: Identifiers
    ) {
        val activePermissions = permissions[player.uniqueId] ?: mutableMapOf()
        val uuid = identifiers.uuid
        activePermissions[uuid]?.let {
            handler.playerRemove(player, it)
        }
        activePermissions.remove(uuid)
        permissions[player.uniqueId] = activePermissions
    }

    override fun validateConfig(config: Config): List<ConfigViolation> {
        val violations = mutableListOf<ConfigViolation>()

        if (!config.has("permission")) violations.add(
            ConfigViolation(
                "permission",
                "You must specify the permission!"
            )
        )

        return violations
    }
}

package gg.obsidian.obsidianpermissions.models

import com.avaje.ebean.validation.NotNull
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "group_membership")
open class GroupMembership(
        @Id var id: Int? = null,
        @NotNull var groupName: String,
        @NotNull var playerUUID: UUID,
        @NotNull var playerName: String
) {

    open fun getId(): Int? {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getGroupName(): String {
        return groupName
    }

    fun getPlayerUUID(): UUID {
        return playerUUID
    }

    fun


}

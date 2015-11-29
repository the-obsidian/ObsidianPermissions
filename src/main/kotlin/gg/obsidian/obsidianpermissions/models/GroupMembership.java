package gg.obsidian.obsidianpermissions.models;

import com.avaje.ebean.validation.NotNull;
import java.util.*;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "group_membership")
public class GroupMembership {

    @Id
    public int id;

    @NotNull
    public String groupName;

    @NotNull
    public UUID playerUUID;

    @NotNull
    public String playerName;

    public GroupMembership() {}

    public GroupMembership(String groupName, UUID playerUUID, String playerName) {
        this.groupName = groupName;
        this.playerUUID = playerUUID;
        this.playerName = playerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}

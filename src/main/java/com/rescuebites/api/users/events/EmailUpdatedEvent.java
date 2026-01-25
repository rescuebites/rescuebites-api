package com.rescuebites.api.users.events;

import com.rescuebites.api.users.data.models.User;

import java.util.UUID;

public record EmailUpdatedEvent(
        User user,
        UUID tokenId
) {
}

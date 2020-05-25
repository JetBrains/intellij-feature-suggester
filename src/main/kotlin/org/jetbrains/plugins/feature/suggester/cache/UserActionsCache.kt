package org.jetbrains.plugins.feature.suggester.cache

import org.jetbrains.plugins.feature.suggester.changes.UserAction

class UserActionsCache(maxCacheSize: Int) : ChangedCache<UserAction>(maxCacheSize) {
}
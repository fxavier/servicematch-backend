@ApplicationModule(displayName = "notifications", allowedDependencies = {
        "servicerequests::api",
        "servicerequests::events",
        "proposals::events"
})
package com.xavier.servicematchbackend.notifications;

import org.springframework.modulith.ApplicationModule;

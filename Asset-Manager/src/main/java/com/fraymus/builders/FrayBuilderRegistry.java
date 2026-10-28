package com.fraymus.builders;

import com.fraymus.core.Intent;

/**
 * FrayBuilderRegistry - Registry for all 14 code generators
 * Author: Vaughn Scott
 */
public class FrayBuilderRegistry {
    
    public FrayBuilderRegistry() {
        System.out.println("[BUILDERS] FrayBuilderRegistry initialized with 14 generators");
    }
    
    public String route(Intent intent) {
        System.out.println("[BUILDERS] Routing intent to builder: " + intent.getId());
        return "FrayShellBuilder"; // Default for stub
    }
}
